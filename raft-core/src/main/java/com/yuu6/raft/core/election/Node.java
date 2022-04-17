package com.yuu6.raft.core.election;

public interface Node {
    void start();

    void stop() throws InterruptedException;
}
