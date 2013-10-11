package com.trilemon.commons.service;

import com.google.common.collect.Queues;
import com.trilemon.commons.BlockingThreadPoolExecutor;
import com.trilemon.commons.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 */
public abstract class AbstractQueueService<E> implements QueueService<E> {
    private static Logger logger = LoggerFactory.getLogger(AbstractQueueService.class);
    private PriorityQueue<E> queue = Queues.newPriorityQueue();
    private BlockingThreadPoolExecutor taskPool = new BlockingThreadPoolExecutor(5);
    private BlockingThreadPoolExecutor pool = new BlockingThreadPoolExecutor(1);

    @Override
    public void startPoll() {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                pollProcess();
            }
        });
    }

    @Override
    public void pollProcess() {
        while (true) {
            final E e = queue.poll();
            if (null == e) {
                fillQueue();
                if (queue.isEmpty()) {
                    Threads.sleep(1, TimeUnit.MINUTES);
                }
            } else {
                taskPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            process(e);
                        } catch (Throwable throwable) {
                            logger.error("process error.", throwable);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void fillQueue(E e) {
        queue.add(e);
    }

    @Override
    public void fillQueue(Collection<E> elemList) {
        queue.addAll(elemList);
    }

    @Override
    public void reboot() {
        queue.clear();
    }

    public PriorityQueue<E> getQueue() {
        return queue;
    }
}
