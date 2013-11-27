package com.trilemon.commons.db;

/**
 * @author kevin
 */
public class ShardTable implements IShardTable{
    private int tableId;

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
}
