package cp.model;

/**
 * Reference constants and default values used all over
 */
public class Constants {

    //public static final String CONNECT_HOST = "drlvmdrcas01.datawarehousellc.com";
    public final static String CONNECT_HOST = "drlvmdrcas01";
    public final static String KEYSPACE = "does_not_exist_fix_after_checkout";
    public final static String CONNECT_VERTICA_HOST = "vclu1srvr1";
    public final static String PERSON_JANITOR_STATUS = "person_janitor_status";
    public final static String ADDRESS_JANITOR_STATUS = "address_janitor_status";
    public final static String LOADER_STATUS_TABLE = "loader_status";
    public final static String CASS_UNAME = "cassandra";
    public final static String CASS_PWD = "cassandra";
    public final static String PERSON_DERIVED_FIELDS = "person_derived_fields";
    public final static String ADDRESS_DERIVED_FIELDS = "address_derived_fields";
    public final static String PERSON_ADDRESS_LINK = "person_address_link";
    public final static String PERSON_OBSERVED_FIELDS = "person_observed_fields";
    public final static String ADDRESS_OBSERVED_FIELDS = "address_observed_fields";
    public final static String HOUSEHOLD = "household";
    public final static String ELECTION_DATES = "election_dates";
    public final static String BEST_ADDRESS = "best_address";
    public final static String BEST_ADDRESS_LOOKUP = "best_address_lookup";
    public final static String PERSON_JANITOR_STATUS_LOOKUP = "person_janitor_status_lookup";
    public final static String FIRST_LAST_SEEN_ON = "first_last_seen_on";
    public final static String FIRST_LAST_SEEN_ON_LOOKUP = "first_last_seen_on_lookup";
    public final static String VOTER_STATUS_HISTORY_TABLE = "voter_status_history";
    public static final String DISTRICTS_OBSERVED_FIELDS = "districts_observed_fields";
    public static final String DISTRICTS_DERIVED_FIELDS = "districts_derived_fields";
    public final static String ADDRESS_DISTRICT_TABLE = "address_district";
    public final static String ADDRESS_TABLE = "address";
    public final static String ADDRESS_MATCH_FIELDS_TABLE = "address_match_fields";
    public final static String ADDRESS_GEOCODE_TABLE = "address_geocode";
    public final static String DISTRICT_TABLE = "district";
    public final static String RACE_RELIGION_ETHNICITY = "race_religion_ethnicity";
    public final static String AUDITOR_HISTOGRAMS = "auditor_histograms";

    public final static String MULTIPLE_APPEARANCE_TABLE = "multiple_appearance";
    public final static String BEST_STATE_TABLE = "best_state";

    public final static String TALEND_PERSON = "talend_person";
    public final static String VOTE_HISTORY_OBSERVED_TABLE = "vote_history_observed";
    public final static String VOTE_HISTORY_DERIVED_TABLE = "vote_history_derived";
    public final static String BALLOTCAST_LOOKUP_TABLE = "ballotcast_lookup";
    public final static String BALLOTCAST_TABLE = "ballot_cast";
    public final static String BALLOTCAST_SUMMARY_TABLE = "ballotcast_summary";
    public final static String VOTEHISTORYSUMMARY_TABLE = "vote_history_summary";
    public final static String ELECTION_TABLE = "election";
    public final static String VOTE_HISTORY_JANITOR_STATUS = "voteHistory_janitor_status";

    public static final String PERSON_JANITOR_STATUS_UPDATED = "updated";
    public static final String PERSON_JANITOR_STATUS_JANITOR = "janitor";
    public static final String PERSON_JANITOR_STATUS_WAITING_ON = "waiting_on";
    public static final String PERSON_JANITOR_STATUS_NEEDS_WORK = "needs_work";
    public static final String ADDRESS_JANITOR_STATUS_UPDATED = "updated";
    public static final String ADDRESS_JANITOR_STATUS_JANITOR = "janitor";
    public static final String ADDRESS_JANITOR_STATUS_ADDRESS_TYPE = "address_type";
    public static final String ADDRESS_JANITOR_STATUS_WAITING_ON = "waiting_on";
    public static final String ADDRESS_JANITOR_STATUS_NEEDS_WORK = "needs_work";


    //Deceased related
    public final static double SSDM_CONFIDENCE_SCORE_THRESHOLD = 1;
    public final static double IMS_CONFIDENCE_SCORE_THRESHOLD = 0.85;
    public static final Boolean DEFAULT_USE_BATCH_DELETE = Boolean.TRUE;
    public static final int LOG_BUFFER_CAPACITY = 10000;

