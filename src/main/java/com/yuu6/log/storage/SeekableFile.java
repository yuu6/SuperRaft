package com.yuu6.log.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: yuu6
 * @Date: 2022/03/31/上午8:32
 */
public interface SeekableFile {
    // 获取当前位置
    long position() throws IOException;
    // 跳到目标位置
    void seek(long position) throws IOException;
    // 在指定位置写int类型的值
    void writeInt(int i) throws IOException;
    // 在指定位置写long类型的值
    void writeLong(long l) throws IOException;
    // 写入二进制
    void write(byte[] bytes)throws IOException;
    // 读取int值
    int readInt() throws IOException;
    // 读取long值
    long readLong() throws IOException;
    // 读字节数组
    int read(byte[] b) throws IOException;
    // 大小
    long size() throws IOException;
    // 从指定位置舍弃
    void truncate(long size) throws IOException;

    InputStream inputStream(long start) throws IOException;
    // 刷新
    void flush() throws IOException;
    // 关闭
    void close() throws IOException;
}
