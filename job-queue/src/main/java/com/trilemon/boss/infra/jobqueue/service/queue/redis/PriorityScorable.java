package com.trilemon.boss.infra.jobqueue.service.queue.redis;

/**
 * @author kevin
 */
public interface PriorityScorable {
    double getScore();
}
