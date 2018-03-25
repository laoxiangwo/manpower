package export.model;

/**
 * Generic form of an export column expression.
 *
 * Created by shengli on 12/23/15.
 */
public interface ColumnExpression {

    /**
     * Evaluate the expression and provide the evaluation result.
     * @param context The binding context for expression variables.
     * @return The evaluation result
     */
    String evaluate(ExpressionContext context);

    /**
     * Return the column number for this column.
     * @return The column number for this column
     */
    int columnNumber();

    /**
     * Return the label for this column expression on the output
     * @return The label for this column expression
     */
    String label();

}
