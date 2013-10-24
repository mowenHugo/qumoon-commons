package com.trilemon.commons.web.shiro.redis;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Bytes;
import com.trilemon.commons.redis.JedisTemplate;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class RedisCache<K, V> implements Cache<K, V> {

    private String cacheName;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private JedisTemplate jedisTemplate;

    /**
     * 通过一个JedisManager实例构造RedisCache
     */
    private RedisCache(String cacheName, JedisPool jedisPool) {
        checkNotNull(cacheName, "cache name can not be null.");
        checkNotNull(jedisPool, "jedisPool can not be null.");

        this.cacheName = cacheName;
        this.jedisTemplate = new JedisTemplate(jedisPool);
    }

    public static RedisCache create(String cacheName, JedisPool jedisPool) {
        return new RedisCache(cacheName, jedisPool);
    }

    public byte[] getNamespaceKey(K key) {
        return Bytes.concat(cacheName.getBytes(), SerializationUtils.serialize(key));
    }

    @Override
    public V get(K key) throws CacheException {
        logger.debug("get value from key [{}]", key);
        try {
            if (key == null) {
                return null;
            } else {
                byte[] result = jedisTemplate.get(getNamespaceKey(key));
                return (V) SerializationUtils.deserialize(result);
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }

    }

    @Override
    public V put(K key, V value) throws CacheException {
        logger.debug("put key [{}] value[{}]", key, value);
        try {
            jedisTemplate.set(getNamespaceKey(key), SerializationUtils.serialize(value));
            return value;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        logger.debug("remove key [{}]", key);
        try {
            V previous = get(key);
            jedisTemplate.del(getNamespaceKey(key));
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public void clear() throws CacheException {
        logger.debug("clear");
        for (K key : keys()) {
            jedisTemplate.del(getNamespaceKey(key));
        }
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        try {
            Set<byte[]> keys = jedisTemplate.keys((cacheName + "*").getBytes());
            if (CollectionUtils.isEmpty(keys)) {
                return Collections.emptySet();
            } else {
                return Sets.newHashSet();
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public Collection<V> values() {
        try {
            Set<byte[]> keys = jedisTemplate.keys((cacheName + "*").getBytes());
            if (!CollectionUtils.isEmpty(keys)) {
                List<V> values = Lists.newArrayListWithCapacity(keys.size());
                for (byte[] key : keys) {
                    byte[] value = jedisTemplate.get(key);
                    if (value != null) {
                        values.add((V) SerializationUtils.deserialize(value));
                    }
                }
                return Collections.unmodifiableList(values);
            } else {
                return Collections.emptyList();
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    public String toString() {
        return "RedisCache name[" + cacheName + "] size[" + size() + "]";
    }
}