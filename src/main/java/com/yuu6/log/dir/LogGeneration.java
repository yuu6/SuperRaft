package com.yuu6.log.dir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @Author: yuu6
 * @Date: 2022/04/09/下午9:36
 */
public class LogGeneration implements LogDir{
    private String entryFileStr = "entries.bin";
    private String entryIndexFileStr = "entries.idx";
    private File entryFile;
    private File entryIndexFile;
    private File logDir;

    public LogGeneration(File fileDir) {
        this.logDir = fileDir;
        initialize();
    }

    public int getLogIndexOffset() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(entryFile, "rw");
            return (int) randomAccessFile.length();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void initialize() {
        String entryFilePath = logDir.getPath()+ "/" + entryFileStr;
        String entryIndexFilePath = logDir.getPath()+ "/" + entryIndexFileStr;
        entryFile = new File(entryFilePath);
        entryIndexFile = new File(entryIndexFilePath);
        try {
            if (!entryFile.exists()){
                entryFile.createNewFile();
                entryIndexFile.createNewFile();
            }
        }catch (IOException e){

        }
    }

    @Override
    public boolean exists() {
        return logDir.exists();
    }

    @Override
    public File getEntryFile() {
        return entryFile;
    }

    @Override
    public File getEntryIndexFile() {
        return entryIndexFile;
    }

    @Override
    public File get() {
        return logDir;
    }

    @Override
    public boolean renameTo(LogDir logDir) {
        return false;
    }
}
