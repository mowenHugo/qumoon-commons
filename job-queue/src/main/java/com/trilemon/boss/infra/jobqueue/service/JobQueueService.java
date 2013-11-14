package com.trilemon.boss.infra.jobqueue.service;

import java.util.List;

/**
 * @author kevin
 */
public interface JobQueueService<T> {
    void init();

    T getJob(String tag);

    void addJob(String tag, T job);

    void addJobs(String tag, List<T> jobs);
}
