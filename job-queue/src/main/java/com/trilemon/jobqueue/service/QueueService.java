package com.trilemon.jobqueue.service;

import java.util.List;

/**
 * @author kevin
 */
public interface QueueService<E> {

    void start();

    void reboot();

    void timeout();

    void getAndProcess();

    void process(E e) throws Exception;

    void fillQueue();

    void fillQueue(E e);

    void fillQueue(List<E> elemList);
}
