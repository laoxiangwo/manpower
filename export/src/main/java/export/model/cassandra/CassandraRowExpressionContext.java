package export.model.cassandra;

import cp.util.SmartRow;
import com.datastax.driver.core.Row;
import com.google.common.base.Objects;
import export.model.ExpressionContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An expression context that binds a Cassandra query result row by the column identifiers.
 *
 * Created by shengli on 12/27/15.
 */
public class CassandraRowExpressionContext implements ExpressionContext {

    private SmartRow row;

    private int rowNumber;

    public CassandraRowExpressionContext(Row row, int rowNumber) {
        this.row = new SmartRow(row);
        this.rowNumber = rowNumber;
    }

    @Override
    public String name() {
        return "Row " + rowNumber;
    }

    @Override
    public Iterable<String> identifiers() {
        return StreamSupport.stream(row.getColumnDefinitions().spliterator(), false).map(input -> input == null ? null : input.getName()).collect(Collectors.toList());
    }

    @Override
    public Object resolve(String identifier) {
        return row.getObject(identifier);
    }

    @Override
    public String resolveString(String identifier) {
        return row.getString(identifier);
    }

    @Override
    public List<?> resolveList(String identifier) {
        return row.getList(identifier, Object.class);
    }

    @Override
    public Set<?> resolveSet(String identifier) {
        return row.getSet(identifier, Object.class);
    }

    @Override
    public Map<?, ?> resolveMap(String identifier) {
        return row.getMap(identifier, Object.class, Object.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CassandraRowExpressionContext that = (CassandraRowExpressionContext) o;
        return Objects.equal(row, that.row);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(row);
    }
}
