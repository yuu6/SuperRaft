package com.yuu6.log;

import com.yuu6.log.dir.LogDir;
import com.yuu6.log.entry.Entry;
import com.yuu6.log.storage.EntryFile;
import com.yuu6.log.storage.EntryIndexFile;
import org.apache.logging.log4j.LoggingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: yuu6
 * @Date: 2022/04/09/上午11:06
 */
public class FileEntrySequence extends AbstractEntrySequence{
    // 日志条目工厂
    private final EntryFactory entryFactory = new EntryFactory();
    // 日志条目存储文件
    private EntryFile entryFile;
    // 日志条目索引文件
    private EntryIndexFile entryIndexFile;
    // 日志条目索引
    private final LinkedList<Entry> pendingEntries = new LinkedList<>();
    // 提交的日志条目下标
    private int commitIndex = 0;

    /**
     * logIndexOffset 的初始值是1
     * @param logDir
     * @param logIndexOffset
     */
    public FileEntrySequence(LogDir logDir, int logIndexOffset){
        super(logIndexOffset);
        try {
            this.entryFile = new EntryFile(logDir.getEntryFile());
            this.entryIndexFile = new EntryIndexFile(logDir.getEntryIndexFile());
            initialize();
        }catch (Exception e){
            throw new LoggingException("fail to open entries file or entry idnex file", e);
        }
    }

    public FileEntrySequence(EntryFile entryFile,
                             EntryIndexFile entryIndexFile, int logIndexOffset){
        super(logIndexOffset);
        this.entryFile = entryFile;
        this.entryIndexFile = entryIndexFile;
        initialize();
    }

    private void initialize(){
        if (entryIndexFile.isEmpty()){
            return;
        }
        logIndexOffset = entryIndexFile.getMinEntryIndex();
        nextLogIndex = entryIndexFile.getMaxEntryIndex() + 1;
    }

    public int getCommitIndex(){
        return commitIndex;
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        List<Entry> result = new ArrayList<>();
        if (!entryIndexFile.isEmpty() && fromIndex <= entryIndexFile.getMaxEntryIndex()){
            int maxIndex = Math.min(entryIndexFile.getMaxEntryIndex() + 1, toIndex);

            for (int i= fromIndex; i< maxIndex ;i++){
                result.add(getEntryInFile(i));
            }
        }

        if (!pendingEntries.isEmpty() && toIndex > pendingEntries.getFirst().getIndex()){
            Iterator<Entry> iterator = pendingEntries.iterator();
            Entry entry;
            int index;
            while(iterator.hasNext()){
                entry = iterator.next();
                index = entry.getIndex();
                if (index >= toIndex){
                    break;
                }
                if (index >= fromIndex){
                    result.add(entry);
                }
            }
        }
        return result;
    }

    @Override
    public Entry doGetEntry(int index){
        if (!pendingEntries.isEmpty()){
            int firstPendingEntryIndex = pendingEntries.getFirst().getIndex();
            if (index >= firstPendingEntryIndex){
                return pendingEntries.get(index - firstPendingEntryIndex);
            }
        }
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(index);
    }

    public EntryMeta getEntryMeta(int index){
        if (!isEntryPresent(index)){
            return null;
        }

        if (!pendingEntries.isEmpty()){
            int firstPendingEntryIndex = pendingEntries.getFirst().getIndex();
            if (index >= firstPendingEntryIndex){
                return pendingEntries.get(index - firstPendingEntryIndex).getMeta();
            }
        }
        return getEntryInFile(index).getMeta();
    }

    private Entry getEntryInFile(int index){
        long offset = entryIndexFile.getOffset(index);
        try {
            return entryFile.loadEntry(offset, entryFactory);
        }catch (IOException e){
            throw new IllegalStateException("加载日志条目失败");
        }
    }

    public Entry getLastEntry(){
        if (isEmpty()){
            return null;
        }
        if (!pendingEntries.isEmpty()){
            return pendingEntries.getLast();
        }
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(entryIndexFile.getMaxEntryIndex());
    }

    @Override
    protected void doAppend(Entry e) {
        pendingEntries.add(e);
    }

    /**
     * 真正提交日志，就是将两个下标之间的entry写到日志中
     * @param index
     */
    @Override
    public void commit(int index) {
        if (index < commitIndex){
            throw new IllegalArgumentException("commit index < " + commitIndex);
        }

        if (index == commitIndex){
            return;
        }

        if (!entryIndexFile.isEmpty() && index <= entryIndexFile.getMaxEntryIndex()){
            commitIndex = index;
            return;
        }

        // 检查commitIndex 是否在日志缓冲的区间内
        if (pendingEntries.isEmpty() || pendingEntries.getFirst().getIndex() > index ||
            pendingEntries.getLast().getIndex() < index){
            throw new IllegalArgumentException("no entry to commit or commit index exceed");
        }

        long offset;
        Entry entry = null;
        try {
            for (int i = pendingEntries.getFirst().getIndex(); i<= index; i++){
                entry = pendingEntries.removeFirst();
                offset = entryFile.appendEntry(entry);
                entryIndexFile.appendEntryIndex(i, offset, entry.getKind(), entry.getTerm());
                commitIndex = i;
            }
        }catch (IOException e){

        }
    }

    @Override
    protected void doRemoveAfter(int index) {
        // 只需要移除缓冲中的日志
        if (!pendingEntries.isEmpty() && index >= pendingEntries.getFirst().getIndex() - 1){
            for (int i = index + 1; i <= getLastLogIndex(); i++){
                pendingEntries.removeLast();
            }
            nextLogIndex = index + 1;
            return;
        }

        try{
            if (index >= doGetFirstLogIndex()){
                pendingEntries.clear();
                entryFile.truncate(entryIndexFile.getOffset(index + 1));
                entryIndexFile.removeAfter(index);
                nextLogIndex = index + 1;
                commitIndex = index;
            }else{
                pendingEntries.clear();
                entryFile.clear();
                entryIndexFile.clear();
                nextLogIndex = logIndexOffset;
                commitIndex = logIndexOffset - 1;
            }
        }catch (Exception e){

        }
    }

    @Override
    public void close() {

    }
}
