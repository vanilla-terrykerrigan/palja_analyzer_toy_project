package com.chancrawler.chancrawler.tools.messaging;

import java.util.List;

public class PageParserWorker<K,V,T extends RedisTask> extends RedisWorker<K,V,T> {

    private final RedisPublisher redisPublisher;

    public PageParserWorker(Class<T> taskClass, final int MaxWorkerNumber, final RedisPublisher redisPublisher) {
        super(taskClass, MaxWorkerNumber);
        this.redisPublisher = redisPublisher;
    }

    @Override
    void workerHandler(K channel, V message) {
        while(true) {
            for (int i = 0; i < maxWorkerNumber; i++) {
                if (workers.get(i) == null || workers.get(i).getState() == Thread.State.NEW  || workers.get(i).getState() == Thread.State.TERMINATED) {
                    try {
                        workers.set(i, (T) taskClass.getDeclaredConstructor(String.class, String.class, RedisPublisher.class).newInstance(channel, message, redisPublisher));
                        workers.get(i).start();
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException("instance cannot created");
                    }
                }
            }
        }
    }
}