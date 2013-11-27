package com.trilemon.commons.db;

import java.util.HashMap;

/**
 * @author kevin
 */
public class ShardTableMap extends HashMap<String, Object> implements IShardTable {
    public Integer getTableId() {
        Object obj = get(SHARD_TABLE_FIELD);
        if (null != obj && obj instanceof Integer) {
            return (Integer) obj;
        }
        return null;
    }

    public void setTableId(Integer tableId) {
        put(SHARD_TABLE_FIELD, tableId);
    }
}
