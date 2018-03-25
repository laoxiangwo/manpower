package cp.services;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import cp.exceptions.TestExecutionException;
import cp.testsupport.supportmodel.StringCQLDataSet;
import cp.util.DelimitedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Utility methods to be used when building a unit test class that extends {@code AbstractCassandraUnit4CQLTestNGCase}
 *
 * Created by shengli on 10/16/15.
 *
 * @see AbstractCassandraUnit4CQLTestNGCase
 */
public class CassandraTestSupport {
    private static final Logger log = LoggerFactory.getLogger(CassandraTestSupport.class);
    public static final char SEMICOLON = ';';

    public static StringCQLDataSet generateCQLStructure(String keyspace) {
        String fullScript = generateMainCQLSchema(keyspace);

        log.info("The full script:\n{}", fullScript);

        return new StringCQLDataSet(fullScript, keyspace);

    }

    public static StringCQLDataSet generateCQLMainEnvironment(String keyspace, String filename) {
        String fullScript = generateMainCQLSchema(keyspace) + "\n\n" + generateCQLInserts(filename);

        log.info("The full script:\n{}", fullScript);

        return new StringCQLDataSet(fullScript, keyspace);
    }

    public static StringCQLDataSet generateCQLReferenceEnvironment(String keyspace, String filename) {
        String fullScript = generateReferenceCQLSchema(keyspace) + "\n\n" + generateCQLInserts(filename);

        log.info("The full script:\n{}", fullScript);

        return new StringCQLDataSet(fullScript, keyspace);
    }

    public static StringCQLDataSet generateFullCQLEnvironment(String mainKeyspace, String referenceKeyspace, String filename) {
        String fullScript = generateMainCQLSchema(mainKeyspace) + "\n\n" +
                generateReferenceCQLSchema(referenceKeyspace) + "\n\n" +
                generateCQLInserts(filename);

        log.info("The full script:\n{}", fullScript);

        return new StringCQLDataSet(fullScript, mainKeyspace);
    }


    public static StringCQLDataSet generateCQLMainEnvironment(String keyspace, String ...filenames) {
        log.debug("Generating CQL environment for files: {}", Joiner.on(", ").join(filenames));
        StringBuilder fullScriptSB = new StringBuilder(generateMainCQLSchema(keyspace));
        for (String filename : filenames) {
            fullScriptSB.append("\n\n").append(generateCQLInserts(filename));
        }
        String fullScript = fullScriptSB.toString();

        log.info("The full script:\n{}", fullScript);

        return new StringCQLDataSet(fullScript, keyspace);
    }

    public static String generateMainCQLSchema(String keyspace) {
        return generateCQLSchema(keyspace, "cassandra_schema.cql");
    }

    public static String generateReferenceCQLSchema(String keyspace) {
        return generateCQLSchema(keyspace, "reference.cql");
    }

    private static String generateCQLSchema(String keyspace, String filename) {
        File tablesCqlFile = locateCQLFile(filename);
        String schemaCreation;
        try {
            schemaCreation = Files.toString(tablesCqlFile, Charsets.UTF_8);
        } catch (IOException e) {
            throw new TestExecutionException("Unable to load " + filename, e);
        }

        // we need to remove the first 2 statements that construct the keyspace and use it
        int firstSemicolonPosition = schemaCreation.indexOf(";");
        int secondSemicolonPosition = schemaCreation.indexOf(";", firstSemicolonPosition + 1);
        schemaCreation = schemaCreation.substring(secondSemicolonPosition + 1);

        // and remove all the drop and truncate statements
        schemaCreation = schemaCreation.replaceAll("DROP TABLE.*?;", "");
        schemaCreation = schemaCreation.replaceAll("TRUNCATE.*?;", "");

        // now we construct our own keyspace and use it
        String keyspaceSetupStatement = "CREATE KEYSPACE IF NOT EXISTS " +  keyspace +
                " WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '1'};";
        String usageStatement = "USE " + keyspace + ";";

        return keyspaceSetupStatement + "\n\n" + usageStatement + "\n\n" + schemaCreation;
    }