    // Gender related
    public final static String FIRST_NAME = "first_name";
    public final static String NAME_FREQUENCY = "name_frequency";
    public final static String MALE_FIRST_NAMES = "male_first_names";
    public final static String FEMALE_FIRST_NAMES = "female_first_names";
    public final static String GENDER = "gender";
    public final static String MALE_GENDER = "male";
    public final static String FEMALE_GENDER = "female";
    public final static String UNKNOWN_GENDER = "unknown";
    public final static String OTHER_GENDER = "other";
    public final static String MALE_GENDER_ABBREVIATION = "m";
    public final static String FEMALE_GENDER_ABBREVIATION = "f";
    public final static String UNKNOWN_GENDER_ABBREVIATION = "u";
    public final static String OTHER_GENDER_ABBREVIATION = "o";
    public final static double DEFAULT_GENDER_JANITOR_PROBABILITY = 0.90;

    public final static String SSDM_DECEASED_EXTRACT = "SSDM_deceased_extract";
    public final static String SSDM_DECEASED_EXTRACT_LOOKUP = "SSDM_deceased_extract_lookup";
    public final static String IMS_DECEASED_EXTRACT = "IMS_deceased_extract";
    public final static String IMS_DECEASED_EXTRACT_LOOKUP = "IMS_deceased_extract_lookup";

    public final static int JANITOR_DEFAULT_LIMIT = 1000000;
    public final static int JANITOR_DEFAULT_BATCH = 1000;
    public final static int LOAD_PUBLISH_DEFAULT_MAX = 1000000;
    public final static int LOAD_PUBLISH_DEFAULT_BATCH = 1000;

    public static final int TIME_INTERVAL_TYPE_RESERVED = 000;
    public static final int TIME_INTERVAL_TYPE_KNOWN = 101;
    public static final int TIME_INTERVAL_TYPE_KNOWN_START = 100;
    public static final int TIME_INTERVAL_TYPE_KNOWN_END = 001;

    public static final int KAFKA_QUEUE_BUFFER_CAPACITY = 10000; // Unsent queue messages buffer to be resend

    public final static String ZOOKEEPER_HOST="pdtechsrvr1:2181";
    public final static String ZOOKEEPER_SESSION_TIMEOUT_IN_MS = "10000";
    public final static String ZOOKEEPER_CONNECTION_TIMEOUT_IN_MS = "10000";
    public final static String ZOOKEEPER_RETRY_TIME_IN_MS = "10000";
    public final static String ZOOKEEPER_CLIENT_MAX_RETRY = "5";
    public static final String DEFAULT_OPTIMIZE_STRATEGY = "SimpleOptimizeStrategy";

    public final static String DWID = "dwid";
    public final static String STATE = "state";
    public final static String LOCID = "locid";
    public final static String ROWID = "rowid";
    public final static String JANITOR = "janitor";
    public final static String ELECTION_TYPE_GENERAL = "general";
    public static final String ADDRESS_LOCATION_ID = "addrlocationid";
    public static final String REGISTRATION_ADDRESS_LOCATION_ID = "regaddrlocationid";
    public static final String MAILING_ADDRESS_LOCATION_ID = "mailaddrlocationid";

    public static final String JANITOR_KAFKA_WORK_QUEUE_SUBNAME = "work";
    public static final String JANITOR_KAFKA_AUDIT_QUEUE_SUBNAME = "audit";
    public static final String JANITOR_KAFKA_FIELD_PROCESSING_QUEUE_SUBNAME = "field_processed";
    public static final String KAFKA_CONSUMER_STREAM_COUNT = "1";

    public final static String OOZIE_JDBC_URL ="jdbc:postgresql://pdtechsrvr1:5432/oozie_proc";
    public final static String OOZIE_DB_USERNAME="oozie_proc";
    public final static String OOZIE_DB_PASSWORD="oozie_proc";
    public final static String OOZIE_JDBC_DRIVER="org.postgresql.Driver";
    public final static String OOZIE_URL="http://pdtechsrvr1:11000/oozie";

    public final static String NAMENODE_URL="hdfs://pdtechsrvr1.datawarehousellc.com:8020";
    public final static String JOBTRACKER_URL="pdtechsrvr1.datawarehousellc.com:8032";
    public final static String HUE_URL="http://pdtechsrvr1:8888/oozie/list_oozie_workflow";

    public final static String COORDINATOR_WORKFLOW_FILE= "coordinator.xml";
    public final static String COORDINATOR_TIMEOUT="0";
    public final static String COORDINATOR_CONCURRENCY_LEVEL="2";
    public final static String COORDINATOR_EXECUTION_ORDER="FIFO";
    public final static String COORDINATOR_THROTTLE="1";
    public final static String COORDINATOR_FREQUENCY_VALUE = "5";

