package com.trilemon.jobqueue.service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.trilemon.commons.BlockingThreadPoolExecutor;
import com.trilemon.commons.Threads;
import com.trilemon.jobqueue.service.queue.JobQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author kevin
 */
public abstract class AbstractRedisQueueService<E> implements QueueService<E> {
    private static Logger logger = LoggerFactory.getLogger(AbstractRedisQueueService.class);
    private BlockingThreadPoolExecutor taskPool ;
    private BlockingThreadPoolExecutor writeThread= new BlockingThreadPoolExecutor(1);
    private JobQueue<E> jobQueue;
    private Map<String, ThreadPoolExecutor> threadPoolExecutorMap = Maps.newHashMap();
    private int pollRoundCount = 0;
    private boolean init = true;
    private String tag;
    private int taskNum=5;
    private int sleepMinutes=1;

    @Override
    public void start() {
        checkNotNull("tag can not be null.",tag);
        taskPool=new BlockingThreadPoolExecutor(taskNum);
        threadPoolExecutorMap.put(getClass().getSimpleName() + "-task", taskPool);

        writeThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    getAndProcess();
                } catch (Throwable throwable) {
                    logger.error("getAndProcess thread error.", throwable);
                }
            }
        });

        logger.info("start task thread[{}]",taskPool);
    }

    @Override
    public void reboot() {
        //do nothing
    }

    @Override
    public void timeout() {
        //do nothing
    }

    @Override
    public void getAndProcess() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (true) {
            final E e = jobQueue.getJob(tag);
            if (null == e) {
                if (!init) {
                    logger.info("end [{}] process round, spend [{}] seconds.", pollRoundCount,
                            stopwatch.elapsed(TimeUnit.SECONDS));
                    Threads.sleep(sleepMinutes, TimeUnit.MINUTES);
                } else {
                    init = false;
                }
                pollRoundCount++;
                stopwatch.reset();
                logger.info("start [{}] process round.", pollRoundCount);
                fillQueue();
            } else {
                taskPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            process(e);
                        } catch (Throwable throwable) {
                            logger.error("process error.", throwable);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void fillQueue(E e) {
        jobQueue.addJob(tag, e);
    }

    @Override
    public void fillQueue(List<E> elemList) {
        jobQueue.addJobs(tag, elemList);
    }

    public Map<String, ThreadPoolExecutor> getThreadPoolExecutorMap() {
        return threadPoolExecutorMap;
    }

    public BlockingThreadPoolExecutor getTaskPool() {
        return taskPool;
    }

    public void setTaskPool(BlockingThreadPoolExecutor taskPool) {
        this.taskPool = taskPool;
    }


    public JobQueue<E> getJobQueue() {
        return jobQueue;
    }

    public void setThreadPoolExecutorMap(Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    public int getPollRoundCount() {
        return pollRoundCount;
    }

    public void setPollRoundCount(int pollRoundCount) {
        this.pollRoundCount = pollRoundCount;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getSleepMinutes() {
        return sleepMinutes;
    }

    public void setSleepMinutes(int sleepMinutes) {
        this.sleepMinutes = sleepMinutes;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public void setJobQueue(JobQueue<E> jobQueue) {
        this.jobQueue = jobQueue;
    }
}
