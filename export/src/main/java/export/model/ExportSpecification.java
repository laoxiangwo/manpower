package export.model;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

/**
 * Created by shengli on 12/23/15.
 */
public class ExportSpecification {

    private String keyspace;

    private String table;

    private boolean allowFiltering;

    private String predicate;

    private List<String> columns;

    private List<ColumnExpression> outputExpressions;

    private Long limit;

    private Boolean omitHeader;

    public ExportSpecification() {
    }

    public boolean allowFiltering() {
        return this.allowFiltering;
    }

    public String keyspace() {
        return this.keyspace;
    }

    public String table() {
        return this.table;
    }

    public String predicate() {
        return this.predicate;
    }

    public List<String> columns() {
        return this.columns;
    }

    public List<ColumnExpression> outputExpressions() {
        return this.outputExpressions;
    }

    public ExportSpecification setAllowFiltering(final boolean allowFiltering) {
        this.allowFiltering = allowFiltering;
        return this;
    }

    public ExportSpecification setKeyspace(final String keyspace) {
        this.keyspace = keyspace;
        return this;
    }

    public ExportSpecification setTable(final String table) {
        this.table = table;
        return this;
    }

    public ExportSpecification setPredicate(final String predicate) {
        this.predicate = predicate;
        return this;
    }

    public ExportSpecification setColumns(final List<String> columns) {
        this.columns = columns;
        return this;
    }

    public ExportSpecification setOutputExpressions(final List<ColumnExpression> outputExpressions) {
        this.outputExpressions = outputExpressions;
        return this;
    }

    public Long limit() {
        return limit;
    }

    public ExportSpecification setLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    public Boolean getOmitHeader() {
        return omitHeader;
    }

    public ExportSpecification setOmitHeader(Boolean omitHeader) {
        this.omitHeader = omitHeader;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportSpecification that = (ExportSpecification) o;
        return allowFiltering == that.allowFiltering &&
                Objects.equal(keyspace, that.keyspace) &&
                Objects.equal(table, that.table) &&
                Objects.equal(predicate, that.predicate) &&
                Objects.equal(columns, that.columns) &&
                Objects.equal(limit, that.limit) &&
                Objects.equal(outputExpressions, that.outputExpressions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(keyspace, table, allowFiltering, predicate, columns, limit, outputExpressions);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("keyspace", keyspace)
                .add("table", table)
                .add("predicate", predicate)
                .add("allowFiltering", allowFiltering)
                .add("columns", columns == null ? null :
                        "[" + Joiner.on(", ").useForNull("null").join(columns) + "]")
                .add("outputExpressions", outputExpressions == null ? null :
                        "[" + Joiner.on(", ").useForNull("null").join(outputExpressions) + "]")
                .add("limit", limit)
                .toString();
    }
}