    public final static String JANITOR_WORKFLOW_DIR = "/user/mdrwf/wf_1/";
    public final static String JANITOR_WORKFLOW_FILE = "workflow.xml";
    public final static String HDFS_USERNAME = "mdrwf";

    public final static int PREPAREDSTATEMENT_CACHE_SIZE = 100;

    public static final String PERSON_OBSERVED_QUEUE_KEY = "mdr.queue.janitor.person_observed.";
    public static final String ADDRESS_OBSERVED_QUEUE_KEY = "mdr.queue.janitor.address_observed.";
    // DQM configurations
    public static final String PRODUCTION_DQM_SERVER_WSDL = "http://prlvdqmapp01:8080/DataServices/servlet/webservices?ver=2.1&wsdlxml";
    public static final String DEVELOPMENT_DQM_SERVER_WSDL = "http://drlvdqmapp02:8080/DataServices/servlet/webservices?ver=2.1&wsdlxml";
    public static final String PRODUCTION_DQM_SERVER_REPO_NAME = "DS_Repo_01";
    public static final String DEVELOPMENT_DQM_SERVER_REPO_NAME = "SAP_DS_REPO_01";
    public static final int DQM_SERVER_REPORT_TIMEOUT = 60000;
    public static final String DQM_JOB_VOTERFILEPROCESS_CASS = "mdr_VoterFileProcess_WithCase_And_Geocoding";

    public static final String VOTER_FILE_SOURCE = "voterFile";
    public static final String COMMERCIAL_FILE_SOURCE = "commercial";
    public static final String NCOA_SOURCE = "NCOA";
    public static final String MULTIPLE_APPEARANCE_SOURCE = "multipleAppearance";
    public static final String CLIENT_FILE_SOURCE = "client";
    public static final String IMPUTED_SOURCE = "imputed";

    // Geolocation related
    public static final String GEOLOCATION_OBSERVATION = "geolocation_observation";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String GEOLOCATION_ACCURACY = "geolocation_accuracy";
    public static final String GEO_FIRST_SEEN_ON = "first_seen_on";
    public static final String GEO_LAST_SEEN_ON = "last_seen_on";
    public static final String GEO_SOURCE = "source";
    public static final String GEO_SOURCE_ACCURACY = "source_accuracy";
    public static final String ADDRESS_BEST_GEOLOCATION = "address_best_geolocation";
    public static final String GEO_SOURCE_CATALIST_LEGACY = "CATALIST_LEGACY";
    public static final String GEO_SOURCE_DQM = "DQM";

    // Best Address related
    public static final String BEST_ADDRESS_TYPE = "best_address_type";

    // MDR metadata JDBC stuff
    public static final String MDR_METADATA_JDBC_URL = "jdbc:postgresql://pdtechsrvr1:5432/mdr_metadata";
    public static final String MDR_METADATA_JDBC_USER = "mdr_metadata";
    public static final String MDR_METADATA_JDBC_PASSWORD = "mdr_metadata";
    public static final String MDR_METADATA_JDBC_DRIVER_CLASS = "org.postgresql.Driver";
    public static final String MDR_METADATA_JDBC_DEFAULT_AUTOCOMMIT = "false";
    public static final String MDR_METADATA_JDBC_MAX_IDLE = "50";
    public static final String MDR_METADATA_JDBC_MAX_ACTIVE = "50";
    public static final String MDR_METADATA_JDBC_INITIAL_SIZE = "0";
    public static final String MDR_METADATA_JDBC_MAX_AGE_IN_MINUTES = "40";
    public static final String MDR_METADATA_JDBC_TIME_BETWEEN_EVICTIONS_IN_SECONDS = "10";
    public static final String MDR_METADATA_JDBC_VALIDATION_QUERY = "select 'valid'";
    public static final String MDR_METADATA_JDBC_TEST_WHILE_IDLE = "true";
    public static final String MDR_METADATA_JDBC_TEST_ON_BORROW = "false";
    public static final String MDR_METADATA_JDBC_TEST_ON_RETURN = "false";
    public static final String MDR_METADATA_JDBC_PROPAGATE_INTERRUPT_STATE = "true";
    public static final String MDR_METADATA_JDBC_REMOVE_ABANDONED = "true";
    public static final String MDR_METADATA_JDBC_REMOVE_ABANDONED_TIMEOUT_IN_HOURS = "4";
    public static final String MDR_METADATA_JDBC_INTERCEPTORS = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
            "StatementCache(prepared=true,max=50);ResetAbandonedTimer";

