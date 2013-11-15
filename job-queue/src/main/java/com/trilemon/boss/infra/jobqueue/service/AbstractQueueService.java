package com.trilemon.boss.infra.jobqueue.service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.trilemon.commons.BlockingThreadPoolExecutor;
import com.trilemon.commons.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 */
public abstract class AbstractQueueService<E extends Comparable<E>> implements QueueService<E> {
    private static Logger logger = LoggerFactory.getLogger(AbstractQueueService.class);
    private PriorityQueue<E> queue = Queues.newPriorityQueue();
    private BlockingThreadPoolExecutor taskPool = new BlockingThreadPoolExecutor(5);
    private BlockingThreadPoolExecutor pool = new BlockingThreadPoolExecutor(1);
    private Map<String, ThreadPoolExecutor> threadPoolExecutorMap = Maps.newHashMap();
    private int pollRoundCount = 0;
    private boolean init = true;

    @Override
    public void startPoll() {
        threadPoolExecutorMap.put(getClass().getSimpleName() + "-task", taskPool);
        threadPoolExecutorMap.put(getClass().getSimpleName() + "-poll", pool);

        pool.submit(new Runnable() {
            @Override
            public void run() {
                pollProcess();
            }
        });
    }

    @Override
    public void pollProcess() {
        Stopwatch stopwatch = new Stopwatch().start();
        while (true) {
            final E e = queue.poll();
            if (null == e) {
                if (!init) {
                    logger.info("end [{}] poll,spend [{}] seconds.", pollRoundCount,
                            stopwatch.elapsed(TimeUnit.SECONDS));
                    Threads.sleep(1, TimeUnit.MINUTES);
                } else {
                    init = false;
                }
                pollRoundCount++;
                stopwatch.reset();
                logger.info("start [{}] poll.", pollRoundCount);
                fillQueue();
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

    public Map<String, ThreadPoolExecutor> getThreadPoolExecutorMap() {
        return threadPoolExecutorMap;
    }
}
