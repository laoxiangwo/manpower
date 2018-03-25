package cp.config;

/**
 * Uninstantiable class that provides Java constants for all the configuration
 * property keys we care about.
 *
 */
public final class ConfigurationKeys {

    // Cassandra connection properties
    public static final String CASSANDRA_SEEDS_KEY    = "cassandra.seeds";
    public static final String CASSANDRA_USERNAME_KEY = "cassandra.username";
    public static final String CASSANDRA_PASSWORD_KEY = "cassandra.password";
    public static final String MAIN_KEYSPACE_KEY      = "cassandra.main_keyspace";
    public static final String REFERENCE_KEYSPACE_KEY = "cassandra.reference_keyspace";

    // Cassandra pooling options
    public static final String CASSANDRA_CONCURRENCY_KEY = "cassandra.concurrency";
    public static final String CASSANDRA_CORE_CONNECTIONS_KEY = "cassandra.core_connections";
    public static final String CASSANDRA_CONSISTENCY_LEVEL_KEY = "cassandra.consistency_level";
    public static final String CASSANDRA_MAX_CONNECTIONS_KEY = "cassandra.max_connections";
    public static final String CASSANDRA_USING_COMPRESSION_KEY = "cassandra.using_compression";

    // Cassandra load balancing policy options
    public static final String CASSANDRA_EXCLUSION_THRESHOLD_KEY  = "cassandra.exclusion_threshold";
    public static final String CASSANDRA_MINIMUM_MEASUREMENTS_KEY = "cassandra.minimum_measurements";
    public static final String CASSANDRA_RETRY_PERIOD_IN_MS_KEY   = "cassandra.retry_period_in_ms";
    public static final String CASSANDRA_SCALE_IN_MS_KEY          = "cassandra.scale_in_ms";
    public static final String CASSANDRA_UPDATE_RATE_IN_S_KEY     = "cassandra.update_rate_in_s";

    // Cassandra socket options
    public static final String CASSANDRA_READ_TIMEOUT_IN_MS_KEY   = "cassandra.read_timeout_in_s";

    // Vertica connection properties
    public static final String VERTICA_HOSTNAME_KEY = "vertica.hostname";
    public static final String VERTICA_PORT_KEY     = "vertica.port";
    public static final String VERTICA_DATABASE_KEY = "vertica.database";
    public static final String VERTICA_USERNAME_KEY = "vertica.username";
    public static final String VERTICA_PASSWORD_KEY = "vertica.password";

    // Kafka connection properties and options
    public final static String KAFKA_BROKER_KEY = "kafka.broker";
    public final static String KAFKA_REQUIRED_ACKS_KEY =  "kafka.required_acks";
    public final static String KAFKA_LOG_TOPIC_REPLICATION_FACTOR_KEY = "kafka_log.topic_replication_factor";
    public final static String KAFKA_LOG_TOPIC_PARTITION_COUNT_KEY = "kafka_log.topic_partition_count";
    public final static String KAFKA_QUEUE_TOPIC_REPLICATION_FACTOR_KEY = "kafka_queue.topic_replication_factor";
    public final static String KAFKA_QUEUE_TOPIC_PARTITION_COUNT_KEY = "kafka_queue.topic_partition_count";
    public final static String KAFKA_TIMEOUT_IN_MS_KEY = "kafka.timeout_in_ms";
    public final static String KAFKA_METADATA_TIMEOUT_IN_MS_KEY = "kafka.metadata_timeout_in_ms";
    public final static String KAFKA_EXCEPTION_TOPIC_KEY =  "kafka.exception_topic";
    public final static String KAFKA_RETRIES_CONFIG = "kafka.retries";
    public final static String KAFKA_RETRY_BACKOFF_MS_CONFIG = "kafka.retry.backoff.ms";
    public final static String KAFKA_BUFFER_MEMORY_CONFIG = "kafka.buffer.memory";
    public final static String KAFKA_COMPRESSION_TYPE_CONFIG = "kafka.compression";
    public final static String JANITOR_KAFKA_QUEUE_NAMESPACE = "janitor.kafka.queue.namespace";
    public static final String KAFKA_CONSUMER_STREAM_COUNT_KEY = "janitor.kafka.consumerStreamCount";

    public final static String LOG_LEVEL = "log.level";

