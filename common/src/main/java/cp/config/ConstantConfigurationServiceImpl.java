package cp.config;

import cp.model.Constants;

import java.util.HashMap;
import java.util.Map;

import static cp.config.ConfigurationKeys.*;

/**
 * Configuration service implementation that simply serves up the
 * same old constants we've always had.
 *
 * This is a temporary bridge to help refactor Janitors to get configuration
 * from a central place and untether us from the actual implementation of
 * configuration source information.
 *
 * Ultimately, environment-specific values should be removed from this class, so that we can
 * force their provisioning in the environment.
 */
public class ConstantConfigurationServiceImpl extends AbstractConfigurationServiceImpl {

    private static final long serialVersionUID = 1004626710328151323L;

    @Override
    public String getValue(String key) {
        return VALUE_MAP.get(key);
    }

    private static final Map<String, String> VALUE_MAP = new HashMap<>();

    static {
       // VALUE_MAP.put(CASSANDRA_SEEDS_KEY    , "drlvmdrcas01");
        VALUE_MAP.put(CASSANDRA_SEEDS_KEY    , "127.0.0.1");
        VALUE_MAP.put(CASSANDRA_USERNAME_KEY , "cassandra");
        VALUE_MAP.put(CASSANDRA_PASSWORD_KEY , "cassandra");
        VALUE_MAP.put(MAIN_KEYSPACE_KEY      , "config_key_constant_does_not_exist_fix_after_checkout");
        VALUE_MAP.put(REFERENCE_KEYSPACE_KEY , "reference");

        VALUE_MAP.put(BATCH_SIZE_KEY , "1000");
        VALUE_MAP.put(LOADER_BATCH_SIZE_KEY, "100");

        VALUE_MAP.put(CASSANDRA_CONCURRENCY_KEY      , "2");
        VALUE_MAP.put(CASSANDRA_CONSISTENCY_LEVEL_KEY , "TWO");
        VALUE_MAP.put(CASSANDRA_CORE_CONNECTIONS_KEY , "2");
        VALUE_MAP.put(CASSANDRA_MAX_CONNECTIONS_KEY  , "8");
        VALUE_MAP.put(CASSANDRA_USING_COMPRESSION_KEY, "true");

        VALUE_MAP.put(CASSANDRA_EXCLUSION_THRESHOLD_KEY , "2");
        VALUE_MAP.put(CASSANDRA_MINIMUM_MEASUREMENTS_KEY, "100");
        VALUE_MAP.put(CASSANDRA_RETRY_PERIOD_IN_MS_KEY  , "10000");
        VALUE_MAP.put(CASSANDRA_SCALE_IN_MS_KEY         , "100");
        VALUE_MAP.put(CASSANDRA_UPDATE_RATE_IN_S_KEY    , "10");
        VALUE_MAP.put(CASSANDRA_READ_TIMEOUT_IN_MS_KEY  , "60000");

        VALUE_MAP.put(VERTICA_HOSTNAME_KEY , "vclu1srvr1");
        VALUE_MAP.put(VERTICA_PORT_KEY     , "vertica.port");
        VALUE_MAP.put(VERTICA_DATABASE_KEY , "vertica.database");
        VALUE_MAP.put(VERTICA_USERNAME_KEY , "vertica.username");
        VALUE_MAP.put(VERTICA_PASSWORD_KEY , "vertica.password");

        VALUE_MAP.put(KAFKA_BROKER_KEY , "pdtechsrvr2:9092");
        VALUE_MAP.put(KAFKA_REQUIRED_ACKS_KEY ,  "1");
        VALUE_MAP.put(KAFKA_QUEUE_TOPIC_PARTITION_COUNT_KEY , "10");
        VALUE_MAP.put(KAFKA_QUEUE_TOPIC_REPLICATION_FACTOR_KEY , "2");
        VALUE_MAP.put(KAFKA_LOG_TOPIC_PARTITION_COUNT_KEY , "1");
        VALUE_MAP.put(KAFKA_LOG_TOPIC_REPLICATION_FACTOR_KEY , "1");
        VALUE_MAP.put(KAFKA_TIMEOUT_IN_MS_KEY , "10000");
        VALUE_MAP.put(KAFKA_METADATA_TIMEOUT_IN_MS_KEY , "10000");
        VALUE_MAP.put(KAFKA_EXCEPTION_TOPIC_KEY ,  "exception");
        VALUE_MAP.put(KAFKA_RETRIES_CONFIG , "3");                      // Default is "0"
        VALUE_MAP.put(KAFKA_RETRY_BACKOFF_MS_CONFIG , "200");           // Default is "100L"
        VALUE_MAP.put(KAFKA_BUFFER_MEMORY_CONFIG , "67108864");         // Default is "33554432L"
        VALUE_MAP.put(KAFKA_COMPRESSION_TYPE_CONFIG , "gzip");
        VALUE_MAP.put(JANITOR_KAFKA_QUEUE_NAMESPACE , "mdr.queue.janitor");
        VALUE_MAP.put(KAFKA_CONSUMER_STREAM_COUNT_KEY, Constants.KAFKA_CONSUMER_STREAM_COUNT);

        VALUE_MAP.put(LOG_LEVEL , "INFO");

        VALUE_MAP.put(ZOOKEEPER_HOST_KEY , Constants.ZOOKEEPER_HOST);
        VALUE_MAP.put(ZOOKEEPER_SESSION_TIMEOUT_IN_MS_KEY , Constants.ZOOKEEPER_SESSION_TIMEOUT_IN_MS);
        VALUE_MAP.put(ZOOKEEPER_CONNECTION_TIMEOUT_IN_MS_KEY , Constants.ZOOKEEPER_CONNECTION_TIMEOUT_IN_MS);
        VALUE_MAP.put(ZOOKEEPER_RETRY_TIME_IN_MS_KEY , Constants.ZOOKEEPER_RETRY_TIME_IN_MS);
        VALUE_MAP.put(ZOOKEEPER_CLIENT_MAX_RETRY_KEY , Constants.ZOOKEEPER_CLIENT_MAX_RETRY);

        VALUE_MAP.put(USE_BATCH_DELETE_KEY, "true");

        VALUE_MAP.put(SSDM_CONFIDENCE_SCORE_THRESHOLD_KEY , "1");
        VALUE_MAP.put(IMS_CONFIDENCE_SCORE_THRESHOLD_KEY , "0.85");

        VALUE_MAP.put(GENDER_JANITOR_PROBABILITY_THRESHOLD_KEY , "0.90");

        VALUE_MAP.put(SPARK_JANITOR_BATCH_DURATION_IN_SECONDS_KEY, "1");
        VALUE_MAP.put(SPARK_JANITOR_PARTITION_COUNT_KEY, "2");
        VALUE_MAP.put(SPARK_JOBS_PARTITIONS, "20");

        // Beginning DQM settings
        VALUE_MAP.put(PRODUCTION_DQM_SERVER_WSDL , "http://prlvdqmapp01:8080/DataServices/servlet/webservices?ver=2.1&wsdlxml");
        VALUE_MAP.put(DEVELOPMENT_DQM_SERVER_WSDL , "http://drlvdqmapp02:8080/DataServices/servlet/webservices?ver=2.1&wsdlxml");
        VALUE_MAP.put(PRODUCTION_DQM_SERVER_REPO_NAME, "DS_Repo_01");
        VALUE_MAP.put(DEVELOPMENT_DQM_SERVER_REPO_NAME, "SAP_DS_REPO_01");
        VALUE_MAP.put(DQM_SERVER_REPORT_TIMEOUT, "60000");
        VALUE_MAP.put(DQM_JOB_VOTERFILEPROCESS_CASS, "mdr_VoterFileProcess_WithCase_And_Geocoding");

        // MDR Metadata JDBC settings                                                                                                              );
        VALUE_MAP.put(MDR_METADATA_JDBC_URL_KEY, Constants.MDR_METADATA_JDBC_URL);
        VALUE_MAP.put(MDR_METADATA_JDBC_USER_KEY, Constants.MDR_METADATA_JDBC_USER);
        VALUE_MAP.put(MDR_METADATA_JDBC_PASSWORD_KEY, Constants.MDR_METADATA_JDBC_PASSWORD);
        VALUE_MAP.put(MDR_METADATA_JDBC_DRIVER_CLASS_KEY, Constants.MDR_METADATA_JDBC_DRIVER_CLASS);
        VALUE_MAP.put(MDR_METADATA_JDBC_DEFAULT_AUTOCOMMIT_KEY, Constants.MDR_METADATA_JDBC_DEFAULT_AUTOCOMMIT);
        VALUE_MAP.put(MDR_METADATA_JDBC_MAX_IDLE_KEY, Constants.MDR_METADATA_JDBC_MAX_IDLE);
        VALUE_MAP.put(MDR_METADATA_JDBC_MAX_ACTIVE_KEY, Constants.MDR_METADATA_JDBC_MAX_ACTIVE);
        VALUE_MAP.put(MDR_METADATA_JDBC_INITIAL_SIZE_KEY, Constants.MDR_METADATA_JDBC_INITIAL_SIZE);
        VALUE_MAP.put(MDR_METADATA_JDBC_MAX_AGE_IN_MINUTES_KEY, Constants.MDR_METADATA_JDBC_MAX_AGE_IN_MINUTES);
        VALUE_MAP.put(MDR_METADATA_JDBC_TIME_BETWEEN_EVICTIONS_IN_SECONDS_KEY, Constants.MDR_METADATA_JDBC_TIME_BETWEEN_EVICTIONS_IN_SECONDS);
        VALUE_MAP.put(MDR_METADATA_JDBC_INTERCEPTORS_KEY, Constants.MDR_METADATA_JDBC_INTERCEPTORS);
        VALUE_MAP.put(MDR_METADATA_JDBC_VALIDATION_QUERY_KEY, Constants.MDR_METADATA_JDBC_VALIDATION_QUERY);
        VALUE_MAP.put(MDR_METADATA_JDBC_TEST_WHILE_IDLE_KEY, Constants.MDR_METADATA_JDBC_TEST_WHILE_IDLE);
        VALUE_MAP.put(MDR_METADATA_JDBC_TEST_ON_BORROW_KEY, Constants.MDR_METADATA_JDBC_TEST_ON_BORROW);
        VALUE_MAP.put(MDR_METADATA_JDBC_TEST_ON_RETURN_KEY, Constants.MDR_METADATA_JDBC_TEST_ON_RETURN);
        VALUE_MAP.put(MDR_METADATA_JDBC_PROPAGATE_INTERRUPT_STATE_KEY, Constants.MDR_METADATA_JDBC_PROPAGATE_INTERRUPT_STATE);
        VALUE_MAP.put(MDR_METADATA_JDBC_REMOVE_ABANDONED_KEY, Constants.MDR_METADATA_JDBC_REMOVE_ABANDONED);
        VALUE_MAP.put(MDR_METADATA_JDBC_REMOVE_ABANDONED_TIMEOUT_IN_HOURS_KEY, Constants.MDR_METADATA_JDBC_REMOVE_ABANDONED_TIMEOUT_IN_HOURS);

        // Oozie CLI settings
        VALUE_MAP.put(NAMENODE_URL_KEY, Constants.NAMENODE_URL);
        VALUE_MAP.put(JANITOR_WORKFLOW_DIR_KEY, Constants.JANITOR_WORKFLOW_DIR);
        VALUE_MAP.put(JANITOR_WORKFLOW_FILE_KEY, Constants.JANITOR_WORKFLOW_FILE);
        VALUE_MAP.put(COORDINATOR_WORKFLOW_FILE_KEY, Constants.COORDINATOR_WORKFLOW_FILE);
        VALUE_MAP.put(HDFS_USER_KEY, Constants.HDFS_USERNAME);
        VALUE_MAP.put(HUE_URL_KEY, Constants.HUE_URL);
        VALUE_MAP.put(JOBTRACKER_URL_KEY, Constants.JOBTRACKER_URL);
        VALUE_MAP.put(COORDINATOR_TIMEOUT_KEY, Constants.COORDINATOR_TIMEOUT);
        VALUE_MAP.put(COORDINATOR_CONCURRENCY_LEVEL_KEY, Constants.COORDINATOR_CONCURRENCY_LEVEL);
        VALUE_MAP.put(COORDINATOR_EXECUTION_ORDER_KEY, Constants.COORDINATOR_EXECUTION_ORDER);
        VALUE_MAP.put(COORDINATOR_THROTTLE_KEY, Constants.COORDINATOR_THROTTLE);
        VALUE_MAP.put(COORDINATOR_FREQUENCY_VALUE_KEY, Constants.COORDINATOR_FREQUENCY_VALUE);
        VALUE_MAP.put(OOZIE_JDBC_URL_KEY, Constants.OOZIE_JDBC_URL);
        VALUE_MAP.put(OOZIE_DB_USER_KEY, Constants.OOZIE_DB_USERNAME);
        VALUE_MAP.put(OOZIE_DB_PASSWORD_KEY, Constants.OOZIE_DB_PASSWORD);
        VALUE_MAP.put(OOZIE_JDBC_DRIVER_KEY, Constants.OOZIE_JDBC_DRIVER);
        VALUE_MAP.put(OOZIE_URL_KEY , Constants.OOZIE_URL);

        VALUE_MAP.put(INSERT_MATCH_KEY, Constants.INSERT_MATCH_DEFAULT);

        // district inference
        VALUE_MAP.put(DISTRICT_INFERENCE_CONFIDENCE_THRESHOLD, Constants.DISTRICT_INFERENCE_DEFAULT_CONFIDENCE_THRESHOLD);
    }
}
