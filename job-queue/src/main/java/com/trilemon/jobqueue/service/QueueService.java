package com.trilemon.jobqueue.service;

import java.util.List;

/**
 * @author kevin
 */
public interface QueueService<E> {

    void start();

    /**
     * 正式启动队列前需要做的工作
     */
    void clean();

    void timeout();

    /**
     * 处理一个元素
     * @param e
     * @throws Exception
     */
    void process(E e) throws Exception;

    /**
     * 填充队列
     */
    void fillQueue();

    void fillQueue(E e);

    void fillQueue(List<E> elemList);

    /**
     *处理非阻塞队列的钩子
     */
    void pollNull();
}