    // Zookeeper connection properties and options
    public static final String ZOOKEEPER_HOST_KEY = "zookeeper.host";
    public static final String ZOOKEEPER_SESSION_TIMEOUT_IN_MS_KEY = "zookeeper.session_timeout_in_ms";
    public static final String ZOOKEEPER_CONNECTION_TIMEOUT_IN_MS_KEY = "zookeeper.connection_timeout_in_ms";
    public static final String ZOOKEEPER_RETRY_TIME_IN_MS_KEY = "zookeeper.retry_time_in_ms";
    public static final String ZOOKEEPER_CLIENT_MAX_RETRY_KEY = "zookeeper.client_max_retries";

    // Generic janitor related
    public static final String USE_BATCH_DELETE_KEY = "janitor.use_batch_delete";
    public static final String BATCH_SIZE_KEY = "janitor.batch_size";
    public static final String LIMIT_KEY = "janitor.limit";

    // Deceased janitor related
    public static final String SSDM_CONFIDENCE_SCORE_THRESHOLD_KEY = "janitor.deceased.ssdm_confidence_score_threshold";
    public static final String IMS_CONFIDENCE_SCORE_THRESHOLD_KEY = "janitor.deceased.ims_confidence_score_threshold";

    // Gender janitor related
    public static final String GENDER_JANITOR_PROBABILITY_THRESHOLD_KEY = "janitor.gender.probability_threshold";

    // Mapping config related
    public static final String MAPPED_KEY = "map";
    public static final String SYNTHETICS_KEY = "synthetics";
    public static final String TIMESTAMPS_KEY = "timestamps";
    public static final String LINENUMBERS_KEY = "linenumbers";
    public static final String UUID_KEYS = "uuidcolumns";
    public static final String IGNORED_KEY = "ignore";

    // Command line loader related
    public final static String LOADER_BATCH_SIZE_KEY = "loader.batchSize";
    public final static String LOADER_CACHE_SIZE_KEY = "loader.cacheSize";
    public static final String LOADER_TABLE_NAME_KEY = "loader.tableName";
    public static final String LOADER_INPUT_FILE_KEY = "loader.inputFile";
    public static final String LOADER_MAPPING_FILE_KEY = "loader.mappingFile";
    public static final String LOADER_USE_STRICT_MAPPING_KEY = "loader.useStrictMapping";

    public static final String SPARK_JANITOR_BATCH_DURATION_IN_SECONDS_KEY = "janitor.spark.batch_duration_in_s";
    public static final String SPARK_JANITOR_PARTITION_COUNT_KEY = "janitor.spark.partition_count";
    public static final String SPARK_MASTER_KEY = "spark.master";
    public static final String SPARK_JANITOR_MESSAGE_CONSUMER_SCOPE_KEY = "janitor.spark.message_consumer_scope";
    public static final String SPARK_JOBS_PARTITIONS = "spark.jobs.partitions";

    // Message loader specific
    public static final String MESSAGE_TYPE_KEY = "message.record_type";
    public static final String MESSAGE_DWID_KEY = "message.dwid";
    public static final String MESSAGE_STATE_KEY = "message.state";
    public static final String MESSAGE_PERSON_ROW_ID_KEY = "message.person_row_id";
    public static final String MESSAGE_LOCATION_ID_KEY = "message.location_id";
    public static final String MESSAGE_PERSON_ADDRESS_LINK_TYPE_KEY = "message.person_address_link_type";
    public static final String MESSAGE_RUN_ID_KEY = "message.run_id";
    public static final String MESSAGE_LINE_NUMBER_KEY = "message.line_number";

    // Misc
    public static final String CONFIG_FILE_KEY = "configFile";

    // Matching related
    public static final String INSERT_MATCH_KEY = "insertMatch";
    public static final String ADDRESS_PARSE_LEVEL_KEY = "addressParsingLevel";
    public static final String REGISTRATION_ADDRESS_PARSE_LEVEL_KEY = "registrationAddressParsingLevel";
    public static final String MAILING_ADDRESS_PARSE_LEVEL_KEY = "mailingAddressParsingLevel";

    // Person fields
    public static final String STATE_KEY = "person.state";
    public static final String STATE_FILE_ID_KEY = "person.stateFileId";
    public static final String COUNTY_FILE_ID_KEY = "person.countyFileId";
    public static final String NAME_PREFIX_KEY = "person.namePrefix";
    public static final String FIRST_NAME_KEY = "person.firstName";
    public static final String MIDDLE_NAME_KEY = "person.middleName";
    public static final String LAST_NAME_KEY = "person.lastName";
    public static final String NAME_SUFFIX_KEY = "person.nameSuffix";
    public static final String FULL_NAME_KEY = "person.fullName";
    public static final String BIRTHDATE_KEY = "person.birthdate";
    public static final String AGE_KEY = "person.age";
    public static final String GENDER_KEY = "person.gender";
    public static final String VOTER_STATUS_KEY = "person.voterStatus";
    public static final String EMAIL_KEY = "person.email";
    public static final String RACE_KEY = "person.race";

