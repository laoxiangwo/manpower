package loader.cli;

import com.datastax.driver.core.*;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.DataType.Name;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import cp.config.ConfigurationService;
import cp.config.ConstantConfigurationServiceImpl;
import cp.config.DelegatingConfigurationServiceImpl;
import cp.config.PropertiesConfigurationServiceImpl;
import cp.connect.CassandraSessionManager;
import cp.connect.LiveCassandraSessionManagerImpl;
import cp.exceptions.ProcessException;
import cp.model.Constants;
import cp.util.CaseInsensitiveSet;
import cp.util.CommandLinePropertiesUtil;
import cp.util.DurationUtil;
import loader.util.CQLTypeUtil;
import loader.util.CQLUtil;
import loader.util.CloseablesWrapper;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cp.config.ConfigurationKeys.*;
import static cp.util.CommandLinePropertiesUtil.setOptionsAsProperties;

/**
 * Loads from flat text files into Cassandra in multi-threaded fashion.
 */
public class CommandLineLoader extends AbstractLoader {
    private static final Logger log = LoggerFactory.getLogger(CommandLineLoader.class);

    public static void main(String args[]) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    log.info("Shutting down logging");
                    LogManager.shutdown();
                } catch (Exception e) {
                    log.error("Unable to cleanly shut down logging", e);
                }
            }
        });

        CommandLineLoader loader = setupCLL(args);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                try{
                    log.debug("Stopping Cassandra session");
                    loader.sessionManager.stopCassandraClient();
                } catch (Exception e) {
                    log.error("Unable to nicely close Cassandra client.", e);
                }
            }
        });

        int executionResult = 0;
        try {
            executionResult = loader.load();
        } catch (Exception e) {
            log.error("Error while loading", e);
            System.exit(-1);
        }
        System.exit(executionResult);
    }

    private static CommandLineLoader setupCLL(String args[]) {
        ConfigurationService configurationService = buildConfigService(setOptionsAsProperties(args));

        MappingConfig mappingConfig = null;

        inputFileFilename = configurationService.getValue(LOADER_INPUT_FILE_KEY);
        if (Strings.isNullOrEmpty(inputFileFilename)) {
            log.error("Missing argument {}: input filename", LOADER_INPUT_FILE_KEY);
            printHelp();
            System.exit(-1);
        }

        mappingFilename = configurationService.getValue(LOADER_MAPPING_FILE_KEY);
        if (Strings.isNullOrEmpty(mappingFilename)) {
            log.error("Missing argument {}: mapping config file", LOADER_MAPPING_FILE_KEY);
            printHelp();
            System.exit(-1);
        }

        // process the mapping configuration file, which should be a json file
        try {
            mappingConfig = MappingConfig.fromJSON(loadJSONFromFile(mappingFilename));
        } catch (FileNotFoundException e) {
            log.error("Mapping file not found: {}", mappingFilename);
            printHelp();
            System.exit(-1);
        } catch (RuntimeException e) {
            log.error("Error processing mapping config file.", e);
            System.exit(-1);
        }

        // process any synthetics from command line that might have been passed in addition to those from mapping config
        if (!Strings.isNullOrEmpty(configurationService.getValue(SYNTHETICS_KEY))) {
            mappingConfig.addSynthetics(configurationService.getValue(SYNTHETICS_KEY));
        }

        // process any timestamps from command line that might have been passed in addition to those from mapping config
        if (!Strings.isNullOrEmpty(configurationService.getValue(TIMESTAMPS_KEY))) {
            mappingConfig.addTimestamps(configurationService.getValue(TIMESTAMPS_KEY));
        }

        // process any linenumbers from command line that might have been passed in addition to those from mapping config
        if (!Strings.isNullOrEmpty(configurationService.getValue(LINENUMBERS_KEY))) {
            mappingConfig.addLineNumbers(configurationService.getValue(LINENUMBERS_KEY));
        }

        // if user overwrote the table in the file, assign it now
        if (!Strings.isNullOrEmpty(configurationService.getValue(LOADER_TABLE_NAME_KEY))) {
            mappingConfig.setTable(configurationService.getValue(LOADER_TABLE_NAME_KEY));
        }

        if(!Strings.isNullOrEmpty(configurationService.getValue(LOADER_USE_STRICT_MAPPING_KEY))){
            mappingConfig.setUseStrictMapping(configurationService.getValue(LOADER_USE_STRICT_MAPPING_KEY));
        }

        if (!Strings.isNullOrEmpty(configurationService.getValue(UUID_KEYS))) {
            mappingConfig.addUUIDs(configurationService.getValue(UUID_KEYS));
        }

        CassandraSessionManager sessionManager = new LiveCassandraSessionManagerImpl(configurationService);
        try {
            sessionManager.startCassandraClient();
        } catch (Exception e) {
            log.error("Unable to start Cassandra Client.", e);
            System.exit(-1);
        }

        return new CommandLineLoader(sessionManager, configurationService, mappingConfig, inputFileFilename);
    }

    private static void printHelp(){
        // Required options
        log.info("------- Commandline loader options -------");
        log.info("Required options :");
        log.info("--{} : Name of the keyspace.", MAIN_KEYSPACE_KEY);
        log.info("--{} : Path to mapping file.", LOADER_MAPPING_FILE_KEY);
        log.info("--{} : Path to input file.", LOADER_INPUT_FILE_KEY);
        // Additional options
        log.info("Additional options :");
        log.info("--{} : Batch size for writing to db.", LOADER_BATCH_SIZE_KEY);
        log.info("--{} : Cache size for prepared statements.", LOADER_CACHE_SIZE_KEY);
        log.info("--{} : Path to additional properties config file", CONFIG_FILE_KEY);
        log.info("--{} : Extra synthetic columns", SYNTHETICS_KEY);
        log.info("--{} : Extra timestamp columns", TIMESTAMPS_KEY);
        log.info("--{} : Extra linenumber columns", LINENUMBERS_KEY);
        log.info("--{} : Extra uuid columns", UUID_KEYS);
        log.info("--{} : Table name override", LOADER_TABLE_NAME_KEY);
        log.info("--{} : Use strict mappings from mapping file (Default value false)", LOADER_USE_STRICT_MAPPING_KEY);
        log.info("-------  -------");
    }

    private static ConfigurationService buildConfigService(Properties properties) {
        ConstantConfigurationServiceImpl constantService = new ConstantConfigurationServiceImpl();

        PropertiesConfigurationServiceImpl propsService = new PropertiesConfigurationServiceImpl(properties);
        properties.setProperty(CASSANDRA_CONSISTENCY_LEVEL_KEY, CQLUtil.CQLKeyword.LOCAL_QUORUM.name());
        // user might choose to add an additional configuration file,
        // the properties from that file will be superseded by those from the command line
        if (properties.containsKey(CONFIG_FILE_KEY)) {
            String configFile = properties.getProperty(CONFIG_FILE_KEY);

            if (Strings.isNullOrEmpty(configFile)) {
                log.error("configFile option present but value was null or empty");
                System.exit(-1);
            }

            PropertiesConfigurationServiceImpl loaderConfig = null;

            try {
                loaderConfig = CommandLinePropertiesUtil.getPropertiesFromFile(configFile);
            } catch (FileNotFoundException e) {
                log.error("Config file not found: {}", configFile, e);
                System.exit(-1);
            } catch (IOException e) {
                log.error("Error loading config file: {}", configFile, e);
                System.exit(-1);
            } catch (RuntimeException e) {
                log.error("Error processing config file.", e);
                System.exit(-1);
            }

            return DelegatingConfigurationServiceImpl.builder()
                    .add(propsService)
                    .add(loaderConfig)
                    .add(constantService)
                    .build();
        }

        return DelegatingConfigurationServiceImpl.builder()
                .add(propsService)
                .add(constantService)
                .build();
    }

    // static fields and methods above
    //====================================================================================================
    // instance fields and methods below

    public CommandLineLoader(CassandraSessionManager sessionManager,
                             ConfigurationService configService,
                             MappingConfig mappingConfig,
                             String inputFilename) {
        super(sessionManager, configService, mappingConfig, inputFilename);
    }

    @Override
    public int load() {

        if (!determineIfTableExists()) {
            throw new CLILoaderRuntimeException("Target table " + configService.getValue(MAIN_KEYSPACE_KEY) + "." + mappingConfig.getTable() +
               " does not exist");
        }

        FileMetrics fileMetrics = estimateBytesAndLinesInFile(inputFilename);
        log.debug("File metrics: {}", fileMetrics);
        if(fileMetrics.lines == 0){
            log.error("Invalid input file {}. File is empty", inputFilename);
            return -1;
        }
        LineNumberReader inputFile = null;

        Session session = sessionManager.getSession();

        int columnIndex = 0;
        int linenumber = 0;
        int linesProcessed = 0;
        int lastPercentile = 0;
        int insertsCompleted = 0;
        Stopwatch timer = DurationUtil.getStopwatchStarted();
        boolean errorCondition = false;
        boolean exceededEstimate = false;
        List<ResultSetFuture> futures = Lists.newArrayListWithExpectedSize(configService.getIntegerValue(LOADER_BATCH_SIZE_KEY));

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                log.debug("In futures shutdown hook");
                for (ResultSetFuture future: futures) {
                    if(!future.isDone()){
                        future.cancel(true);
                    }
                }
            }
        });

        /**
         *   Using Guava CacheLoader to cache the prepared statements for reuse.
         *   The idea is to void inserting null values as they create tombstones in cassandra.
         *   So we will try to create insert statements based on which columns have data in a line.
         */
        CacheLoader<String, PreparedStatement> cacheLoader = new CacheLoader<String, PreparedStatement>() {
            @Override
            public PreparedStatement load(String id)
               throws Exception {
                return session.prepare(id);
            }
        };
        Cache<String, PreparedStatement> preparedStatementCache = CacheBuilder.newBuilder()
           .maximumSize(configService.getIntegerValue(
                   LOADER_CACHE_SIZE_KEY) == null ? Constants.PREPAREDSTATEMENT_CACHE_SIZE : configService.getIntegerValue(
                   LOADER_CACHE_SIZE_KEY))
           .recordStats()
           .build(cacheLoader);

        //For any columns with timestamp synthetic
        String timestamp = DurationUtil.getCurrentDateWithMDRDateFormat(); // TODO: temporary fix

        PreparedStatement preparedStatement;

        try {
            inputFile = new LineNumberReader(new FileReader(inputFilename));
            String line;

            //TODO: accept delimiter as an option from command line
            List<String> fileHeaders = Arrays.asList(inputFile.readLine().split("\\t", -1));

            CaseInsensitiveSet insertedColumns = new CaseInsensitiveSet();

            if (log.isDebugEnabled()) {
                log.debug("File headers: {}", Joiner.on(", ").join(fileHeaders));
            }

            List<String> columns = determineInputColumns(fileHeaders, getTableColumns());
            Map<String, DataType> columnTypeMap = determineTableColumnTypes(columns.toArray(new String[columns.size()]));

            int statementsInBatch = 0;

            Map<String, String> syntheticToColumnMap = mappingConfig.getSyntheticToColumnMap();

            //Fix for missing headers in file but mapped in mapping file.
            //Since we are getting different file formats at this time, user might have hard time correcting the mapping files.
            //To avoid this, this fix will have same mapping file, and avoid any headers missing in inputfile but present in mapping file.

            List<String> missingMappingHeaders = new ArrayList<>();
            mappingConfig.getHeaderToColumnMap().forEach((k, v) -> {
                int index = fileHeaders.indexOf(k);
                if (index < 0) {
                    log.warn("Invalid header {}. Header mapped in {} is not present in input file {}.", k, mappingFilename,
                       inputFilename);
                    missingMappingHeaders.add(k);
                }
            });
            for(String headerToRemove : missingMappingHeaders) {
                mappingConfig.getHeaderToColumnMap().remove(headerToRemove);
            }

            do {
                insertedColumns.clear();
                line = inputFile.readLine();
                if (line == null) {
                    continue;
                }

                linenumber++;

                Insert insertQuery = QueryBuilder.insertInto(CQLUtil.escapeIdentifier(configService.getValue(MAIN_KEYSPACE_KEY)),
                                                             CQLUtil.escapeIdentifier(mappingConfig.getTable()));

                List<Object> rowValuesToInsert = new ArrayList<>();

                String rowData[] = line.split("\\t", -1);

                /**
                 * Check header to column map and either add the value in the file or if the value in the file is null/empty
                 *  then either check the synthetics map for defaults or don't insert it if default value is not present
                 */
                mappingConfig.getHeaderToColumnMap().forEach((k, v) -> {
                    int index = fileHeaders.indexOf(k);
                    Object valueToInsert = CQLTypeUtil.convertObjectType(rowData[index], columnTypeMap.get(v.toLowerCase()));

                    if (valueToInsert == null && syntheticToColumnMap.containsKey(v)) {
                        // check if there is a default value in the synthetics map
                        valueToInsert =
                           CQLTypeUtil.convertObjectType(syntheticToColumnMap.get(v), columnTypeMap.get(v.toLowerCase()));
                    }

                    // only insert if the value is not null to avoid tombstones in cassandra
                    if (valueToInsert != null) {
                        insertQuery.value(v, QueryBuilder.bindMarker());
                        rowValuesToInsert.add(valueToInsert);
                        insertedColumns.add(v);
                    }

                });

                /**
                 * Check the rest of the synthetics map for any constants to be added for a column by
                 * cross-referencing the columns already added and synthetics that haven't been added.
                 * This is due to the fact that if a header and synthetics are both mapped to the same column then that
                 * implies the synthetic is a default value in case of a n ull/empty file value, if a synthetic is
                 * present with no header mapping then it is a constant value to be associated to a column
                 */
                syntheticToColumnMap.forEach((k, v) -> {
                    if (!insertedColumns.contains(k)) {
                        insertQuery.value(k, QueryBuilder.bindMarker());
                        rowValuesToInsert.add(CQLTypeUtil.convertObjectType(v, columnTypeMap.get(k.toLowerCase())));
                        insertedColumns.add(k);
                    }
                });

                // check timestamps and add the value from the pre-calculated timestamp
                mappingConfig.getTimestampColumns().forEach(s -> {
                    if (!(columnTypeMap.get(s).getName().equals(Name.TEXT) ||
                             columnTypeMap.get(s).getName().equals(Name.TIMESTAMP) || columnTypeMap.get(s).getName().equals(
                       Name.VARCHAR))) {
                        throw new ProcessException(
                           "Invalid data type for column " + s + ". Expected TEXT/VARCHAR/TIMESTAMP but found " +
                              columnTypeMap.get(s).getName());
                    }

                    insertQuery.value(s, QueryBuilder.bindMarker());
                    rowValuesToInsert.add(CQLTypeUtil.convertObjectType(timestamp, columnTypeMap.get(s.toLowerCase())));
                    insertedColumns.add(s);
                });

                // check linenumbers and add the value of the current linesprocessed count
                final String finalLinenumber = String.valueOf(linenumber);
                mappingConfig.getLineNumberColumns().forEach(s -> {
                    insertQuery.value(s, QueryBuilder.bindMarker());
                    rowValuesToInsert.add(CQLTypeUtil.convertObjectType(finalLinenumber, columnTypeMap.get(s.toLowerCase())));
                    insertedColumns.add(s);
                });

                mappingConfig.getUuidColumns().forEach(s -> {
                    insertQuery.value(s, QueryBuilder.bindMarker());
                    rowValuesToInsert.add(CQLTypeUtil.convertObjectType(UUID.randomUUID().toString(), columnTypeMap.get(s.toLowerCase())));
                    insertedColumns.add(s);
                });

                log.debug("Inserting {} columns. Columns: {}", insertedColumns.size(), insertedColumns.toString());

                preparedStatement = preparedStatementCache.getIfPresent(insertQuery.getQueryString());
                if(preparedStatement == null){
                    preparedStatement = session.prepare(insertQuery.getQueryString());
                    preparedStatementCache.put(insertQuery.getQueryString(), preparedStatement);
                }

                futures.add(session.executeAsync( preparedStatement.bind(rowValuesToInsert.toArray())));

                statementsInBatch++;
                if (statementsInBatch == configService.getIntegerValue(LOADER_BATCH_SIZE_KEY)) {
                    for (ResultSetFuture future: futures) {
                        future.getUninterruptibly();
                        insertsCompleted++;
                    }
                    futures.clear();
                    statementsInBatch = 0;
                }

                if (!exceededEstimate && insertsCompleted > fileMetrics.lines) {
                    exceededEstimate = true;
                    log.info("Line estimate fell short. Using actual inserted line count henceforth");
                }

                if (insertsCompleted > ((lastPercentile + 10) * fileMetrics.lines / 100)) {
                    while (insertsCompleted > ((lastPercentile + 10) * fileMetrics.lines / 100)) {
                        lastPercentile += 10;
                    }
                    //log.trace("Elapsed time millis: {}", timer.elapsed(TimeUnit.MILLISECONDS));
                    long newLineEstimate = fileMetrics.lines > insertsCompleted ? fileMetrics.lines : insertsCompleted;
                    log.info("{}% complete. {}/{} lines processed. {} elapsed, {} remaining.",
                       insertsCompleted * 100 / newLineEstimate, insertsCompleted, newLineEstimate,
                       formatElapsedTime(timer.elapsed(TimeUnit.MILLISECONDS)),
                       formatElapsedTime(estimateRemainingTime(insertsCompleted, newLineEstimate,
                          timer.elapsed(TimeUnit.MILLISECONDS))));
                }
            } while (line != null);

            if (statementsInBatch > 0 || !futures.isEmpty()) {
                //log.trace("Waiting for batch of futures");
                for (ResultSetFuture future: futures) {
                    future.getUninterruptibly();
                    insertsCompleted++;
                }
            }

            timer.stop();
            long newLineEstimate = fileMetrics.lines > insertsCompleted ? fileMetrics.lines : insertsCompleted;
            log.info("Loading data to db completed. {} lines of {} line file processed. {} elapsed.",
               insertsCompleted, newLineEstimate, formatElapsedTime(timer.elapsed(TimeUnit.MILLISECONDS)));
            if (timer.elapsed(TimeUnit.MILLISECONDS) > 0) {
                double bytesPerSecond = 1000.0d * fileMetrics.bytes / timer.elapsed(TimeUnit.MILLISECONDS);
                log.info("Throughput: {} per second", humanReadableByteCount(bytesPerSecond, false));
            } else {
                log.info("Cannot compute throughput; execution was too fast.");
            }

        } catch (FileNotFoundException e) {
            errorCondition = true;
            log.error("Input file not found", e);
        } catch (IOException e) {
            errorCondition = true;
            log.error("Error while loading input file on line {} and column {} (insert record number {})", inputFile.getLineNumber(),
               columnIndex, insertsCompleted, e);
        } catch (RuntimeException e) {
            errorCondition = true;
            log.error("Error while processing input file on line {} and column {} (insert record number {})",
               inputFile == null ? 0 : inputFile.getLineNumber(), columnIndex, insertsCompleted, e);
        } catch (Exception e) {
            errorCondition = true;
            log.error("Error while loading input file on line {} and column {} (insert record number {})",
               inputFile == null ? 0 : inputFile.getLineNumber(), columnIndex, insertsCompleted, e);
        } finally {
            log.info("Prepared statement cache statistics : {} ", preparedStatementCache.stats().toString());
            log.debug("Closing input file and session");
            try {
                linesProcessed = inputFile.getLineNumber();
            } catch (Exception e) {
                log.error("Unable to count lines processed", e);
            }
            CloseablesWrapper.closeQuietly(inputFile);
        }
        if (!errorCondition) {
            log.info("Load completed successfully. {} lines processed.", linesProcessed);
        }
        return errorCondition ? -1 : 0;
    }

    private List<String> determineInputColumns(List<String> headers,  List<String> tableColumns) {
        log.debug("Determining input columns for insert");
        log.debug("Input columns found({}): {}", tableColumns.size(), log.isDebugEnabled() ? Joiner.on(", ").join(tableColumns) : "");

        CaseInsensitiveSet tableColumnSet = new CaseInsensitiveSet();
        tableColumnSet.addAll(tableColumns);

        List<String> orderedCopyColumns = Lists.newArrayListWithExpectedSize(headers.size());

        CaseInsensitiveSet accountedTableColumns = new CaseInsensitiveSet();

        final List<String> errors = Lists.newLinkedList();
        final List<String> errorColumns = Lists.newLinkedList();

        Map<String, String> overrideHeaderToColumnMap = mappingConfig.getHeaderToColumnMap();

        boolean useStrictMapping = (!Strings.isNullOrEmpty(mappingConfig.isUseStrictMapping()) &&
                mappingConfig.isUseStrictMapping().equalsIgnoreCase("true")) ? true : false;
        int headerIndex = 0;

        for (String header : headers) {
            headerIndex++;
            String targetColumn;
            // check file headers against headerToColumn map in mappingConfig
            if (overrideHeaderToColumnMap.containsKey(header)) {
                // user specifically called out this header, so use the mapping they gave
                targetColumn = overrideHeaderToColumnMap.get(header);
                if (!tableColumnSet.contains(targetColumn)) {
                    errors.add("Error in header " + headerIndex + ": A mapping is configured from header " + header +
                            " to column " + targetColumn + " but the column was not found on the table.");
                    continue;
                }

                if (accountedTableColumns.contains(targetColumn)) {
                    errors.add("Error for header " + headerIndex + ": Column " + targetColumn +
                            " is already used for another header.");
                    continue;
                }
                accountedTableColumns.add(targetColumn);
                orderedCopyColumns.add(targetColumn);
            } else if(useStrictMapping){
                errors.add("File header "+ header +" not mapped to any column. Skipping");
            }
        }

        //If the header is not mapped but matches table column name, load the data to the column.
        if(!useStrictMapping) {
            for (String header : headers) {
                if(Strings.isNullOrEmpty(header)){
                    continue;
                }
                if (tableColumnSet.contains(header.toLowerCase()) && !accountedTableColumns.contains(header.toLowerCase())) {
                    orderedCopyColumns.add(header.toLowerCase());
                    //Add the matching file headers to the mapping config map.
                    mappingConfig.getHeaderToColumnMap().put(header, header.toLowerCase());
                    log.debug("File header {} not mapped but matches {} table column.", header, mappingConfig.getTable());
                } else if(!tableColumnSet.contains(overrideHeaderToColumnMap.get(header))){
                    log.debug("File header {} not mapped to any column. Skipping", header);
                }
            }
        }

        // check table columns against synthetics in mapping config
        mappingConfig.getSyntheticToColumnMap()
                .forEach((k, v) -> {
                    if (tableColumnSet.contains(k) && !orderedCopyColumns.contains(k)) {
                        if (!orderedCopyColumns.contains(k)) {
                            orderedCopyColumns.add(k);
                        }
                    } else {
                        errorColumns.add(k);
                    }
                });

        if(!errorColumns.isEmpty()) {
            errors.add("Error for synthetics: " + errorColumns.size() + " synthetics targeted non-existent columns. Synthetics: " + errorColumns.toString());
            errorColumns.clear();
        }

        // check table columns against timestamps in mapping config
        mappingConfig.getTimestampColumns()
                .stream()
                .filter(column -> {
                    if (tableColumnSet.contains(column) && !orderedCopyColumns.contains(column))
                        return true;
                    else {
                        errorColumns.add(column);
                        return false;
                    }
                })
                .forEach(orderedCopyColumns::add);

        if(!errorColumns.isEmpty()) {
            errors.add("Error for timestamps: " + errorColumns.size() + " timestamps targeted non-existent columns or columns that were already mapped. Timestamps: " + errorColumns.toString());
            errorColumns.clear();
        }

        // check table columns against line numbers in mapping config
        mappingConfig.getLineNumberColumns()
                .stream()
                .filter(column -> {
                    if (tableColumnSet.contains(column) && !orderedCopyColumns.contains(column))
                        return true;
                    else {
                        errorColumns.add(column);
                        return false;
                    }
                })
                .forEach(orderedCopyColumns::add);

        mappingConfig.getUuidColumns()
           .stream()
           .filter(column -> {
               if (tableColumnSet.contains(column) && !orderedCopyColumns.contains(column))
                   return true;
               else {
                   errorColumns.add(column);
                   return false;
               }
           })
           .forEach(orderedCopyColumns::add);

        if(!errorColumns.isEmpty()) {
            errors.add("Error for linenumber columns: " + errorColumns.size() + " linenumber columns targeted non-existent columns or columns that were already mapped. Linenumber columns: " + errorColumns.toString());
            errorColumns.clear();
        }

        if (!errors.isEmpty()) {
            log.error("{} error{} found while determining mapping from file headers to table columns",
                    errors.size(), (errors.size() == 1 ? "" : "s"));
            errors.forEach(log::error);
            throw new CLILoaderRuntimeException("Can't complete mapping from file headers to table columns");
        }
        return orderedCopyColumns;
    }

    private Map<String, DataType> determineTableColumnTypes(String[] columns) {
        log.debug("Determining table column types");
        Select select = QueryBuilder.select(columns)
           .from(configService.getValue(MAIN_KEYSPACE_KEY), mappingConfig.getTable())
           .limit(1);
        log.trace("CQL: {}", select.getQueryString());
        ResultSet rs = sessionManager.getSession().execute(select);
        ColumnDefinitions columnDefs = rs.getColumnDefinitions();
        Map<String, DataType> columnTypeMap = new TreeMap<>();
        for (Definition def : columnDefs) {
            columnTypeMap.put(def.getName().toLowerCase(), def.getType());
        }
        return columnTypeMap;
    }
}
