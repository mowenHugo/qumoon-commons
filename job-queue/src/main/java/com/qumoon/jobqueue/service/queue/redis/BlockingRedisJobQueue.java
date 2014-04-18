package com.qumoon.jobqueue.service.queue.redis;

import com.google.common.collect.Lists;

import com.qumoon.commons.redis.JedisTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import java.util.List;

import redis.clients.jedis.Jedis;

/**
 * 普通队列，FIFO
 *
 * @author kevin
 */
public class BlockingRedisJobQueue<T> extends AbstractRedisJobQueue<T> {

  private static Logger logger = LoggerFactory.getLogger(BlockingRedisJobQueue.class);

  @Override
  public T getJob(final String tag) {
    return jedisTemplate.execute(new JedisTemplate.JedisAction<T>() {
      @Override
      public T action(Jedis jedis) {
        List<byte[]> result = jedis.blpop(0, tag.getBytes());
        return (T) SerializationUtils.deserialize(result.get(1));
      }
    });
  }

  public void doAddJob(final String tag, final T job) {
    jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
      @Override
      public Long action(Jedis jedis) {
        return jedis.rpush(tag.getBytes(), SerializationUtils.serialize(job));
      }
    });
  }

  public void doAddJobs(final String tag, List<T> jobs) {
    final List<byte[]> byteOfJobs = Lists.newArrayList();
    for (T job : jobs) {
      byteOfJobs.add(SerializationUtils.serialize(job));
    }
    jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
      @Override
      public Long action(Jedis jedis) {
        return jedis.rpush(tag.getBytes(), byteOfJobs.toArray(new byte[0][]));
      }
    });
  }
}
