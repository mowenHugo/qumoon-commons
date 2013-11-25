package com.trilemon.jobqueue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 */
public abstract class AbstractFixRateQueueService<E> extends AbstractBlockingQueueService<E> {
    private static Logger logger = LoggerFactory.getLogger(Thread.currentThread().getClass());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int fixSeconds = 0;
    private int delaySeconds = 0;

    @Override
    protected void startAdd() {
        if (delaySeconds <= 0) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    triggerAdd();
                }
            }, 0, fixSeconds, TimeUnit.SECONDS);
        } else {
            scheduler.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    triggerAdd();
                }
            }, fixSeconds, delaySeconds, TimeUnit.SECONDS);
        }
        logger.info("started add schedule, fixSeconds[{}] delaySeconds[{}]", fixSeconds, delaySeconds);
    }

    public int getFixSeconds() {
        return fixSeconds;
    }

    public void setFixSeconds(int fixSeconds) {
        this.fixSeconds = fixSeconds;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }
}
