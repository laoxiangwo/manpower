package cp.testsupport.supportmodel;

import org.cassandraunit.AbstractCassandraUnit4CQLTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * @author Marcin Szymaniuk
 * @author Jeremy Sevellec
 */
public abstract class AbstractCassandraUnit4CQLTestNGCase extends AbstractCassandraUnit4CQLTestCase {

    public AbstractCassandraUnit4CQLTestNGCase() {
        super();
    }

    public AbstractCassandraUnit4CQLTestNGCase(String configurationFileName) {
        super(configurationFileName);
    }

    @BeforeClass
    public void before() throws Exception {
        super.before();
    }

    @AfterClass
    public void after() {
        super.after();
    }
}
