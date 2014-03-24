package com.qumoon.commons.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * JedisTemplate 提供了一个template方法，负责对Jedis连接的获取与归还。 JedisAction<T> 和 JedisActionNoResult两种回调接口，适用于有无返回值两种情况。
 * 同时提供一些最常用函数的封装, 如get/set/zadd等。
 */
public class JedisTemplate {
    private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

    private JedisPool jedisPool;

    public JedisTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 执行有返回结果的action。
     */
    public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            return jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * 执行无返回结果的action。
     */
    public void execute(JedisActionNoResult jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * 根据连接是否已中断的标志，分别调用returnBrokenResource或returnResource。
     */
    protected void closeResource(Jedis jedis, boolean connectionBroken) {
        if (jedis != null) {
            try {
                if (connectionBroken) {
                    jedisPool.returnBrokenResource(jedis);
                } else {
                    jedisPool.returnResource(jedis);
                }
            } catch (Exception e) {
                logger.error("Error happen when return jedis to pool, try to close it directly.", e);
                JedisUtils.closeJedis(jedis);
            }
        }
    }

    /**
     * 获取内部的pool做进一步的动作。
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public Set<String> keys(final String keyPattern) {
        return execute(new JedisAction<Set<String>>() {

            @Override
            public Set<String> action(Jedis jedis) {
                return jedis.keys(keyPattern);
            }
        });
    }
    public Set<byte[]> keys(final byte[] keyPattern) {
        return execute(new JedisAction<Set<byte[]>>() {

            @Override
            public Set<byte[]> action(Jedis jedis) {
                return jedis.keys(keyPattern);
            }
        });
    }

    /**
     * 有返回结果的回调接口定义。
     */
    public interface JedisAction<T> {
        T action(Jedis jedis);
    }

    /**
     * 无返回结果的回调接口定义。
     */
    public interface JedisActionNoResult {
        void action(Jedis jedis);
    }

    // ////////////// 常用方法的封装 ///////////////////////// //

    // ////////////// 公共 ///////////////////////////
    /**
     * 删除key, 如果key存在返回true, 否则返回false。
     */
    public boolean del(final String key) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.del(key) == 1 ? true : false;
            }
        });
    }

    public boolean del(final byte[] key) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.del(key) == 1 ? true : false;
            }
        });
    }

    public void flushDB() {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.flushDB();
            }
        });
    }

    // ////////////// 关于String ///////////////////////////
    /**
     * 如果key不存在, 返回null.
     */
    public String get(final String key) {
        return execute(new JedisAction<String>() {

            @Override
            public String action(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    public <T> T getObj(final String key) {
        return execute(new JedisAction<T>() {
            @Override
            public T action(Jedis jedis) {
                byte[] result= jedis.get(key.getBytes());
                return (T)SerializationUtils.deserialize(result);
            }
        });
    }

    public <T> T getAndSetObj(final String key,final T t, final int seconds) {
        return execute(new JedisAction<T>() {
            @Override
            public T action(Jedis jedis) {
                byte[] result= jedis.get(key.getBytes());
                if(null==result){
                    setex(key.getBytes(),t,seconds);
                    return t;
                }else{
                    return (T)SerializationUtils.deserialize(result);
                }

            }
        });
    }


    public byte[] get(final byte[] key) {
        return execute(new JedisAction<byte[]>() {

            @Override
            public byte[] action(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    /**
     * 如果key不存在, 返回0.
     */
    public Long getAsLong(final String key) {
        String result = get(key);
        return result != null ? Long.valueOf(result) : 0;
    }

    /**
     * 如果key不存在, 返回0.
     */
    public Integer getAsInt(final String key) {
        String result = get(key);
        return result != null ? Integer.valueOf(result) : 0;
    }

    public void set(final String key, final String value) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.set(key, value);
            }
        });
    }

    public void set(final byte[] key, final byte[] value) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.set(key, value);
            }
        });
    }

    public  <T>  void set(final byte[] key, final T value) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.set(key, SerializationUtils.serialize(value));
            }
        });
    }

    public void setex(final String key, final String value, final int seconds) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.setex(key, seconds, value);
            }
        });
    }

    public void setex(final byte[] key, final byte[] value, final int seconds) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.setex(key, seconds, value);
            }
        });
    }

    public <T> void setex(final byte[] key, final T value, final int seconds) {
        execute(new JedisActionNoResult() {

            @Override
            public void action(Jedis jedis) {
                jedis.setex(key, seconds,  SerializationUtils.serialize(value));
            }
        });
    }

    /**
     * 如果key还不存在则进行设置，返回true，否则返回false.
     */
    public boolean setnx(final String key, final String value) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.setnx(key, value) == 1 ? true : false;
            }
        });
    }

    public boolean setnx(final byte[] key, final byte[] value) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.setnx(key, value) == 1 ? true : false;
            }
        });
    }

    public <T> boolean setnx(final byte[] key, final T value) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.setnx(key, SerializationUtils.serialize(value)) == 1 ? true : false;
            }
        });
    }

    public long incr(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    public <T> T getSet(final String key,final T t) {
        return execute(new JedisAction<T>() {
            @Override
            public T action(Jedis jedis) {
                byte[] result=jedis.getSet( key.getBytes(), SerializationUtils.serialize(t));
                return (T)SerializationUtils.deserialize(result);
            }
        });
    }


    public long decr(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    // ////////////// 关于List ///////////////////////////
    public List<String> lrange(final String key, final long start,final long end) {
       return execute(new JedisAction<List<String>>() {
            @Override
            public List<String> action(Jedis jedis) {
               return jedis.lrange(key, start, end);
            }
        });
    }

    public void lpush(final String key, final String value) {
    execute(new JedisActionNoResult() {

      @Override
      public void action(Jedis jedis) {
        jedis.lpush(key, value);
      }
    });
    }

    /**
     * 返回List长度, key不存在时返回0，key类型不是list时抛出异常.
     */
    public long llen(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    /**
     * 删除List中的第一个等于value的元素，value不存在或key不存在时返回0.
     */
    public boolean lremOne(final String key, final String value) {
        return execute(new JedisAction<Boolean>() {
            @Override
            public Boolean action(Jedis jedis) {
                Long count = jedis.lrem(key, 1, value);
                return (count == 1);
            }
        });
    }

    /**
     * 删除List中的所有等于value的元素，value不存在或key不存在时返回0.
     */
    public boolean lremAll(final String key, final String value) {
        return execute(new JedisAction<Boolean>() {
            @Override
            public Boolean action(Jedis jedis) {
                Long count = jedis.lrem(key, 0, value);
                return (count > 0);
            }
        });
    }

    // ////////////// 关于Sorted Set ///////////////////////////
    /**
     * 加入Sorted set, 如果member在Set里已存在，只更新score并返回false,否则返回true.
     */
    public boolean zadd(final String key, final String member, final double score) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.zadd(key, score, member) == 1 ? true : false;
            }
        });
    }

    /**
     * 删除sorted set中的元素，成功删除返回true，key或member不存在返回false。
     */
    public boolean zrem(final String key, final String member) {
        return execute(new JedisAction<Boolean>() {

            @Override
            public Boolean action(Jedis jedis) {
                return jedis.zrem(key, member) == 1 ? true : false;
            }
        });
    }

    /**
     * 返回List长度, key不存在时返回0，key类型不是sorted set时抛出异常.
     */
    public long zcard(final String key) {
        return execute(new JedisAction<Long>() {

            @Override
            public Long action(Jedis jedis) {
                return jedis.zcard(key);
            }
        });
    }
}