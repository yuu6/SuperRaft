package Election;

import java.util.Random;
import java.util.concurrent.*;

public class DefaultScheduler implements Scheduler{

    private final int minElectionTimeout;

    private final int maxElectionTimeout;

    private final int logReplicationDelay;

    private final int logReplicationInterval;

    private final Random electionTimeoutRandom;

    private final ScheduledExecutorService scheduledExecutorService;

    public DefaultScheduler(
            int minElectionTimeout, int maxElectionTimeout,
            int logReplicationDelay, int logReplicationInterval
    ){
        if (minElectionTimeout <= 0 || maxElectionTimeout <= 0 ||
            minElectionTimeout > maxElectionTimeout){
            throw new IllegalArgumentException(
                    "election timeout should not be 0 or min > max"
            );
        }

        if (logReplicationDelay < 0 || logReplicationInterval <= 0){
            throw new IllegalArgumentException(
                    "log replication delay <0 or logReplicationInterval <= 0"
            );
        }
        this.minElectionTimeout = minElectionTimeout;
        this.maxElectionTimeout = maxElectionTimeout;
        this.logReplicationDelay = logReplicationDelay;
        this.logReplicationInterval = logReplicationInterval;
        electionTimeoutRandom = new Random();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "scheduler")
        );
    }

    @Override
    public LogReplicationTask scheduleLogReplicationTask(Runnable task) {
        return null;
    }

    @Override
    public ElectionTimeout scheduleElectionTimeout(Runnable task) {
        // 随机超时时间
        int timeout = electionTimeoutRandom.nextInt(
                maxElectionTimeout - minElectionTimeout
        ) + minElectionTimeout;
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(
                task, timeout, TimeUnit.MICROSECONDS
        );
        return new ElectionTimeout(scheduledFuture);
    }

    @Override
    public void stop() throws InterruptedException {
    }
}
