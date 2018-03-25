package export.model.python;

import export.model.ExpressionContext;
import export.model.ResolutionException;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thin wrapper around a Jython {@code PythonInterpreter}
 * that adds in a few utility methods.
 *
 * Created by shengli on 12/27/15.
 */
public class PythonEngine {
    private static final Logger log = LoggerFactory.getLogger(PythonEngine.class);

    private PythonInterpreter pyInterp;

    public PythonEngine(PythonInterpreter pyInterp) {
        this.pyInterp = pyInterp;
    }

    public PythonInterpreter interpreter() {
        return pyInterp;
    }

    /**
     * Bind all the values in the expression context into Python variables.
     *
     * @param context The execution context providing the bindings.
     */
    public void bindContext(ExpressionContext context) {
        log.trace("Binding context: {}", context.name());
        int counter = 0;
        for (String identifier : context.identifiers()) {
            // find the ground truth value
            Object value = context.resolve(identifier);

            // execute the statement
            try {
                log.trace("Python binding: {identifier: {}, value: {}}", identifier, value);
                pyInterp.set(identifier, value);
            } catch (Exception e) {
                throw new ResolutionException("Unable to bind identifier " + identifier + " to value " + value, e);
            }
            counter++;
        }
        log.trace("Bound {} statements from context {}", counter, context.name());
    }

    /**
     * Given an identifier and an object, construct the Python statement that will
     * assign the value to the identifier. Correctly handles varying types
     * and {@code null} values.
     *
     * @param identifier The identifier to assign
     * @param value The value to bind
     * @return The executable python statement
     */
    @Deprecated // TODO may not need this anymore
    public static String constructBindingStatement(String identifier, Object value) {
        String pyStatement;
        if (value == null) {
            pyStatement = identifier + " = None";
        } else {
            if (value instanceof String) {
                pyStatement = identifier + " = " + encodePythonStringLiteral((String)value);
            } else if (value instanceof Number) {
                pyStatement = identifier + " = " + value;
            } else if (value instanceof Boolean) {
                pyStatement = identifier + " = " + (((Boolean)value) ? "True" : "False");
            } else {
                throw new ResolutionException(
                        String.format("Unable to bind value for identifier %s (class %s) into context. Unsupported.",
                                identifier, value.getClass().getName()));
            }
        }
        return pyStatement;
    }

    /**
     * Given a Java string object, write it as a Python string literal.
     * Will handle escapes and unicode characters.
     * Wraps the string in single quotes.
     *
     * @param value The native Java string
     * @return The Python string literal equivalent, suitable for embedding in a Python statement.
     */
    public static String encodePythonStringLiteral(String value) {
        return PyString.encode_UnicodeEscape(value, true);
    }

}