    public static final String REGISTRATION_ADDRESS_LINE_1_KEY = "person.registrationAddressLine1";
    public static final String REGISTRATION_ADDRESS_LINE_2_KEY = "person.registrationAddressLine2";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_KEY = "person.registrationStreetNum";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_FRACTION_KEY = "person.registrationStreetNumFraction";
    public static final String REGISTRATION_ADDRESS_PREDIRECTIONAL_KEY = "person.registrationPredirectional";
    public static final String REGISTRATION_ADDRESS_STREET_NAME_KEY = "person.registrationStreetName";
    public static final String REGISTRATION_ADDRESS_STREET_TYPE_KEY = "person.registrationStreetType";
    public static final String REGISTRATION_ADDRESS_POSTDIRECTIONAL_KEY = "person.registrationPostdirectional";
    public static final String REGISTRATION_SECONDARY_ADDRESS_TYPE_KEY = "person.registrationUnitType";
    public static final String REGISTRATION_SECONDARY_ADDRESS_NUMBER_KEY = "person.registrationUnitNumber";
    public static final String REGISTRATION_ADDRESS_CITY_KEY = "person.registrationCity";
    public static final String REGISTRATION_ADDRESS_STATE_KEY = "person.registrationState";
    public static final String REGISTRATION_ADDRESS_ZIP_KEY = "person.registrationZip";
    public static final String REGISTRATION_ADDRESS_ZIP_4_KEY = "person.registrationZip4";

    public static final String MAILING_ADDRESS_LINE_1_KEY = "person.mailingAddressLine1";
    public static final String MAILING_ADDRESS_LINE_2_KEY = "person.mailingAddressLine2";
    public static final String MAILING_ADDRESS_STREET_NUM_KEY = "person.mailingStreetNum";
    public static final String MAILING_ADDRESS_STREET_NUM_FRACTION_KEY = "person.mailingStreetNumFraction";
    public static final String MAILING_ADDRESS_PREDIRECTIONAL_KEY = "person.mailingPredirectional";
    public static final String MAILING_ADDRESS_STREET_NAME_KEY = "person.mailingStreetName";
    public static final String MAILING_ADDRESS_STREET_TYPE_KEY = "person.mailingStreetType";
    public static final String MAILING_ADDRESS_POSTDIRECTIONAL_KEY = "person.mailingPostdirectional";
    public static final String MAILING_SECONDARY_ADDRESS_TYPE_KEY = "person.mailingUnitType";
    public static final String MAILING_SECONDARY_ADDRESS_NUMBER_KEY = "person.mailingUnitNumber";
    public static final String MAILING_ADDRESS_CITY_KEY = "person.mailingCity";
    public static final String MAILING_ADDRESS_STATE_KEY = "person.mailingState";
    public static final String MAILING_ADDRESS_ZIP_KEY = "person.mailingZip";
    public static final String MAILING_ADDRESS_ZIP_4_KEY = "person.mailingZip4";

    public static final String REGISTRATION_ADDRESS_LINE_1_CATALIST_PARSED_KEY = "person.registrationAddressLine1.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_LINE_2_CATALIST_PARSED_KEY = "person.registrationAddressLine2.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_CATALIST_PARSED_KEY = "person.registrationStreetNum.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_FRACTION_CATALIST_PARSED_KEY = "person.registrationStreetNumFraction.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_PREDIRECTIONAL_CATALIST_PARSED_KEY = "person.registrationPredirectional.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_NAME_CATALIST_PARSED_KEY = "person.registrationStreetName.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_TYPE_CATALIST_PARSED_KEY = "person.registrationStreetType.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_POSTDIRECTIONAL_CATALIST_PARSED_KEY = "person.registrationPostdirectional.CATALIST_PARSED";
    public static final String REGISTRATION_SECONDARY_ADDRESS_TYPE_CATALIST_PARSED_KEY = "person.registrationUnitType.CATALIST_PARSED";
    public static final String REGISTRATION_SECONDARY_ADDRESS_NUMBER_CATALIST_PARSED_KEY = "person.registrationUnitNumber.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_CITY_CATALIST_PARSED_KEY = "person.registrationCity.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_STATE_CATALIST_PARSED_KEY = "person.registrationState.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_ZIP_CATALIST_PARSED_KEY = "person.registrationZip.CATALIST_PARSED";
    public static final String REGISTRATION_ADDRESS_ZIP_4_CATALIST_PARSED_KEY = "person.registrationZip4.CATALIST_PARSED";

