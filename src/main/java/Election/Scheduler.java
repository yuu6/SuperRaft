package Election;

public interface Scheduler {
    LogReplicationTask scheduleLogReplicationTask(Runnable task);
    ElectionTimeout scheduleElectionTimeout(Runnable task);

    void stop() throws InterruptedException;

}
