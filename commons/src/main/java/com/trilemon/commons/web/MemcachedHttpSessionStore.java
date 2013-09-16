package com.trilemon.commons.web;

import net.spy.memcached.MemcachedClient;

/**
 * @author kevin
 */
public class MemcachedHttpSessionStore implements HttpSessionStore {
    private MemcachedClient memcachedClient;
    private int expires;

    @Override
    public String getValue(String sessionId, String key) {
        return (String) memcachedClient.get(sessionId + "-" + key);
    }

    @Override
    public void setValue(String sessionId, String key, String value) {
        memcachedClient.set(sessionId + "-" + key, expires, value);
    }

    public int getExpires() {
        return expires;
    }

    @Override
    public void setExpires(int expires) {
        this.expires = expires;
    }

    @Override
    public void remove(String sessionId, String key) {
        memcachedClient.delete(sessionId + "-" + key);
    }

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }
}
