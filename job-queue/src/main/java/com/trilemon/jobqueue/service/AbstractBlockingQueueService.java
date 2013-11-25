package com.trilemon.jobqueue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kevin
 */
public abstract class AbstractBlockingQueueService<E> extends AbstractQueueService<E>{
    private static Logger logger = LoggerFactory.getLogger(Thread.currentThread().getClass());
    @Override
    public void pollNull() {
        logger.warn("i am blocking queue, its message should not be seen.");
    }
}