    public static final String MAILING_ADDRESS_LINE_1_CATALIST_PARSED_KEY = "person.mailingAddressLine1.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_LINE_2_CATALIST_PARSED_KEY = "person.mailingAddressLine2.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_STREET_NUM_CATALIST_PARSED_KEY = "person.mailingStreetNum.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_STREET_NUM_FRACTION_CATALIST_PARSED_KEY = "person.mailingStreetNumFraction.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_PREDIRECTIONAL_CATALIST_PARSED_KEY = "person.mailingPredirectional.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_STREET_NAME_CATALIST_PARSED_KEY = "person.mailingStreetName.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_STREET_TYPE_CATALIST_PARSED_KEY = "person.mailingStreetType.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_POSTDIRECTIONAL_CATALIST_PARSED_KEY = "person.mailingPostdirectional.CATALIST_PARSED";
    public static final String MAILING_SECONDARY_ADDRESS_TYPE_CATALIST_PARSED_KEY = "person.mailingUnitType.CATALIST_PARSED";
    public static final String MAILING_SECONDARY_ADDRESS_NUMBER_CATALIST_PARSED_KEY = "person.mailingUnitNumber.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_CITY_CATALIST_PARSED_KEY = "person.mailingCity.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_STATE_CATALIST_PARSED_KEY = "person.mailingState.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_ZIP_CATALIST_PARSED_KEY = "person.mailingZip.CATALIST_PARSED";
    public static final String MAILING_ADDRESS_ZIP_4_CATALIST_PARSED_KEY = "person.mailingZip4.CATALIST_PARSED";

    public static final String REGISTRATION_ADDRESS_LINE_1_DQM_PARSED_KEY = "person.registrationAddressLine1.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_LINE_2_DQM_PARSED_KEY = "person.registrationAddressLine2.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_DQM_PARSED_KEY = "person.registrationStreetNum.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_FRACTION_DQM_PARSED_KEY = "person.registrationStreetNumFraction.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_PREDIRECTIONAL_DQM_PARSED_KEY = "person.registrationPredirectional.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_NAME_DQM_PARSED_KEY = "person.registrationStreetName.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_STREET_TYPE_DQM_PARSED_KEY = "person.registrationStreetType.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_POSTDIRECTIONAL_DQM_PARSED_KEY = "person.registrationPostdirectional.DQM_PARSED";
    public static final String REGISTRATION_SECONDARY_ADDRESS_TYPE_DQM_PARSED_KEY = "person.registrationUnitType.DQM_PARSED";
    public static final String REGISTRATION_SECONDARY_ADDRESS_NUMBER_DQM_PARSED_KEY = "person.registrationUnitNumber.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_CITY_DQM_PARSED_KEY = "person.registrationCity.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_STATE_DQM_PARSED_KEY = "person.registrationState.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_ZIP_DQM_PARSED_KEY = "person.registrationZip.DQM_PARSED";
    public static final String REGISTRATION_ADDRESS_ZIP_4_DQM_PARSED_KEY = "person.registrationZip4.DQM_PARSED";