    public static String generateCQLInserts(String filename) {
        File lookupCqlTestData = locateCQLFile(filename);
        String dataInsertion;
        try {
            dataInsertion = Files.toString(lookupCqlTestData, Charsets.UTF_8);
        } catch (IOException e) {
            throw new TestExecutionException("Unable to load " + filename, e);
        }

        return dataInsertion;
    }

    /**
     * Looks in src/test/scripts/cql, src/main/scripts/cql, and on the resource path
     * @param filename The file to look for
     * @return The {@code File} object
     */
    public static File locateCQLFile(String filename) {
        File file = new File("src/test/scripts/cql/" + filename);
        if (file.exists()) {
            log.debug("Located file {} at {}", filename, file);
            return file;
        }
        file = new File("src/main/scripts/cql/" + filename);
        if (file.exists()) {
            log.debug("Located file {} at {}", filename, file);
            return file;
        }
        try {
            file = new File(Resources.getResource(filename).toURI());
            log.debug("Located file {} at {}", filename, file);
            return file;
        } catch (URISyntaxException e) {
            throw new TestExecutionException("Unable to process filename as URI" + filename, e);
        }
    }

    /**
     * Load a table from a TSV reader. Expects the first line of the reader input
     * to be headers that identify table columns, and every line should have the same number of items
     *
     * @param table The table to load
     * @param in The reader
     * @throws IOException if something goes wrong.
     */
    public static void loadFromTSV(Session session, String keyspace, String table, Reader in) throws IOException {
        LineNumberReader reader = (in instanceof LineNumberReader) ? (LineNumberReader)in : new LineNumberReader(in);
        String line;

        // read header line
        line = reader.readLine();
        if (line == null) {
            log.error("Asked to load from empty reader");
            return;
        }
        List<String> headers = DelimitedUtil.TAB_SPLIT.splitToList(line);
        if (headers.isEmpty()) {
            throw new IllegalArgumentException("Header line was empty");
        }

        while ((line = reader.readLine()) != null) {
            List<String> cells = DelimitedUtil.TAB_SPLIT.splitToList(line);
            if (cells.size() != headers.size()) {
                throw new IllegalArgumentException(String.format("Line %d had %d cells but header had %d. Line: %s",
                        reader.getLineNumber(), cells.size(), headers.size(), line));
            }

            // construct insert statement
            Insert insert = QueryBuilder.insertInto(keyspace, table);
            for (int i = 0; i < headers.size(); i++) {
                // skip blanks
                String cell = cells.get(i);
                if (cell.isEmpty()) {
                    continue;
                }
                insert.value(headers.get(i), cells.get(i));
            }
            try {
                session.execute(insert);
            } catch (Exception e) {
                throw new RuntimeException("Failed while running insert for TSV load on line " +
                        reader.getLineNumber() + ". Line: " + line, e);
            }
        }
        log.debug("Read {} lines", reader.getLineNumber());
    }

    /**
     * Given a session and a file, execute the CQL statements within that file.
     *
     * @param session A Cassandra session
     * @param cqlStatementFile A file containing CQL statement text
     */
    public static void runFromFile(Session session, File cqlStatementFile) {
        try (FileReader reader = new FileReader(cqlStatementFile)) {
            runFromReader(session, reader);
        } catch (IOException e) {
            throw new TestExecutionException("Unable to open file " + cqlStatementFile, e);
        }

    }

    /**
     * Given a session and a Reader supplying CQL statement text, execute the CQL statements within the given session.
     *
     * @param session A Cassandra session
     * @param cqlStatementReader A reader supplying CQL statement text
     */
    public static void runFromReader(Session session, Reader cqlStatementReader) {
        StringBuilder builder = new StringBuilder();

        int ch;
        try {
            while ((ch = cqlStatementReader.read()) != -1) {
                builder.append((char)ch);
                if (ch == SEMICOLON) {
                    String cql = builder.toString();
                    log.debug("Executing CQL: {}",cql);
                    session.execute(cql);
                    builder = new StringBuilder();
                }
            }
        } catch (IOException e) {
            throw new TestExecutionException("Unable to read CQL statements", e);
        }
        if (builder.length() > 0) {
            String cql = builder.toString();
            cql = cql.trim();
            if (!cql.isEmpty()) {
                log.debug("Executing CQL: {}", cql);
                session.execute(cql);
            }
        }
    }


}
