package com.qumoon.jobqueue.service.queue.redis;

import com.qumoon.commons.BlockingThreadPoolExecutor;
import com.qumoon.jobqueue.model.Master;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 使用 redis 作为 job 存储和 master，保证单写多读
 *
 * @author kevin
 */
public abstract class AbstractRedisQueue<T> extends RedisQueue<T> {

  private static Logger logger = LoggerFactory.getLogger(AbstractRedisQueue.class);
  protected RedisTemplate redisTemplate;
  //是否是 master
  private boolean isMaster = false;
  private String group;
  private String masterName;
  private int expireTime = 10;//秒
  private BlockingThreadPoolExecutor checkMasterThread = new BlockingThreadPoolExecutor(1);

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    checkMasterThread.submit(new Runnable() {
      @Override
      public void run() {
        checkMaster();
      }
    });
    logger.info("start master check thread.");
  }

  /**
   * 如果 master down 掉，写队列最多会有设定缓存秒数的不可用
   */
  private void checkMaster() {
    while (true) {
      try {
        doCheckMaster();
        try {
          Thread.sleep(expireTime * 1000);
        } catch (InterruptedException e) {
          logger.error("check master error", e);
        }
      } catch (Throwable e) {
        logger.error("check master error", e);
      }
    }
  }

  /**
   * 检查是否是 master
   */
  public void doCheckMaster() {
    boolean prevMaster = isMaster;
    BoundValueOperations<String, Master> masterOperations = redisTemplate.boundValueOps(group);
    Master master = masterOperations.get();
    if (null == master) {
      master = new Master();
      master.setMaster(masterName);
      master.setCreateTime(DateTime.now());
      masterOperations.set(master, expireTime, TimeUnit.SECONDS);
      master = masterOperations.get();
    }

    if (null == master || null == master.getMaster()) {
      logger.warn("group[{}] has no master.", group);
      isMaster = false;
    } else {
      isMaster = master.getMaster().equals(masterName);
    }
    if (prevMaster != isMaster) {
      if (isMaster) {
        logger.info("election master: group[{}] master[{}]", group, ToStringBuilder.reflectionToString(master));
      } else {
        logger.info("remove master: group[{}] master[{}]", group, ToStringBuilder.reflectionToString(master));
      }
    }
  }

  @Override
  public void add(T value) {
    if (isMaster) {
      super.add(value);
    }
  }

  @Override
  public void addAll(List<T> values) {
    if (isMaster) {
      super.addAll(values);
    }
  }

  public boolean isMaster() {
    return isMaster;
  }

  public void setMaster(boolean master) {
    isMaster = master;
  }

  public String getMasterName() {
    return masterName;
  }

  public void setMasterName(String masterName) {
    this.masterName = masterName;
  }

  public int getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(int expireTime) {
    this.expireTime = expireTime;
  }
}