    public static final String MAILING_ADDRESS_LINE_1_DQM_PARSED_KEY = "person.mailingAddressLine1.DQM_PARSED";
    public static final String MAILING_ADDRESS_LINE_2_DQM_PARSED_KEY = "person.mailingAddressLine2.DQM_PARSED";
    public static final String MAILING_ADDRESS_STREET_NUM_DQM_PARSED_KEY = "person.mailingStreetNum.DQM_PARSED";
    public static final String MAILING_ADDRESS_STREET_NUM_FRACTION_DQM_PARSED_KEY = "person.mailingStreetNumFraction.DQM_PARSED";
    public static final String MAILING_ADDRESS_PREDIRECTIONAL_DQM_PARSED_KEY = "person.mailingPredirectional.DQM_PARSED";
    public static final String MAILING_ADDRESS_STREET_NAME_DQM_PARSED_KEY = "person.mailingStreetName.DQM_PARSED";
    public static final String MAILING_ADDRESS_STREET_TYPE_DQM_PARSED_KEY = "person.mailingStreetType.DQM_PARSED";
    public static final String MAILING_ADDRESS_POSTDIRECTIONAL_DQM_PARSED_KEY = "person.mailingPostdirectional.DQM_PARSED";
    public static final String MAILING_SECONDARY_ADDRESS_TYPE_DQM_PARSED_KEY = "person.mailingUnitType.DQM_PARSED";
    public static final String MAILING_SECONDARY_ADDRESS_NUMBER_DQM_PARSED_KEY = "person.mailingUnitNumber.DQM_PARSED";
    public static final String MAILING_ADDRESS_CITY_DQM_PARSED_KEY = "person.mailingCity.DQM_PARSED";
    public static final String MAILING_ADDRESS_STATE_DQM_PARSED_KEY = "person.mailingState.DQM_PARSED";
    public static final String MAILING_ADDRESS_ZIP_DQM_PARSED_KEY = "person.mailingZip.DQM_PARSED";
    public static final String MAILING_ADDRESS_ZIP_4_DQM_PARSED_KEY = "person.mailingZip4.DQM_PARSED";

    public static final String REGISTRATION_ADDRESS_LINE_1_DQM_CORRECTED_KEY = "person.registrationAddressLine1.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_LINE_2_DQM_CORRECTED_KEY = "person.registrationAddressLine2.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_DQM_CORRECTED_KEY = "person.registrationStreetNum.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_STREET_NUM_FRACTION_DQM_CORRECTED_KEY = "person.registrationStreetNumFraction.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_PREDIRECTIONAL_DQM_CORRECTED_KEY = "person.registrationPredirectional.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_STREET_NAME_DQM_CORRECTED_KEY = "person.registrationStreetName.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_STREET_TYPE_DQM_CORRECTED_KEY = "person.registrationStreetType.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_POSTDIRECTIONAL_DQM_CORRECTED_KEY = "person.registrationPostdirectional.DQM_CORRECTED";
    public static final String REGISTRATION_SECONDARY_ADDRESS_TYPE_DQM_CORRECTED_KEY = "person.registrationUnitType.DQM_CORRECTED";
    public static final String REGISTRATION_SECONDARY_ADDRESS_NUMBER_DQM_CORRECTED_KEY = "person.registrationUnitNumber.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_CITY_DQM_CORRECTED_KEY = "person.registrationCity.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_STATE_DQM_CORRECTED_KEY = "person.registrationState.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_ZIP_DQM_CORRECTED_KEY = "person.registrationZip.DQM_CORRECTED";
    public static final String REGISTRATION_ADDRESS_ZIP_4_DQM_CORRECTED_KEY = "person.registrationZip4.DQM_CORRECTED";

    public static final String MAILING_ADDRESS_LINE_1_DQM_CORRECTED_KEY = "person.mailingAddressLine1.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_LINE_2_DQM_CORRECTED_KEY = "person.mailingAddressLine2.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_STREET_NUM_DQM_CORRECTED_KEY = "person.mailingStreetNum.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_STREET_NUM_FRACTION_DQM_CORRECTED_KEY = "person.mailingStreetNumFraction.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_PREDIRECTIONAL_DQM_CORRECTED_KEY = "person.mailingPredirectional.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_STREET_NAME_DQM_CORRECTED_KEY = "person.mailingStreetName.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_STREET_TYPE_DQM_CORRECTED_KEY = "person.mailingStreetType.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_POSTDIRECTIONAL_DQM_CORRECTED_KEY = "person.mailingPostdirectional.DQM_CORRECTED";
    public static final String MAILING_SECONDARY_ADDRESS_TYPE_DQM_CORRECTED_KEY = "person.mailingUnitType.DQM_CORRECTED";
    public static final String MAILING_SECONDARY_ADDRESS_NUMBER_DQM_CORRECTED_KEY = "person.mailingUnitNumber.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_CITY_DQM_CORRECTED_KEY = "person.mailingCity.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_STATE_DQM_CORRECTED_KEY = "person.mailingState.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_ZIP_DQM_CORRECTED_KEY = "person.mailingZip.DQM_CORRECTED";
    public static final String MAILING_ADDRESS_ZIP_4_DQM_CORRECTED_KEY = "person.mailingZip4.DQM_CORRECTED";

