package com.trilemon.commons.web;

/**
 * @author kevin
 */
public interface HttpSessionStore {
    String getValue(String sessionId, String key);

    void setValue(String sessionId, String key, String value);

    void setExpires(int expires);

    void remove(String sessionId, String key);
}
