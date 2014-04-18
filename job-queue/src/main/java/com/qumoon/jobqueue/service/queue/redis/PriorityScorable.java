package com.qumoon.jobqueue.service.queue.redis;

/**
 * @author kevin
 */
public interface PriorityScorable {

  double getScore();
}
