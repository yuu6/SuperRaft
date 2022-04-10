package com.yuu6.log.storage;

import java.io.*;

/**
 * @Description: 随机读写文件适配器
 * @Author: yuu6
 * @Date: 2022/03/31/上午8:38
 */
public class RandomAccessFileAdpter implements SeekableFile {
    // 操作的文件
    private final File file;
    // 设配的对象
    private final RandomAccessFile randomAccessFile;

    public RandomAccessFileAdpter(File file)throws FileNotFoundException{
        this(file, "rw");
    }

    public RandomAccessFileAdpter(File file, String mode) throws FileNotFoundException{
        this.file = file;
        this.randomAccessFile = new RandomAccessFile(file, mode);
    }
    @Override
    public long position() throws IOException {
        return randomAccessFile.getFilePointer();
    }

    @Override
    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public void writeInt(int i) throws IOException {
        randomAccessFile.writeInt(i);
    }

    @Override
    public void writeLong(long l) throws IOException {
        randomAccessFile.writeLong(l);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        randomAccessFile.write(bytes);
    }

    @Override
    public int readInt() throws IOException {
        return randomAccessFile.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return randomAccessFile.readLong();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return randomAccessFile.read(b);
    }

    @Override
    public long size() throws IOException {
        return randomAccessFile.length();
    }

    @Override
    public void truncate(long size) throws IOException {
        randomAccessFile.setLength(size);
    }

    @Override
    public InputStream inputStream(long start) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        if (start > 0) {
            inputStream.skip(start);
        }
        return inputStream;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
