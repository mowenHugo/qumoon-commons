package com.qumoon.commons.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author kevin
 */
public class ShardTable implements IShardTable {

  @JsonIgnore
  private int tableId;

  public Integer getTableId() {
    return tableId;
  }

  public void setTableId(Integer tableId) {
    this.tableId = tableId;
  }
}
