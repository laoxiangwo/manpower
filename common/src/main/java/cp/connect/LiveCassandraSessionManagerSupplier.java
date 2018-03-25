package cp.connect;

import cp.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Supplies a live CassandraSessionManager that connects to a real Cassandra db.
 * Will return the same instance of the CassandraSessionManager as long as it exists.
 *
 */
public class LiveCassandraSessionManagerSupplier implements CassandraSessionManagerSupplier {
    private static final Logger log = LoggerFactory.getLogger(LiveCassandraSessionManagerSupplier.class);

    private static final long serialVersionUID = 4293439864746747265L;
    private final ConfigurationService configurationService;
    private final ReentrantLock lock;
    private transient CassandraSessionManager cassandraSessionManager;

    public LiveCassandraSessionManagerSupplier(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        lock = new ReentrantLock();
    }

    @Override
    public CassandraSessionManager get() {
        lock.lock();
        try {
            // construct the session manager if need be
            if (cassandraSessionManager == null) {
                log.debug("Constructing a new CassandraSessionManager");
                cassandraSessionManager = new LiveCassandraSessionManagerImpl(configurationService);
            }
        } finally {
            lock.unlock();
        }
        return cassandraSessionManager;
    }
}
