package com.yuu6.log.storage;

import com.yuu6.log.EntryFactory;
import com.yuu6.log.entry.Entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Description: 日志文件基本操作
 * @Author: yuu6
 * @Date: 2022/03/31/下午10:36
 */
public class EntryFile {
    private final SeekableFile seekableFile;

    public EntryFile(File file)throws FileNotFoundException{
        this(new RandomAccessFileAdpter(file));
    }

    // 构造函数 seekableFile
    public EntryFile(SeekableFile seekableFile){
        this.seekableFile = seekableFile;
    }

    /**
     * 写入日志条目，每一个日志条目分为五部分：kind, index, term, command.length, command
     * @param entry
     * @return
     * @throws IOException
     */
    public long appendEntry(Entry entry) throws IOException{
        long offset = seekableFile.size();
        seekableFile.seek(offset);

        byte[] commandBytes = entry.getCommandBytes();
        seekableFile.writeInt(entry.getKind());
        seekableFile.writeInt(entry.getIndex());
        seekableFile.writeInt(entry.getTerm());
        seekableFile.writeInt(commandBytes.length);
        seekableFile.write(commandBytes);
        return offset;
    }

    public Entry loadEntry(long offset, EntryFactory factory) throws IOException{
        if (offset > seekableFile.size()){
            throw new IllegalArgumentException("offset > size");
        }
        seekableFile.seek(offset);
        int kind = seekableFile.readInt();
        int index = seekableFile.readInt();
        int term = seekableFile.readInt();
        int length = seekableFile.readInt();
        byte[] bytes = new byte[length];
        seekableFile.read(bytes);

        return factory.create(kind, index,term, bytes);
    }

    public long size() throws IOException{
        return seekableFile.size();
    }

    public  void clear() throws IOException{
        truncate(0L);
    }

    public void truncate(long offset) throws IOException{
        seekableFile.truncate(offset);
    }

    public void close()throws IOException{
        seekableFile.close();
    }

}
