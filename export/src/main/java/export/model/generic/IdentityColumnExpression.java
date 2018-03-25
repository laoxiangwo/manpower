package export.model.generic;


import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import export.model.ColumnExpression;
import export.model.ExpressionContext;


/**
 * A column expression type that simply evaluates an identifier and returns it.
 * Useful for short-circuiting the python interpreter when it is not needed.
 *
 * Created by shengli on 12/27/15.
 **/
public class IdentityColumnExpression implements ColumnExpression {

    private final String label;
    private final String identifier;
    private final int columnNumber;


    public IdentityColumnExpression(String label, String identifier, int columnNumber) {
        this.label = label;
        this.identifier = identifier;
        this.columnNumber = columnNumber;
    }

    @Override
    public String evaluate(ExpressionContext context) {
        return String.valueOf(context.resolve(identifier));
    }

    @Override
    public int columnNumber() {
        return columnNumber;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentityColumnExpression that = (IdentityColumnExpression) o;
        return Objects.equal(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("label", label)
                .add("columnNumber", columnNumber)
                .add("identifier", identifier)
                .toString();
    }
}