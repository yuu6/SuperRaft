package com.yuu6.raft.core.log.dir;

import java.io.File;

/**
 * @Author: yuu6
 * @Date: 2022/04/09/上午11:10
 */
public interface LogDir {
    // 初始化目录
    void initialize();
    // 是否存在
    boolean exists();
    // 获取 EntriesFile 对应的文件
    public File getEntryFile();
    // 获取 EntryIndexFile 对应的文件
    public File getEntryIndexFile();

    // 获取目录
    File get();
    // 重命名目录
    boolean renameTo(LogDir logDir);
}
