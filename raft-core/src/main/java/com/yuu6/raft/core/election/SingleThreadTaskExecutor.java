package com.yuu6.raft.core.election;

import java.util.concurrent.*;

public class SingleThreadTaskExecutor implements TaskExecutor{
    private final ExecutorService executorService;

    public SingleThreadTaskExecutor(){
        this(Executors.defaultThreadFactory());
    }

    public SingleThreadTaskExecutor(String name){
        this(r -> new Thread(r, name));
    }

    private SingleThreadTaskExecutor(ThreadFactory threadFactory){
        executorService = Executors.newSingleThreadExecutor(threadFactory);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }


    @Override
    public <V> Future<V> submit(Callable<V> task) {
        return executorService.submit(task);
    }


    @Override
    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }
}
