package com.trilemon.commons.web.shiro.redis;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import redis.clients.jedis.JedisPool;

public class RedisCacheManager extends AbstractCacheManager {
    private JedisPool jedisPool;

    @Override
    protected Cache createCache(String name) throws CacheException {
        return RedisCache.create(name, jedisPool);
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
