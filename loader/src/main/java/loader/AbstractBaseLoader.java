package loader;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import cp.config.*;
import cp.connect.CassandraSessionManager;
import cp.connect.LiveCassandraSessionManagerImpl;
import cp.exceptions.DataAccessException;
import cp.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static cp.config.ConfigurationKeys.CASSANDRA_SEEDS_KEY;

//import us.catalist.mdr.janitors.metadata.dao.MetadataDao;

/**
 * Base abstract loader implementation
 */
public abstract class AbstractBaseLoader implements Loader {
    public Logger logger;
    protected String keyspace;

   // protected MetadataDao metadataDao;
    protected CassandraSessionManager sessionManager;

    public AbstractBaseLoader(CassandraSessionManager sessionManager) {
        logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());
        this.sessionManager = sessionManager;
    }

//    //TODO: instantiate the dao differently
//    protected AbstractBaseLoader(/*MetadataDao metadataDao,*/ CassandraSessionManager sessionManager) {
//        //this.metadataDao = metadataDao;
//        logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());
//        this.sessionManager = sessionManager;
//    }

    @Override
    public void init() {
        sessionManager.startCassandraClient();
    }

    @Override
    public void down() throws IllegalStateException {
        if (sessionManager == null) {
            throw new IllegalStateException("down() called on a loader which has no session manager.");
        }
        if (!sessionManager.isSessionActive()) {
            throw new IllegalStateException("down() called on a loader which has not been initialized.");
        }
        sessionManager.stopCassandraClient();
    }

    @Override
    public void reset() throws IllegalStateException {
        if (sessionManager == null) {
            throw new IllegalStateException("reset() called on a loader which has no session manager.");
        }
        if (sessionManager.isSessionActive()) {
            sessionManager.stopCassandraClient();
        }
        init();
    }
    
    @Override
    public boolean canLoad(String... files) {
        for(String file : files) {
            if(!canLoad(file)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canLoad(String file) {
        // if we can't read the first line of the file, then we can't load
        try {
            LineNumberReader in = new LineNumberReader(new FileReader(file));
            String line = in.readLine();
            if (line == null) {
                logger.error("Could not read first line in file");
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception thrown during canLoad(). Exception: {}", e);
            return false;
        }
        return true;
    }

    @Override
    public void load(String... files) {
        for(String file : files) {
            load(file);
        }
    }

    @Override
    public void load(String filename) {
        LineNumberReader in = null;
        String line;
        int linesSeen = 0;
        int linesInserted = 0;
        long runId = getRunId();

        PreparedStatement ps = sessionManager.getSession().prepare(loaderInsertStatement());
        List<ResultSetFuture> futures = new ArrayList<>();

        try {
            in = new LineNumberReader(new FileReader(filename));
            do {
                line = in.readLine();

                if (line == null)
                    continue;

                linesSeen++;

                logger.debug("Line {} is '{}'", linesSeen, line);

                // basic case when we have a CSV file
                String[] columns = line.split(",");

                String rowNumber = String.valueOf(linesSeen);
                String value1 = columns[1];
                String value2 = columns[2];
                String value3 = columns[3];

                if(!Strings.isNullOrEmpty(value1) && !Strings.isNullOrEmpty(value2) && !Strings.isNullOrEmpty(value3)) {
                    logger.debug("Inserting {}, {}, {}", value1, value2, value3);
                    ResultSetFuture rsfRawDataInsert = sessionManager.getSession().executeAsync(ps.bind(runId, rowNumber, value1, value2, value3));
                    futures.add(rsfRawDataInsert);
                    ResultSetFuture rsfStatusInsert = sessionManager.getSession().executeAsync(writeStatus(runId, rowNumber, this.getClass().getCanonicalName(), "TRUE"));
                    futures.add(rsfStatusInsert);

                    linesInserted++;
                } else {
                    logger.debug("Failed to insert");
                    ResultSetFuture rsfStatusInsert = sessionManager.getSession().executeAsync(writeStatus(runId, rowNumber, this.getClass().getCanonicalName(), "FALSE"));
                    futures.add(rsfStatusInsert);
                }

                if (futures.size() >= Constants.LOAD_PUBLISH_DEFAULT_BATCH) {
                    for (ResultSetFuture future: futures) {
                        future.getUninterruptibly();
                    }
                    futures.clear();
                }
            } while (line != null && linesSeen < Constants.LOAD_PUBLISH_DEFAULT_MAX);

            if (futures.size() > 0) {
                for (ResultSetFuture future: futures) {
                    future.getUninterruptibly();
                }
                futures.clear();
            }
        } catch (FileNotFoundException e) {
            throw new DataAccessException("Unable to find file " + filename, e);
        } catch (IOException e) {
            throw new DataAccessException("Unable to read file " + filename, e);
        } catch (Exception e) {
            throw new DataAccessException("Error while trying to load file " + filename, e);
        } finally {
            Closeables.closeQuietly(in);
            sessionManager.stopCassandraClient();
        }

        logger.info("Lines seen: {}, lines inserted: {}", linesSeen, linesInserted);
    }

    // this method can be used by all the loaders since they are writing to the same table
    // the flags can be expanded upon, for now these are basically just placeholders
    // loadId + rowId is unique across all loaders
    protected Statement writeStatus(long loadId, String rowNumber, String loaderName, String loaderStatus) {
        return QueryBuilder.insertInto(keyspace, Constants.LOADER_STATUS_TABLE)
                .value("runid", loadId)
                .value("row_number", rowNumber)
                .value("loader_name", loaderName)
                .value("load_success", loaderStatus)
                .value("processed", "FALSE")
                .value("load_date", System.currentTimeMillis());
    }

    private String loaderInsertStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ")
                .append(keyspace)
                .append('.')
                .append("base_loader_extract_example")
                .append(" (runid, row_number, value1, value2, value3)")
                .append(" VALUES (?,?,?,?,?)");
        return sb.toString();
    }

    public long getRunId() {
        return 0L;
        //return metadataDao.getNextRunId();
    }

    protected static ConfigurationService buildConfigurationService(String cassandraHost, String userName, String password, String keyspace) {

        ConstantConfigurationServiceImpl constantService = new ConstantConfigurationServiceImpl();
        Properties dynamicProperties = new Properties();

        // Cassandra connection properties
        dynamicProperties.setProperty(CASSANDRA_SEEDS_KEY, cassandraHost);
        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_USERNAME_KEY, userName);
        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_PASSWORD_KEY , password);
        dynamicProperties.setProperty(ConfigurationKeys.MAIN_KEYSPACE_KEY, keyspace);

        // Cassandra pooling options
//        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_CONCURRENCY_KEY, "4");
//        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_CORE_CONNECTIONS_KEY, "4");
//        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_MAX_CONNECTIONS_KEY, "8");
//        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_USING_COMPRESSION_KEY, "true");

        // Cassandra load balancing policy options
        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_EXCLUSION_THRESHOLD_KEY, "2");
        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_MINIMUM_MEASUREMENTS_KEY, "100");
        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_RETRY_PERIOD_IN_MS_KEY, "1000");
        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_SCALE_IN_MS_KEY, "100");
        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_UPDATE_RATE_IN_S_KEY, "10");

        // Cassandra socket options
//        dynamicProperties.setProperty(ConfigurationKeys.CASSANDRA_READ_TIMEOUT_IN_MS_KEY, "60000");

        PropertiesConfigurationServiceImpl propsService = new PropertiesConfigurationServiceImpl(dynamicProperties);
        return DelegatingConfigurationServiceImpl.builder()
                .add(propsService)
                .add(constantService)
                .build();

    }

    protected static CassandraSessionManager buildCassandraSessionManager(ConfigurationService configurationService) {
        return new LiveCassandraSessionManagerImpl(configurationService);
    }


}
