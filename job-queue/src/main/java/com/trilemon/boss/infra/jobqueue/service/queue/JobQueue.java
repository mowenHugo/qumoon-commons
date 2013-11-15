package com.trilemon.boss.infra.jobqueue.service.queue;

import java.util.List;

/**
 * @author kevin
 */
public interface JobQueue<T> {
    void start();

    T getJob(String tag);

    void addJob(String tag, T job);

    void addJobs(String tag, List<T> jobs);
}
