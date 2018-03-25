package export.model.generic;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import export.model.ColumnExpression;
import export.model.ExpressionContext;


/**
 * Constant value synthetic. Useful for short circuiting the
 * python interpreter when a simple constant value is desired.
 *
 * Created by shengli on 12/27/15.
 */
public class ConstantColumnExpression implements ColumnExpression {

    private final String label;
    private final String constantValue;
    private final int columnNumber;

    public ConstantColumnExpression(String label, String constantValue, int columnNumber) {
        this.label = label;
        this.constantValue = constantValue;
        this.columnNumber = columnNumber;
    }

    @Override
    public String evaluate(ExpressionContext context) {
        return constantValue;
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
        ConstantColumnExpression that = (ConstantColumnExpression) o;
        return Objects.equal(constantValue, that.constantValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(constantValue);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("label", label)
                .add("columnNumber", columnNumber)
                .add("value", constantValue)
                .toString();
    }
}
