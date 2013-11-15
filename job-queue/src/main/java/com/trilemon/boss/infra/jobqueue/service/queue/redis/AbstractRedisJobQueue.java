package com.trilemon.boss.infra.jobqueue.service.queue.redis;

import com.trilemon.boss.infra.jobqueue.model.Master;
import com.trilemon.boss.infra.jobqueue.service.queue.JobQueue;
import com.trilemon.commons.redis.JedisTemplate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 使用 redis 作为 job 存储和 master，保证单写多读
 *
 * @author kevin
 */
public abstract class AbstractRedisJobQueue<T> implements JobQueue<T> {
    private static Logger logger = LoggerFactory.getLogger(AbstractRedisJobQueue.class);
    //是否是 master
    private boolean isMaster;
    private String group;
    private String masterName;
    protected JedisTemplate jedisTemplate;
    private int expireTime = 10;//秒

    @Override
    public void start() {
        checkMaster();
    }

    /**
     * 如果 master down 掉，写队列最多会有设定缓存秒数的不可用
     */
    private void checkMaster() {
        while (true) {
            doCheckMaster();
            try {
                Thread.sleep(expireTime * 1000);
            } catch (InterruptedException e) {
                logger.error("check master error", e);
            }
        }
    }

    /**
     * 检查是否是 master
     */
    public void doCheckMaster() {
        Master master = jedisTemplate.getObj(group);
        if (null == master) {
            master = new Master();
            master.setMaster(masterName);
            master.setCreateTime(DateTime.now());
            jedisTemplate.setex(group.getBytes(), master, expireTime);
            master = jedisTemplate.getObj(group);
        }
        if (null == master || null == master.getMaster()) {
            logger.warn("group[{}] has no master.", group);
            isMaster = false;
        } else {
            isMaster = master.getMaster().equals(masterName);
        }
    }

    @Override
    public void addJob(String tag, T job) {
        if (isMaster) {
            doAddJob(tag, job);
        }
    }

    abstract void doAddJob(final String tag, final T job);

    @Override
    public void addJobs(String tag, List<T> jobs) {
        if (isMaster) {
            doAddJobs(tag, jobs);
        }
    }

    abstract void doAddJobs(final String tag, List<T> jobs);

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public JedisTemplate getJedisTemplate() {
        return jedisTemplate;
    }

    public void setJedisTemplate(JedisTemplate jedisTemplate) {
        this.jedisTemplate = jedisTemplate;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }
}
