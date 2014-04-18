package com.qumoon.jobqueue.service;

import com.google.common.base.Preconditions;

import it.sauronsoftware.cron4j.Scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kevin
 */
public abstract class AbstractCronQueueService<E> extends AbstractBlockingQueueService<E> {

  private static Logger logger = LoggerFactory.getLogger(Thread.currentThread().getClass());
  private Scheduler scheduler = new Scheduler();
  private String cron;

  @Override
  protected void startAdd() {
    Preconditions.checkNotNull(cron, "cron must not be null");

    scheduler.schedule(cron, new Runnable() {
      public void run() {
        triggerAdd();
      }
    });
    logger.info("started add schedule, cron[{}]", cron);
  }

  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }
}
