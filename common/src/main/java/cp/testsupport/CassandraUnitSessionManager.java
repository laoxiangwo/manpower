package cp.testsupport;

import com.datastax.driver.core.Session;
import cp.connect.CassandraSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 *  * An implementation of the CassandraSessionManager that runs against a CassandraUnit Cassandra instance.
 * Created by sheng on 2/28/2016.
 */
public class CassandraUnitSessionManager implements CassandraSessionManager {
    private static final Logger log = LoggerFactory.getLogger(CassandraUnitSessionManager.class);

    private Supplier<Session> sessionSupplier;

    public CassandraUnitSessionManager(Supplier<Session> sessionSupplier) {
        this.sessionSupplier = sessionSupplier;
    }

    @Override
    public void startCassandraClient() {
        log.info("Starting");
    }

    @Override
    public void stopCassandraClient() {
        log.info("Stopping");
    }

    @Override
    public Session getSession() {
        return sessionSupplier.get();
    }

    @Override
    public boolean isSessionActive() {
        final Session session = getSession();
        return session != null && !session.isClosed();
    }
}
