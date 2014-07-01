package com.qumoon.jobqueue.service.queue;

import java.util.List;

/**
 * @author kevin
 */
public interface Queue<T> {

  T take() throws InterruptedException;

  void add(T value);

  void addAll(List<T> values);

  String getQueueName();
}
