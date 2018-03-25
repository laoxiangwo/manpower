package export.source.cassandra;

import cp.connect.CassandraSessionManager;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import export.model.ExportSpecification;
import export.model.ExpressionContext;
import export.model.cassandra.CassandraRowExpressionContext;
import export.source.ContextSource;
import export.source.ContextSourceException;

import java.util.Iterator;

/**
 * Supplies expression contexts corresponding to rows resulting from a Cassandra query.
 *
 * Created by shengli on 12/28/15.
 */
public class CassandraRowContextSupplier implements ContextSource {

    private CassandraSessionManager sessionManager;

    private ResultSet resultSet;

    private ExportSpecification exportSpec;

    public CassandraRowContextSupplier(CassandraSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Iterable<ExpressionContext> get() {
        // we are constructing the query as a String rather than as a Select object with QueryBuilder
        // because we don't want to try to parse the predicate from the export spec
        String selectStatement = buildSelectStatement();

        try {
            resultSet = sessionManager.getSession().execute(selectStatement) ;
        } catch (Exception e) {
            throw new ContextSourceException("Unable to execute select statement", e);
        }

        return () -> new Iterator<ExpressionContext>() {

            final Iterator<Row> rowIter = resultSet.iterator();
            int counter = 0;

            @Override
            public boolean hasNext() {
                return rowIter.hasNext();
            }

            @Override
            public ExpressionContext next() {
                try {
                    return new CassandraRowExpressionContext(rowIter.next(), ++counter);
                } catch (Exception e) {
                    throw new ContextSourceException("Unable to obtain next row from result set", e);
                }
            }
        };
    }

    protected String buildSelectStatement() {
        return "SELECT " + (Joiner.on(", ").skipNulls().join(exportSpec.columns())) +
                    " FROM " + exportSpec.keyspace() + "." + exportSpec.table() +
                    (Strings.isNullOrEmpty(exportSpec.predicate()) ? "" : " WHERE " + exportSpec.predicate()) +
                    (exportSpec.allowFiltering() ? " ALLOW FILTERING" : "");
    }


    @Override
    public void setExportSpecification(ExportSpecification exportSpecification) {
        this.exportSpec = exportSpecification;
    }

    public CassandraRowContextSupplier start() {
        try {
            if (!sessionManager.isSessionActive()) {
                sessionManager.startCassandraClient();
            }
        } catch (Exception e) {
            throw new ContextSourceException("Unable to start Cassandra client", e);
        }
        return this;
    }

    public CassandraRowContextSupplier stop() {
        try {
            if (sessionManager.isSessionActive()) {
                sessionManager.stopCassandraClient();
            }
        } catch (Exception e) {
            throw new ContextSourceException("Unable to stop Cassandra client", e);
        }
        return this;
    }
}
