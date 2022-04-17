package com.yuu6.raft.core.log;

import com.yuu6.raft.core.log.dir.LogGeneration;
import com.yuu6.raft.core.log.dir.RootDir;

import java.io.File;

/**
 * @Author: yuu6
 * @Date: 2022/04/09/下午6:26
 */
public class FileLog extends AbstractLog{
    private final RootDir rootDir;

    public FileLog(File baseDir){
        rootDir = new RootDir(baseDir);

        LogGeneration lastestGeneration = rootDir.getLaststGeneration();
        if (lastestGeneration != null){
            // 日志存在
            entrySequence = new FileEntrySequence(
                    lastestGeneration, lastestGeneration.getLogIndexOffset()
            );
        }else{
            LogGeneration firstGeneration = rootDir.createFirstGeneration();
            entrySequence = new FileEntrySequence(firstGeneration, 1);
        }
    }
}
