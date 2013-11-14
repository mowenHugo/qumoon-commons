package com.trilemon.boss.infra.jobqueue.model;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @author kevin
 */
public class Master implements Serializable {
    private String master;
    private DateTime createTime;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }
}
