package cp.connect;

import com.datastax.driver.core.Session;

/**
 * Created by shengli on 1/6/16.
 */
public interface CassandraSessionManager {
    /**
     * Start the client. Should be called before calling {@link #getSession() getSession()} .
     */
    void startCassandraClient();

    /**
     * Stop the client. After calling this, the client can be started again.
     */
    void stopCassandraClient();

    /**
     * Get a session object for accessing Cassandra.
     *
     * @return A live session object.
     */
    Session getSession();

    /**
     * Determine if a session is active.
     *
     * @return <tt>true</tt> if active, <tt>false</tt> otherwise.
     */
    boolean isSessionActive();
}
