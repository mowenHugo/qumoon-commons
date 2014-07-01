package com.qumoon.jobqueue.service.queue.redis;

/**
 * @author kevin
 */
public interface RedisQueueListener<T> {

  public void onMessage(T value);
}