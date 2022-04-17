package com.yuu6.raft.core.log.storage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description: 和日志文件不同的是，这个索引文件需要加载到内存中
 * @Author: yuu6
 * @Date: 2022/03/31/下午11:04
 */
public class EntryIndexFile implements Iterable<EntryIndexItem> {
    public static final long OFFSET_MAX_ENTRY_INDEX = Integer.BYTES;
    public static final int LENGTH_ENTRY_INDEX_TERM = 16;

    public final SeekableFile seekableFile;
    private int entryIndexCount; // 日志条目数
    private int minEntryIndex;
    private int maxEntryIndex;
    // 日志索引map
    private Map<Integer, EntryIndexItem> entryIndexItemMap = new HashMap<>();

    public EntryIndexFile(File file) throws IOException{
        this(new RandomAccessFileAdpter(file));
    }

    public EntryIndexFile(SeekableFile seekableFile) throws IOException{
        this.seekableFile = seekableFile;
        load();
    }


    public int getMinEntryIndex() {
        return minEntryIndex;
    }

    public int getMaxEntryIndex() {
        return maxEntryIndex;
    }

    /**
     * 加载日志索引文件
     * @throws IOException
     */
    private void load() throws IOException{
        if (seekableFile.size() == 0L) {
            entryIndexCount = 0;
            return;
        }
        minEntryIndex = seekableFile.readInt();
        maxEntryIndex = seekableFile.readInt();
        updateEntryIndexCount();
        long offset;
        int kind;
        int term;
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            offset = seekableFile.readLong();
            kind = seekableFile.readInt();
            term = seekableFile.readInt();
            entryIndexItemMap.put(i, new EntryIndexItem(i, offset, kind, term));
        }
    }

    private void updateEntryIndexCount(){
        entryIndexCount = maxEntryIndex - minEntryIndex + 1;
    }


    public void appendEntryIndex(int index, long offset, int kind, int term) throws IOException{
        // 首先根据日志索引条目下标判断要不要写入最小下标
        if (seekableFile.size() == 0L){
            seekableFile.writeInt(index);
            minEntryIndex = index;
        } else {
            if (index != maxEntryIndex + 1){
                throw new IllegalArgumentException("index must be " + (maxEntryIndex + 1) + "but was" + index);
            }
        }
        // 更新最大的日志索引下标
        seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);
        seekableFile.writeInt(index);
        maxEntryIndex = index;

        updateEntryIndexCount();
        seekableFile.seek(getOffsetOfEntryIndexItem(index));
        seekableFile.writeLong(offset);
        seekableFile.writeInt(kind);
        seekableFile.writeInt(term);
        entryIndexItemMap.put(index, new EntryIndexItem(index, offset, kind, term));
    }

    private long getOffsetOfEntryIndexItem(int index){
        // 根据下标获得offset
        return (index - minEntryIndex) * LENGTH_ENTRY_INDEX_TERM  + Integer.BYTES * 2;
    }

    public void clear() throws IOException{
        seekableFile.truncate(0L);
        entryIndexCount = 0;
        entryIndexItemMap.clear();
    }

    public void removeAfter(int newMaxEntryIndex)throws IOException{
        if (isEmpty() || newMaxEntryIndex >= maxEntryIndex){
            return;
        }

        if (newMaxEntryIndex < minEntryIndex){
            clear();
            return;
        }

        seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);
        seekableFile.writeInt(newMaxEntryIndex);

        seekableFile.truncate(getOffsetOfEntryIndexItem(newMaxEntryIndex + 1));

        for (int i = newMaxEntryIndex + 1; i < maxEntryIndex; i++) {
            entryIndexItemMap.remove(i);
        }

        maxEntryIndex = newMaxEntryIndex;
        entryIndexCount = maxEntryIndex - minEntryIndex + 1;
    }

    @Override
    public Iterator<EntryIndexItem> iterator(){
        if (isEmpty()){
            return Collections.emptyIterator();
        }
        return new EntryIndexIterator(entryIndexCount, minEntryIndex);
    }


    public boolean isEmpty(){
        try {
            return seekableFile.size() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public long getOffset(int index) {
        return getOffsetOfEntryIndexItem(index);
    }

    public class EntryIndexIterator implements Iterator<EntryIndexItem> {
        private int currentEntryIndex;
        final private int entryIndexCount;

        public EntryIndexIterator(int entryIndexCount, int minEntryIndex) {
            this.currentEntryIndex = minEntryIndex;
            this.entryIndexCount = entryIndexCount;
        }

        @Override
        public boolean hasNext() {
            return currentEntryIndex <= maxEntryIndex;
        }

        @Override
        public EntryIndexItem next() {
            checkModification();
            return entryIndexItemMap.get(currentEntryIndex ++ );
        }

        private void checkModification() {

        }
    }

}
