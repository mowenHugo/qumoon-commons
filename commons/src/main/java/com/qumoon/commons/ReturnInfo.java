package com.qumoon.commons;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author kevin
 */
public class ReturnInfo {

    private Map<Integer, String> info = Maps.newHashMap();
    private boolean isSuccessful;

    public ReturnInfo(boolean successful) {
        isSuccessful = successful;
    }

    public String getMsg(Integer code) {
        return info.get(code);
    }

    public String setMsg(Integer code, String msg) {
        return info.put(code, msg);
    }

    public Map<Integer, String> getInfo() {
        return info;
    }

    public void setInfo(Map<Integer, String> info) {
        this.info = info;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }
}
