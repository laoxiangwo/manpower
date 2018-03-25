package cp.testsupport;

import com.datastax.driver.core.Session;
import cp.connect.CassandraSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.mockito.Mockito.mock;

/**
 *  * Mockito mocked CassandraSessionManager, to facilitate unit tests.
 * Created by sheng on 2/28/2016.
 */
public class MockCassandraSessionManager implements CassandraSessionManager {
    private static final Logger log = LoggerFactory.getLogger(MockCassandraSessionManager.class);

    private CassandraSessionManager mockSessionManager;
    private Session mockSession;
    private boolean active;

    public MockCassandraSessionManager() {
        mockSessionManager = mock(CassandraSessionManager.class);
        mockSession = mock(Session.class);
        active = false;
    }

    public CassandraSessionManager getMockSessionManager() {
        return mockSessionManager;
    }

    public Session getMockSession() {
        return mockSession;
    }

    @Override
    public void startCassandraClient() {
        log.debug("Starting mock Cassandra Session Manager");
        mockSessionManager.startCassandraClient();
        active = true;
    }

    @Override
    public void stopCassandraClient() {
        log.debug("Stopping mock Cassandra Session Manager");
        mockSessionManager.stopCassandraClient();
        active = false;
    }

    @Override
    public Session getSession() {
        log.debug("Getting session from mock Cassandra Session Manager");
        return mockSessionManager.getSession();
    }

    @Override
    public boolean isSessionActive() {
        return active;
    }
}