    // Address fields
    public static final String ADDRESS_LINE_1_KEY = "address.line1";
    public static final String ADDRESS_LINE_2_KEY = "address.line2";
    public static final String ADDRESS_STREET_NUM_KEY = "address.streetNum";
    public static final String ADDRESS_STREET_NUM_FRACTION_KEY = "address.streetNumFraction";
    public static final String ADDRESS_PREDIRECTIONAL_KEY = "address.predirectional";
    public static final String ADDRESS_STREET_NAME_KEY = "address.streetName";
    public static final String ADDRESS_STREET_TYPE_KEY = "address.streetType";
    public static final String ADDRESS_POSTDIRECTIONAL_KEY = "address.postdirectional";
    public static final String SECONDARY_ADDRESS_TYPE_KEY = "address.unitType";
    public static final String SECONDARY_ADDRESS_NUMBER_KEY = "address.unitNumber";
    public static final String ADDRESS_CITY_KEY = "address.city";
    public static final String ADDRESS_STATE_KEY = "address.state";
    public static final String ADDRESS_ZIP_KEY = "address.zip";
    public static final String ADDRESS_ZIP_4_KEY = "address.zip4";

    public static final String ADDRESS_LINE_1_CATALIST_PARSED_KEY = "address.line1.CATALIST_PARSED";
    public static final String ADDRESS_LINE_2_CATALIST_PARSED_KEY = "address.line2.CATALIST_PARSED";
    public static final String ADDRESS_STREET_NUM_CATALIST_PARSED_KEY = "address.streetNum.CATALIST_PARSED";
    public static final String ADDRESS_STREET_NUM_FRACTION_CATALIST_PARSED_KEY = "address.streetNumFraction.CATALIST_PARSED";
    public static final String ADDRESS_PREDIRECTIONAL_CATALIST_PARSED_KEY = "address.predirectional.CATALIST_PARSED";
    public static final String ADDRESS_STREET_NAME_CATALIST_PARSED_KEY = "address.streetName.CATALIST_PARSED";
    public static final String ADDRESS_STREET_TYPE_CATALIST_PARSED_KEY = "address.streetType.CATALIST_PARSED";
    public static final String ADDRESS_POSTDIRECTIONAL_CATALIST_PARSED_KEY = "address.postdirectional.CATALIST_PARSED";
    public static final String SECONDARY_ADDRESS_TYPE_CATALIST_PARSED_KEY = "address.unitType.CATALIST_PARSED";
    public static final String SECONDARY_ADDRESS_NUMBER_CATALIST_PARSED_KEY = "address.unitNumber.CATALIST_PARSED";
    public static final String ADDRESS_CITY_CATALIST_PARSED_KEY = "address.city.CATALIST_PARSED";
    public static final String ADDRESS_STATE_CATALIST_PARSED_KEY = "address.state.CATALIST_PARSED";
    public static final String ADDRESS_ZIP_CATALIST_PARSED_KEY = "address.zip.CATALIST_PARSED";
    public static final String ADDRESS_ZIP_4_CATALIST_PARSED_KEY = "address.zip4.CATALIST_PARSED";

    public static final String ADDRESS_LINE_1_DQM_PARSED_KEY = "address.line1.DQM_PARSED";
    public static final String ADDRESS_LINE_2_DQM_PARSED_KEY = "address.line2.DQM_PARSED";
    public static final String ADDRESS_STREET_NUM_DQM_PARSED_KEY = "address.streetNum.DQM_PARSED";
    public static final String ADDRESS_STREET_NUM_FRACTION_DQM_PARSED_KEY = "address.streetNumFraction.DQM_PARSED";
    public static final String ADDRESS_PREDIRECTIONAL_DQM_PARSED_KEY = "address.predirectional.DQM_PARSED";
    public static final String ADDRESS_STREET_NAME_DQM_PARSED_KEY = "address.streetName.DQM_PARSED";
    public static final String ADDRESS_STREET_TYPE_DQM_PARSED_KEY = "address.streetType.DQM_PARSED";
    public static final String ADDRESS_POSTDIRECTIONAL_DQM_PARSED_KEY = "address.postdirectional.DQM_PARSED";
    public static final String SECONDARY_ADDRESS_TYPE_DQM_PARSED_KEY = "address.unitType.DQM_PARSED";
    public static final String SECONDARY_ADDRESS_NUMBER_DQM_PARSED_KEY = "address.unitNumber.DQM_PARSED";
    public static final String ADDRESS_CITY_DQM_PARSED_KEY = "address.city.DQM_PARSED";
    public static final String ADDRESS_STATE_DQM_PARSED_KEY = "address.state.DQM_PARSED";
    public static final String ADDRESS_ZIP_DQM_PARSED_KEY = "address.zip.DQM_PARSED";
    public static final String ADDRESS_ZIP_4_DQM_PARSED_KEY = "address.zip4.DQM_PARSED";

