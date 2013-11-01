package com.trilemon.commons.mybatis;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author kevin
 */
public class MyBatisBatchWriter<T> implements InitializingBean {
    protected static final Log logger = LogFactory.getLog(MyBatisBatchWriter.class);
    private SqlSessionTemplate sqlSessionTemplate;
    private boolean assertUpdates = true;
    private int batchSize = 100;

    /**
     * Public setter for the flag that determines whether an assertion is made that all items cause at least one row to
     * be updated.
     *
     * @param assertUpdates the flag to set. Defaults to true;
     */
    public void setAssertUpdates(boolean assertUpdates) {
        this.assertUpdates = assertUpdates;
    }

    /**
     * Public setter for {@link org.apache.ibatis.session.SqlSessionFactory} for injection purposes.
     *
     * @param sqlSessionFactory
     */
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        if (sqlSessionTemplate == null) {
            this.sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
        }
    }

    /**
     * Public setter for the {@link org.mybatis.spring.SqlSessionTemplate}.
     *
     * @param sqlSessionTemplate the SqlSessionTemplate
     */
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    /**
     * Check mandatory properties - there must be an SqlSession and a statementId.
     */
    public void afterPropertiesSet() {
        checkNotNull(sqlSessionTemplate, "A SqlSessionFactory or a SqlSessionTemplate is required.");
        checkArgument(ExecutorType.BATCH == sqlSessionTemplate.getExecutorType(), "SqlSessionTemplate's executor type must be BATCH");
    }

    public List<Integer> write(String statementId, final List<? extends T> items) {
        checkNotNull(statementId, "statementId can not be null.");
        List<BatchResult> result = Lists.newArrayList();
        if (!items.isEmpty()) {

            if (logger.isDebugEnabled()) {
                logger.debug("Executing batch with " + items.size() + " items.");
            }

            SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);

            int index = 0;
            try {
                for (T item : items) {
                    session.update(statementId, item);
                    index++;
                    if (index % batchSize == 0) {
                        session.commit();
                        session.clearCache();
                    }
                }

                result = session.flushStatements();
            } catch (Throwable e) {
                session.rollback();
                throw e;
            } finally {
                session.close();
            }
            if (result.size() != 1) {
                throw new InvalidDataAccessResourceUsageException("Batch execution returned invalid results. " +
                        "Expected 1 but number of BatchResult objects returned was " + result.size());
            }

            int[] updateCounts = result.get(0).getUpdateCounts();

            for (int i = 0; i < updateCounts.length; i++) {
                int value = updateCounts[i];
                if (value == 0) {
                    throw new EmptyResultDataAccessException("Item " + i + " of " + updateCounts.length
                            + " did not update any rows: [" + items.get(i) + "]", 1);
                }
            }
            return Ints.asList(updateCounts);
        }
        return Lists.newArrayList();
    }
}
