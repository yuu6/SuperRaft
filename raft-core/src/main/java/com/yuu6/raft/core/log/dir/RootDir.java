package com.yuu6.raft.core.log.dir;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @Author: yuu6
 * @Date: 2022/04/09/下午9:04
 */
public class RootDir {
    private File baseDir;
    private String generationStr = "log-";
    private File currentGenerarionDir = null;
    private LogGeneration currentGenerarion = null;

    public RootDir(File baseDir){
        this.baseDir = baseDir;
        initialize();
    }

    public void initialize() {
        if (!baseDir.exists()){
            baseDir.mkdir();
            currentGenerarionDir = new File(baseDir.getAbsolutePath() + "/" + generationStr + 1);
            currentGenerarionDir.mkdir();
            return;
        }
        File[] generarions = baseDir.listFiles();
        Arrays.sort(generarions, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        currentGenerarionDir = generarions[generarions.length - 1];
    }

    public LogGeneration getLaststGeneration() {
        return new LogGeneration(currentGenerarionDir);
    }

    public LogGeneration createFirstGeneration() {
        return new LogGeneration(currentGenerarionDir);
    }
}