    public static final String ADDRESS_LINE_1_DQM_CORRECTED_KEY = "address.line1.DQM_CORRECTED";
    public static final String ADDRESS_LINE_2_DQM_CORRECTED_KEY = "address.line2.DQM_CORRECTED";
    public static final String ADDRESS_STREET_NUM_DQM_CORRECTED_KEY = "address.streetNum.DQM_CORRECTED";
    public static final String ADDRESS_STREET_NUM_FRACTION_DQM_CORRECTED_KEY = "address.streetNumFraction.DQM_CORRECTED";
    public static final String ADDRESS_PREDIRECTIONAL_DQM_CORRECTED_KEY = "address.predirectional.DQM_CORRECTED";
    public static final String ADDRESS_STREET_NAME_DQM_CORRECTED_KEY = "address.streetName.DQM_CORRECTED";
    public static final String ADDRESS_STREET_TYPE_DQM_CORRECTED_KEY = "address.streetType.DQM_CORRECTED";
    public static final String ADDRESS_POSTDIRECTIONAL_DQM_CORRECTED_KEY = "address.postdirectional.DQM_CORRECTED";
    public static final String SECONDARY_ADDRESS_TYPE_DQM_CORRECTED_KEY = "address.unitType.DQM_CORRECTED";
    public static final String SECONDARY_ADDRESS_NUMBER_DQM_CORRECTED_KEY = "address.unitNumber.DQM_CORRECTED";
    public static final String ADDRESS_CITY_DQM_CORRECTED_KEY = "address.city.DQM_CORRECTED";
    public static final String ADDRESS_STATE_DQM_CORRECTED_KEY = "address.state.DQM_CORRECTED";
    public static final String ADDRESS_ZIP_DQM_CORRECTED_KEY = "address.zip.DQM_CORRECTED";
    public static final String ADDRESS_ZIP_4_DQM_CORRECTED_KEY = "address.zip4.DQM_CORRECTED";

    // DQM configurations
    public static final String PRODUCTION_DQM_SERVER_WSDL = "dqm.production.wsdl";
    public static final String DEVELOPMENT_DQM_SERVER_WSDL = "dqm.development.wsdl";
    public static final String PRODUCTION_DQM_SERVER_REPO_NAME = "dqm.development.reponame";
    public static final String DEVELOPMENT_DQM_SERVER_REPO_NAME = "dqm.development.reponame";
    public static final String DQM_SERVER_REPORT_TIMEOUT = "dqm.exportreport.timeout";
    public static final String DQM_JOB_VOTERFILEPROCESS_CASS = "dqm.job.voterfileprocess.cass";

