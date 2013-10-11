package com.trilemon.commons.service;

import java.util.Collection;
import java.util.Queue;

/**
 * @author kevin
 */
public interface QueueService<E> {

    Byte LOCK = 1;
    Byte UNLOCK = 0;

    void startPoll();

    void reboot();

    void timeout();

    void pollProcess();

    void process(E e) throws Exception;

    void fillQueue();

    void fillQueue(E e);

    void fillQueue(Collection<E> elemList);

    Queue<E> getQueue();
}
