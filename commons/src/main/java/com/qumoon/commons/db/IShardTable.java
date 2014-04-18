package com.qumoon.commons.db;

/**
 * @author kevin
 */
public interface IShardTable {

  String SHARD_TABLE_FIELD = "tableId";

  /**
   * 获取分表 id
   */
  Integer getTableId();

  /**
   * 设置分表 id
   */
  void setTableId(Integer tableId);
}
