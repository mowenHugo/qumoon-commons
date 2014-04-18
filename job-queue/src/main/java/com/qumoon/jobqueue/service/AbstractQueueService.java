package com.qumoon.jobqueue.service;

import com.google.common.collect.Maps;

import com.qumoon.commons.BlockingThreadPoolExecutor;
import com.qumoon.jobqueue.service.queue.JobQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author kevin
 */
public abstract class AbstractQueueService<E> implements QueueService<E> {

  private static Logger logger = LoggerFactory.getLogger(Thread.currentThread().getClass());
  private BlockingThreadPoolExecutor pollWrapThreads = new BlockingThreadPoolExecutor(1);
  private BlockingThreadPoolExecutor pollThreads;
  private JobQueue<E> jobQueue;
  private Map<String, ThreadPoolExecutor> threadPoolExecutorMap = Maps.newHashMap();
  private int pollRoundCount = 0;
  private boolean init = true;
  private String tag;
  private int pollNum = 5;

  @Override
  public void start() {
    checkNotNull("tag can not be null.", tag);
    pollThreads = new BlockingThreadPoolExecutor(pollNum);
    threadPoolExecutorMap.put(getClass().getSimpleName() + "-poll", pollThreads);

    //clean
    clean();

    startPoll();
    startAdd();

    logger.info("start task thread[{}]", pollThreads);
  }

  protected void startPoll() {
    pollWrapThreads.submit(new Runnable() {
      @Override
      public void run() {
        triggerPoll();
      }
    });
    logger.info("started poll");
  }

  /**
   * 启动写队列线程
   */
  protected abstract void startAdd();

  protected void triggerAdd() {
    try {
      fillQueue();
    } catch (Throwable throwable) {
      logger.error("getAndProcess thread error.", throwable);
    }
  }

  @Override
  public void clean() {
    //do nothing
  }

  @Override
  public void timeout() {
    logger.info("this method do nothing now, do not implement it.");
  }

  public void triggerPoll() {
    while (true) {
      try {
        final E e = jobQueue.getJob(tag);
        if (null != e) {
          pollThreads.submit(new Runnable() {
            @Override
            public void run() {
              try {
                process(e);
              } catch (Throwable throwable) {
                logger.error("process error.", throwable);
              }
            }
          });
        } else {
          pollNull();
        }
      } catch (Throwable e) {
        logger.error("getAndProcess error", e);
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

  public void setThreadPoolExecutorMap(Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
    this.threadPoolExecutorMap = threadPoolExecutorMap;
  }

  public BlockingThreadPoolExecutor getPollThreads() {
    return pollThreads;
  }

  public void setPollThreads(BlockingThreadPoolExecutor pollThreads) {
    this.pollThreads = pollThreads;
  }

  public JobQueue<E> getJobQueue() {
    return jobQueue;
  }

  public void setJobQueue(JobQueue<E> jobQueue) {
    this.jobQueue = jobQueue;
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

  public int getPollNum() {
    return pollNum;
  }

  public void setPollNum(int pollNum) {
    this.pollNum = pollNum;
  }
}
