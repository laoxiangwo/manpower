package testUtil;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import cp.exceptions.TestExecutionException;
import cp.util.FileUtil;
import org.hsqldb.jdbc.JDBCDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

/**
 * Created by shengli on 3/13/16.
 */
public class MetadataTestDataSource extends JDBCDataSource {
    private static final long serialVersionUID = -5260483808225534461L;
    private static final Logger log = LoggerFactory.getLogger(MetadataTestDataSource.class);

    public MetadataTestDataSource() {
        setUser("sa");
        setPassword("");
        setUrl("jdbc:hsqldb:mem:test;hsqldb.tx=mvcc");
        resetDatabase();
    }

    private static final Splitter SQL_SPLIT = Splitter.on(';').trimResults().omitEmptyStrings();
    private static final Pattern SQL_COMMENT_LINE = Pattern.compile("^--.*$", Pattern.MULTILINE);

    private static String stripComments(String sql) {
        return Strings.emptyToNull(SQL_COMMENT_LINE.matcher(sql).replaceAll("").trim());
    }

    private void resetDatabase() {
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            statement.execute("DROP SCHEMA PUBLIC CASCADE");
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            throw new TestExecutionException("Unable to reset database", e);
        }

    }

    /**
     * Convenience method for setting up DB environment for testing.
     *
     * @param sqlStatements One or more SQL statements, obvs separated by semicolons.
     */
    public void runSql(String sqlStatements) {
        try (Connection conn = getConnection(); Statement st = conn.createStatement();){
            for (String sqlStatement : SQL_SPLIT.split(sqlStatements)) {
                sqlStatement = stripComments(sqlStatement);
                if (Strings.isNullOrEmpty(sqlStatement))
                    continue;

                if (log.isTraceEnabled())
                    log.trace("Executing: {}", sqlStatement);

                try {
                    st.execute(sqlStatement);
                } catch (Exception e) {
                    if (e instanceof SQLException) {
                        SQLException sqlE = (SQLException)e;
                        while (sqlE.getNextException() != null) {
                            log.error("SQL Exception: ", sqlE);
                            sqlE = sqlE.getNextException();
                        }
                    }
                    throw new TestExecutionException("Unable to execute SQL statement: " + sqlStatement, e);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            throw new TestExecutionException("Unable to connect to test DB", e);
        }
    }

    /**
     * Runs SQL from sql script that exists on class path
     *
     * @param resourcePath A path to the resource
     */
    public void runSqlFromResource(String resourcePath) {
        String sqlScript;
        try {
            sqlScript = CharStreams.toString(FileUtil.resourceReader(getClass(), resourcePath));
        } catch (IOException e) {
            throw new TestExecutionException("Unable to locate and load resource on classpath: " + resourcePath, e);
        }

        try {
            runSql(sqlScript);
        } catch (Exception e) {
            throw new TestExecutionException("Unable to execute SQL in script " + resourcePath, e);
        }
    }
}
