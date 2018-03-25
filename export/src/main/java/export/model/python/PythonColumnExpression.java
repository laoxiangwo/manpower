package export.model.python;

import com.google.common.base.MoreObjects;
import export.model.ColumnExpression;
import export.model.EvaluationException;
import export.model.ExpressionContext;
import org.python.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Python implementation of an export column expression.
 *
 * Created by shengli on 12/27/15.
 */
public class PythonColumnExpression implements ColumnExpression {
    private static final Logger log = LoggerFactory.getLogger(PythonColumnExpression.class);

    private String label;

    private String expression;

    private int columnNumber;

    private PythonEngine pyEngine;

    public PythonColumnExpression() {
    }

    public PythonColumnExpression(String label, String expression, int columnNumber, PythonEngine pyEngine) {
        this.label = label;
        this.expression = expression;
        this.pyEngine = pyEngine;
        this.columnNumber = columnNumber;
    }

    public String expression() {
        return expression;
    }

    public PythonColumnExpression setExpression(String expression) {
        this.expression = expression;
        return this;
    }

    public PythonColumnExpression setLabel(String label) {
        this.label = label;
        return this;
    }

    public PythonEngine pyEngine() {
        return pyEngine;
    }

    public PythonColumnExpression setPyEngine(PythonEngine pyEngine) {
        this.pyEngine = pyEngine;
        return this;
    }

    public PythonColumnExpression setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
        return this;
    }

    @Override
    public int columnNumber() {
        return columnNumber;
    }

    @Override
    public String evaluate(ExpressionContext context) {
        pyEngine.bindContext(context);
        PyObject result = null;
        try {
            if (log.isTraceEnabled()) {
                log.trace("Evaluating context {} column {} expression {}", context.name(),
                        columnNumber, expression);
            }
            result = pyEngine.interpreter().eval(expression);
            return convertResult(result);
        } catch (Exception e) {
            throw new EvaluationException(
                    String.format("Evaluation failed for context %s column %d expression %s result %s",
                            context.name(), columnNumber, expression, result), e);
        }
    }

    private String convertResult(PyObject result) {
        if (result == null || result instanceof PyNone) {
            return null;
        }
        if (result.isNumberType()) {
            if (result instanceof PyInteger || result instanceof PyLong) {
                return Long.toString(Py.py2long(result));
            } else {
                return Double.toString(Py.py2double(result));
            }
        }
        if (result instanceof PyString) {
            return result.asStringOrNull();
        }

        // don't know how to convert anything else to acceptable string output
        // TODO may be possible to convert dictionaries, arrays and sequences to an acceptable string representation
        throw new EvaluationException("Unable to convert Python expression result: " + result);
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("label", label)
                .add("columnNumber", columnNumber)
                .add("expression", expression)
                .toString();
    }
}
