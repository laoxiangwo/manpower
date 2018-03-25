package cp.metadata;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyBatis DAO superclass
 */
public class AbstractMybatisDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SqlSessionFactory sessionFactory;

    public AbstractMybatisDao(SqlSessionFactory sqlSessionFactory) {
        this.sessionFactory = sqlSessionFactory;
    }

    protected SqlSession getSession() {
        return sessionFactory.openSession(ExecutorType.REUSE);
    }

    protected SqlSession getBatchSession() {
        return sessionFactory.openSession(ExecutorType.BATCH);
    }

    protected Configuration getSessionFactoryConfiguration() {
        return sessionFactory.getConfiguration();
    }

    protected void closeSessionQuietly(SqlSession session) {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                log.warn("Unable to nicely close session", e);
            }
        }
    }
}
