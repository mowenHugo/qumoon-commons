package com.qumoon.commons.ibatis;

import com.ibatis.sqlmap.client.SqlMapExecutor;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.sql.SQLException;
import java.util.List;

/**
 * @author kevin
 */
public class BaseSqlMapClientDaoSupport extends SqlMapClientDaoSupport {

  public int batchInsert(final String statementName, final List<?> entities)
      throws DataAccessException {
    return (Integer) getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
      public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
        executor.startBatch();
        for (Object item : entities) {
          executor.insert(statementName, item);
        }
        return executor.executeBatch();
      }
    });
  }

  public int batchDelete(final String statementName, final List<?> entities)
      throws DataAccessException {
    return (Integer) getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
      public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
        executor.startBatch();
        for (Object item : entities) {
          executor.delete(statementName, item);
        }
        return executor.executeBatch();
      }
    });
  }

  public int batchUpdate(final String statementName, final List<?> entities)
      throws DataAccessException {
    return (Integer) getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
      public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
        executor.startBatch();
        for (Object item : entities) {
          executor.update(statementName, item);
        }
        return executor.executeBatch();
      }
    });
  }
}
