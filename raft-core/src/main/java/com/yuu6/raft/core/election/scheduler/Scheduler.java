package com.yuu6.raft.core.election.scheduler;

import com.yuu6.raft.core.election.ElectionTimeout;
import com.yuu6.raft.core.election.LogReplicationTask;

public interface Scheduler {
    // 复制日志的定时器
    LogReplicationTask scheduleLogReplicationTask(Runnable task);
    // 选举超时定时器
    ElectionTimeout scheduleElectionTimeout(Runnable task);

    void stop() throws InterruptedException;
}