    // Address parsing levels
    public static final String CATALIST_PARSED = "CATALIST_PARSED";
    public static final String DQM_PARSED = "DQM_PARSED";
    public static final String DQM_CORRECTED = "DQM_CORRECTED";

    // Matching related
    public static final String INSERT_MATCH_DEFAULT = "false";

    // temporary, voter status related until VoterStatus janitors get merged
    public static final String VOTER_STATUS_ACTIVE = "active";
    public static final String VOTER_STATUS_INACTIVE = "inactive";
    public static final String VOTER_STATUS_UNREGISTERED = "unregistered";
    public static final String VOTER_STATUS_MULTIPLE_APPEARANCES = "multipleAppearances";
    public static final String VOTER_STATUS_DROPPED = "dropped";

    //preload metadata
    public static final String PRELOAD_METADATA_TABLE = "preload_metadata";
    public static final String PRELOAD_YEAR = "year";
    public static final String PRELOAD_STATE = "state";
    public static final String PRELOAD_VERSION = "version_number";
    public static final String PRELOAD_RUN_ID = "run_id";
    public static final String PRELOAD_LATEST_EFFECTIVE_DATE = "latest_effective_date";
    public static final String PRELOAD_RELEASE_TYPE = "release_type";
    public static final String PRELOAD_RELEASE_STATUS = "release_status";
    public static final String PRELOAD_ACQUISITION_DATE = "acquisition_date";

    // address data service constants
    public static final String FIPS = "fips";
    public static final String POSTAL_STATE_TO_STATE_FIPS_LOOKUP = "postal_state_to_state_fips_lookup";
    public static final String STREET_SEGMENT_DISTRICTS = "street_segment_districts";
    public static final String MEDIA_MARKET_LOOKUP = "media_market_lookup";
    public static final String MML_MEDIA_MARKET = "media_market";
    public static final String MML_COUNTY_FIPS = "county_fips";

    // official districts table and columns
    public static final String OFFICIAL_DISTRICTS = "official_districts";
    public static final String DISTRICT_NAME = "district_name";
    public static final String DISTRICT_VALUE = "district_value";
    public static final String OBSERVATION_DATE = "observation_date";

    // official district names
    public static final String DISTRICT_STATE = "State";
    public static final String CITY_COUNCIL = "CityCouncil";
    public static final String CONGRESSIONAL_DISTRICT = "CongressionalDistrict";
    public static final String CONGRESSIONAL_DISTRICT_NAME = "CongressionalDistrictName";
    public static final String COUNTY_COMMISSION = "CountyCommission";
    public static final String COUNTY_FIPS = "CountyFips";
    public static final String COUNTY_LEGISLATIVE = "CountyLegislative";
    public static final String COUNTY_NAME = "CountyName";
    public static final String JUDICIAL_DISTRICT = "JudicialDistrict";
    public static final String MUNICIPAL_DISTRICT = "MunicipalDistrict";
    public static final String PRECINCT_CODE = "PrecinctCode";
    public static final String PRECINCT_NAME = "PrecinctName";
    public static final String PRECINCT_SPLIT = "PrecinctSplit";
    public static final String SCHOOL_BOARD = "SchoolBoard";
    public static final String SCHOOL_DISTRICT = "SchoolDistrict";
    public static final String SOS_COUNTY_CODE = "SosCountyCode";
    public static final String STATE_HOUSE_DISTRICT = "StateHouseDistrict";
    public static final String STATE_SENATE_DISTRICT = "StateSenateDistrict";
    public static final String SUPERVISOR_DISTRICT = "SupervisorDistrict";
    public static final String TOWNSHIP = "Township";
    public static final String UNIQUE_PRECINCT_CODE = "UniquePrecinctCode";
    public static final String WARD = "Ward";
    public static final String ZIP_9 = "Zip9";

    // district inference default confidence threshold
    public static final String DISTRICT_INFERENCE_DEFAULT_CONFIDENCE_THRESHOLD = "75";

    //CE stuff
    public static final String USER_TABLE = "users";
    public static final String FRIEND_LIST_TABLE = "friend_list";
    public static final String SKILLS_TABLE = "skills";
    public static final String PERSON_SKILL_TABLE = "person_skill_1";
    public static final String COUNTRY_LIST_TABLE = "country_list";
    public static final String GEOLOCATION_TABLE = "geolocations";
    public static final String MESSAGE_TABLE = "messages";

    // friend list

    public static final String USER_ID1 = "user_id1";
    public static final String USER_ID2 = "user_id2";
    public static final String FRIEND_UPDATE_TIME = "update_time";


}
