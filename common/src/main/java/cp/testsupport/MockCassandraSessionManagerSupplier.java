package cp.testsupport;

import cp.connect.CassandraSessionManager;
import cp.connect.CassandraSessionManagerSupplier;

/**
 *  * Mocked implementation of the CassandraSessionManagerSupplier, for unit testing
 * Created by sheng on 2/28/2016.
 */
public class MockCassandraSessionManagerSupplier implements CassandraSessionManagerSupplier {
    private transient MockCassandraSessionManager sessionManager;

    public MockCassandraSessionManagerSupplier() {
    }

    @Override
    public CassandraSessionManager get() {
        if (sessionManager == null) {
            sessionManager = new MockCassandraSessionManager();
        }
        return sessionManager;
    }
}
