package cp.connect;

import cp.config.ConfigurationService;
import cp.exceptions.ConfigurationException;
import cp.util.FileUtil;
import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.LatencyAwarePolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static cp.config.ConfigurationKeys.*;

/**
 * Contains all of the messy business of configuring a live Cassandra session
 */
public class LiveCassandraSessionManagerImpl implements CassandraSessionManager {
    private static final Logger log = LoggerFactory.getLogger(LiveCassandraSessionManagerImpl.class);

    private Session session;
    private ConfigurationService configService;
    private Cluster cluster;

    public LiveCassandraSessionManagerImpl(ConfigurationService configService) {
        this.configService = configService;
    }

    @Override
    public void startCassandraClient() {
        log.info("Starting Cassandra session");
        PoolingOptions pools = new PoolingOptions();
        pools.setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance.LOCAL, configService.getIntegerValue(CASSANDRA_CONCURRENCY_KEY));
        pools.setCoreConnectionsPerHost(HostDistance.LOCAL, configService.getIntegerValue(CASSANDRA_CORE_CONNECTIONS_KEY));
        pools.setMaxConnectionsPerHost(HostDistance.LOCAL, configService.getIntegerValue(CASSANDRA_MAX_CONNECTIONS_KEY));
        pools.setCoreConnectionsPerHost(HostDistance.REMOTE, configService.getIntegerValue(CASSANDRA_CORE_CONNECTIONS_KEY));
        pools.setMaxConnectionsPerHost(HostDistance.REMOTE, configService.getIntegerValue(CASSANDRA_MAX_CONNECTIONS_KEY));

        log.info("  max requests per connection: {}", configService.getValue(CASSANDRA_CONCURRENCY_KEY));
        log.info("  core connections:            {}", configService.getValue(CASSANDRA_CORE_CONNECTIONS_KEY));
        log.info("  max connections:             {}", configService.getValue(CASSANDRA_MAX_CONNECTIONS_KEY));
        log.info("  compression enabled:         {}", configService.getValue(CASSANDRA_USING_COMPRESSION_KEY));

        log.debug("Building load balancing policy");
        LoadBalancingPolicy loadBalancingPolicy = LatencyAwarePolicy.builder(new RoundRobinPolicy()) //uses round robin to switch between nodes
           .withExclusionThreshold(configService.getIntegerValue(CASSANDRA_EXCLUSION_THRESHOLD_KEY))   //how much worse should a node be than the best performing node, here it means it must be twice as slow
           .withMininumMeasurements(configService.getIntegerValue(CASSANDRA_MINIMUM_MEASUREMENTS_KEY))  // min measurements for latency used for computation
           .withRetryPeriod(configService.getIntegerValue(CASSANDRA_RETRY_PERIOD_IN_MS_KEY), TimeUnit.MILLISECONDS) //delay before a node is given a 2nd chance
           .withScale(configService.getIntegerValue(CASSANDRA_SCALE_IN_MS_KEY), TimeUnit.MILLISECONDS) //older values of latency measurement are weighted, this tunes the weight given higher values means less weight
           .withUpdateRate(configService.getIntegerValue(CASSANDRA_UPDATE_RATE_IN_S_KEY), TimeUnit.SECONDS) //recalculate latency at this interval
           .build();

        // Create session to hosts
        log.debug("Building cluster");
        String seedsStr = configService.getValue(CASSANDRA_SEEDS_KEY);
        if (Strings.isNullOrEmpty(seedsStr)) {
            throw new ConfigurationException("Can't start Cassandra client with null or empty cassandraSeeds string. " +
               "Missing config property " + CASSANDRA_SEEDS_KEY);
        }
        String cassandraSeeds[] = configService.getValue(CASSANDRA_SEEDS_KEY).split(",");
        log.info("  seeds:                       {}", configService.getValue(CASSANDRA_SEEDS_KEY));

        SocketOptions socketOptions = new SocketOptions()
           .setTcpNoDelay(true)
           .setKeepAlive(true)
           .setReadTimeoutMillis(configService.getIntegerValue(CASSANDRA_READ_TIMEOUT_IN_MS_KEY));
        log.info("  read timeout(ms):            {}", configService.getIntegerValue(CASSANDRA_READ_TIMEOUT_IN_MS_KEY));

        log.info("  consistency level:           {}", configService.getValue(CASSANDRA_CONSISTENCY_LEVEL_KEY));
        Cluster.Builder builder = new Cluster.Builder()
           .addContactPoints(cassandraSeeds)
           .withPoolingOptions(pools)
           .withSocketOptions(socketOptions)
           .withLoadBalancingPolicy(loadBalancingPolicy)
           .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.valueOf(configService.getValue(CASSANDRA_CONSISTENCY_LEVEL_KEY))));
        String username = configService.getValue(CASSANDRA_USERNAME_KEY);
        if (username != null) {
            builder.withCredentials(username, configService.getValue(CASSANDRA_PASSWORD_KEY));
        }
        cluster = builder.build();
        if (configService.getBooleanValue(CASSANDRA_USING_COMPRESSION_KEY, Boolean.FALSE))
            cluster.getConfiguration().getProtocolOptions().setCompression(ProtocolOptions.Compression.LZ4);

        log.debug("Connecting to cluster (No keyspace provided).");
        session = cluster.connect();
        log.debug("Connected to cluster");

        Metadata metadata = cluster.getMetadata();

        String connectInfo = username != null ?
           String.format("Connected to cluster '%s' on %s as %s.",
              metadata.getClusterName(), metadata.getAllHosts(), username)
           : String.format("Connected to cluster '%s' on %s as anonymous user.",
           metadata.getClusterName(), metadata.getAllHosts());
        log.info(connectInfo);
    }

    @Override
    public void stopCassandraClient() {
        log.info("Stopping Cassandra session");

        FileUtil.closeQuietly(session, cluster);

    }

    @Override
    public Session getSession() {
        if (session == null) {
            throw new IllegalStateException("Cassandra session has not been created yet.");
        }
        if (session.isClosed()) {
            throw new IllegalStateException("Cassandra session is closed.");
        }
        return session;
    }

    @Override
    public boolean isSessionActive() {
        return session != null && !session.isClosed();
    }

    @Override
    protected void finalize() throws Throwable {
        stopCassandraClient();
        super.finalize();
    }
}
