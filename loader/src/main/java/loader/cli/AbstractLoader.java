package loader.cli;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.google.common.collect.Lists;
import cp.config.ConfigurationService;
import cp.connect.CassandraSessionManager;
import cp.util.ObjectUtil;
import loader.util.CloseablesWrapper;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import static cp.config.ConfigurationKeys.*;

/**
 * Shared functionality between loader implementations
 */
public abstract class AbstractLoader {
    private static final Logger staticLog = LoggerFactory.getLogger(AbstractLoader.class);

    private static final int LINE_COUNT_FOR_ESTIMATE = 1000;



    protected static String mappingFilename;
    protected static String inputFileFilename;

    public static JSONObject loadJSONFromFile(String filename) throws FileNotFoundException {
        FileReader reader = null;
        try {
            reader = new FileReader(filename);
            JSONTokener tokener = new JSONTokener(reader);
            return new JSONObject(tokener);
        } finally {
            CloseablesWrapper.closeQuietly(reader);
        }
    }

    private static final PeriodFormatter ELAPSED_TIME_FORMATTER = new PeriodFormatterBuilder()
            .printZeroRarelyFirst()
            .appendHours()
            .appendSuffix("h")
            .appendSeparator(":")
            .appendMinutes()
            .appendSuffix("m")
            .appendSeparator(":")
            .appendSecondsWithOptionalMillis()
            .appendSuffix("s")
            .toFormatter();

    protected static String formatElapsedTime(long elapsedMillis) {
        Duration duration = Duration.millis(elapsedMillis);
        Period period = duration.toPeriod();
        return ELAPSED_TIME_FORMATTER.print(period);
    }

    protected static long estimateRemainingTime(int linesProcessed, long totalLines, long elapsed) {
        return linesProcessed == 0 ? -1 : (totalLines - linesProcessed) * elapsed / linesProcessed;
    }

    // borrowed almost completely wholesale from
    // http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    protected static String humanReadableByteCount(double bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
    }

    protected static class FileMetrics {
        public long bytes;
        public long lines;

        public String toString() {
            return ObjectUtil.toStringHelper(this)
                    .add("lines", lines)
                    .add("bytes", bytes)
                    .toString();
        }
    }

    // static fields and methods above
    //====================================================================================================
    // instance fields and methods below

    private final Logger instanceLog = LoggerFactory.getLogger(this.getClass());

    protected MappingConfig mappingConfig;
    protected ConfigurationService configService;
    protected String inputFilename;
    protected CassandraSessionManager sessionManager;

    public AbstractLoader(CassandraSessionManager sessionManager,
                          ConfigurationService configService,
                          MappingConfig mappingConfig,
                          String inputFilename) {
        this.sessionManager = sessionManager;
        this.configService = configService;
        this.mappingConfig = mappingConfig;
        this.inputFilename = inputFilename;
    }

    protected boolean determineIfTableExists() {
        instanceLog.debug("Determining if table {}.{} exists", configService.getValue(MAIN_KEYSPACE_KEY), mappingConfig.getTable());

        Where query = QueryBuilder.select("keyspace_name", "columnfamily_name").from("system", "schema_columnfamilies")
           .where(QueryBuilder.eq("keyspace_name", configService.getValue(MAIN_KEYSPACE_KEY)))
           .and(QueryBuilder.eq("columnfamily_name", mappingConfig.getTable()));

        instanceLog.debug("CQL: {}", query.getQueryString());
        try {
            return sessionManager.getSession().execute(query).one() != null;
        } catch (Exception e) {
            throw new CLILoaderRuntimeException("Unable to determine if table " +
                    configService.getValue(MAIN_KEYSPACE_KEY) + "." + mappingConfig.getTable() + " exists.", e);
        }
    }

    protected List<String> getTableColumns() {
        instanceLog.debug("Getting columns for table {}.{}", configService.getValue(MAIN_KEYSPACE_KEY), mappingConfig.getTable());

        Where query = QueryBuilder.select("keyspace_name", "columnfamily_name", "column_name", "type")
           .from("system", "schema_columns").where(QueryBuilder.eq("keyspace_name", configService.getValue(MAIN_KEYSPACE_KEY)))
           .and(QueryBuilder.eq("columnfamily_name", mappingConfig.getTable()));

        instanceLog.trace("CQL: {}", query.getQueryString());

        try {
            return Lists.transform(sessionManager.getSession().execute(query).all(), input -> input == null ? null : input.getString("column_name"));
        } catch (Exception e) {
            throw new CLILoaderRuntimeException("Unable to get columns for table " +
                    configService.getValue(MAIN_KEYSPACE_KEY) + "." + mappingConfig.getTable(), e);
        }
    }

    protected CommandLineLoader.FileMetrics estimateBytesAndLinesInFile(String filename) {
        instanceLog.debug("Estimating lines and bytes in file");
        LineNumberReader in = null;
        Charset utf8charset = Charset.forName("UTF-8");
        try {
            in = new LineNumberReader(new FileReader(filename));

            CommandLineLoader.FileMetrics metrics = new CommandLineLoader.FileMetrics();
            metrics.lines++; // start with the assumption of one line

            int lineNumber = 0;
            long byteCount = 0L;
            String line;
            do  {
                line = in.readLine();
                if (line != null) {
                    byteCount += line.getBytes().length + 1;
                    lineNumber++;
                }
            } while ((line != null) && lineNumber < LINE_COUNT_FOR_ESTIMATE);

            // if the file is empty, return 0 bytes and 0 lines
            if (lineNumber == 0L) {
                metrics.bytes = 0L;
                metrics.lines = 0L;
                return metrics;
            }

            // determine the file's full length in bytes
            File inputFile = new File(filename);
            metrics.bytes = inputFile.length();

            // if we read everything in the file, then report exact value
            if (lineNumber < LINE_COUNT_FOR_ESTIMATE) {
                instanceLog.info("Input file exact line count: {}", lineNumber);
                metrics.lines = lineNumber;
            } else if (byteCount > 0L) {
                // estimate the number of lines in that number of bytes
                metrics.lines = metrics.bytes * lineNumber / byteCount;
                instanceLog.info("Estimating input file line count at {} from {}/{} bytes in {} lines read",
                        metrics.lines, byteCount, metrics.bytes, lineNumber);
            }

            return metrics;
        } catch (IOException e) {
            throw new CLILoaderRuntimeException("Unable to read the input file", e);
        } finally {
            CloseablesWrapper.closeQuietly(in);
        }
    }

    /**
     * This is where subclasses do all the work.  this is declared here just to make subclasses conformant.
     */
    public abstract int load();
}