    // MDR Metadata JDBC property keys
    public static final String MDR_METADATA_JDBC_URL_KEY = "mdr.metadata.jdbc.url";
    public static final String MDR_METADATA_JDBC_USER_KEY = "mdr.metadata.jdbc.user";
    public static final String MDR_METADATA_JDBC_PASSWORD_KEY = "mdr.metadata.jdbc.password";
    public static final String MDR_METADATA_JDBC_DRIVER_CLASS_KEY = "mdr.metadata.jdbc.driver";
    public static final String MDR_METADATA_JDBC_DEFAULT_AUTOCOMMIT_KEY = "mdr.metadata.jdbc.defaultAutocommit";
    public static final String MDR_METADATA_JDBC_MAX_IDLE_KEY = "mdr.metadata.jdbc.maxIdle";
    public static final String MDR_METADATA_JDBC_MAX_ACTIVE_KEY = "mdr.metadata.jdbc.maxActive";
    public static final String MDR_METADATA_JDBC_INITIAL_SIZE_KEY = "mdr.metadata.jdbc.initialSize";
    public static final String MDR_METADATA_JDBC_MAX_AGE_IN_MINUTES_KEY = "mdr.metadata.jdbc.maxAgeInMinutes";
    public static final String MDR_METADATA_JDBC_TIME_BETWEEN_EVICTIONS_IN_SECONDS_KEY = "mdr.metadata.jdbc.timeBetweenEvictionsInSeconds";
    public static final String MDR_METADATA_JDBC_INTERCEPTORS_KEY = "mdr.metadata.jdbc.interceptors";
    public static final String MDR_METADATA_JDBC_VALIDATION_QUERY_KEY = "mdr.metadata.jdbc.validationQuery";
    public static final String MDR_METADATA_JDBC_TEST_WHILE_IDLE_KEY = "mdr.metadata.jdbc.testWhileIdle";
    public static final String MDR_METADATA_JDBC_TEST_ON_BORROW_KEY = "mdr.metadata.jdbc.testOnBorrow";
    public static final String MDR_METADATA_JDBC_TEST_ON_RETURN_KEY = "mdr.metadata.jdbc.testOnReturn";
    public static final String MDR_METADATA_JDBC_PROPAGATE_INTERRUPT_STATE_KEY = "mdr.metadata.jdbc.propagateInterruptState";
    public static final String MDR_METADATA_JDBC_REMOVE_ABANDONED_KEY = "mdr.metadata.jdbc.removeAbandoned";
    public static final String MDR_METADATA_JDBC_REMOVE_ABANDONED_TIMEOUT_IN_HOURS_KEY = "mdr.metadata.jdbc.removeAbandonedTimeoutInHours";
    public static final String NAMENODE_URL_KEY = "hadoop.namenode.url";
    public static final String JANITOR_WORKFLOW_DIR_KEY = "mdr.janitor.workflowDir";
    public static final String JANITOR_WORKFLOW_FILE_KEY = "mdr.janitor.workflowFile";
    public static final String COORDINATOR_WORKFLOW_FILE_KEY = "mdr.coordinator.workflowFile";
    public static final String HDFS_USER_KEY = "hdfs.user";
    public static final String HUE_URL_KEY = "hue.url";
    public static final String JOBTRACKER_URL_KEY = "jobtracker.url";
    public static final String COORDINATOR_TIMEOUT_KEY = "mdr.coordinator.timeout";
    public static final String COORDINATOR_CONCURRENCY_LEVEL_KEY = "mdr.coordinator.concurrencyLevel";
    public static final String COORDINATOR_EXECUTION_ORDER_KEY = "mdr.coordinator.executionOrder";
    public static final String COORDINATOR_THROTTLE_KEY = "mdr.coordinator.throttle";
    public static final String COORDINATOR_FREQUENCY_VALUE_KEY = "mdr.coordinator.frequencyValue";
    public static final String OOZIE_JDBC_URL_KEY = "oozie.jdbc.url";
    public final static String OOZIE_DB_USER_KEY = "oozie.jdbc.user";
    public final static String OOZIE_DB_PASSWORD_KEY = "oozie.jdbc.password";
    public final static String OOZIE_JDBC_DRIVER_KEY = "oozie.jdbc.driver";
    public final static String OOZIE_URL_KEY = "oozie.url";

    // export related
    public static final String EXPORT_KEYSPACE_KEY = "keyspace";
    public static final String EXPORT_TABLE_NAME_KEY = "table";
    public static final String EXPORT_PREDICATE_KEY = "predicate";
    public static final String EXPORT_ALLOW_FILTERING_KEY = "allow-filtering";
    public static final String EXPORT_CONFIG_FILE_KEY = "config-file";
    public static final String EXPORT_OUTPUT_FILE_KEY = "output-file";
    public static final String EXPORT_LIMIT_KEY = "limit";
    public static final String EXPORT_OMIT_HEADER = "omit-header";

    // preload metadata
    public static final String PRELOAD_ACQUISITION_DATE = "preload.acquisition_date";
    public static final String PRELOAD_YEAR = "preload.year";
    public static final String PRELOAD_STATE = "preload.state";
    public static final String PRELOAD_VERSION = "preload.version";
    public static final String PRELOAD_RUN_ID = "preload.run_id";
    public static final String PRELOAD_RELEASE_TYPE = "preload.release_type";
    public static final String PRELOAD_RELEASE_STATUS = "preload.release_status";
    public static final String PRELOAD_INPUT_FILE = "preload.input_file";

    // ballotcast summary related
    public static final String SUMMARY_STATE = "summary.state";

    // district inference
    public static final String DISTRICT_INFERENCE_CONFIDENCE_THRESHOLD = "mdr.district.inference.confidenceThreshold";

    private ConfigurationKeys() {}
}

