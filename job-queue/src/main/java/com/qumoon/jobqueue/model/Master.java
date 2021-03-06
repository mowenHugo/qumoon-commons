package com.qumoon.jobqueue.model;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * master 节点
 *
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
