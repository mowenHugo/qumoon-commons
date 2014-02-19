package com.qumoon.commons.db;

/**
 * @author kevin
 */
public interface IShardTable {
    String SHARD_TABLE_FIELD="tableId";
    /**
     * 获取分表 id
     *
     * @return
     */
    Integer getTableId();

    /**
     * 设置分表 id
     *
     * @param tableId
     */
    void setTableId(Integer tableId);
}
