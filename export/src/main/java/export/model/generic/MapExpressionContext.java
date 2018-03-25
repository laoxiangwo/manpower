package export.model.generic;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import export.model.ExpressionContext;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A basic expression context implementation that stores string values in a map.
 * This class is not thread safe, owing to reuse of regular expression {@code Matcher} static objects.
 *
 * Created by shengli on 12/28/15.
 */
@NotThreadSafe
public class MapExpressionContext implements ExpressionContext {
    private static final String INTEGER_REGEX = "^([-]?[0-9]+)$";
    private static final String DECIMAL_REGEX = "^([-]?[0-9]*\\.[0-9]+)$";
    private static final String BOOLEAN_REGEX = "^([Tt][Rr][Uu][Ee])|([Ff][Aa][Ll][Ss][Ee])$";
    private static final Pattern INTEGER_REGEXP = Pattern.compile(INTEGER_REGEX);
    private static final Pattern DECIMAL_REGEXP = Pattern.compile(DECIMAL_REGEX);
    private static final Pattern BOOLEAN_REGEXP = Pattern.compile(BOOLEAN_REGEX);
    private static final Matcher INTEGER_MATCHER = INTEGER_REGEXP.matcher("");
    private static final Matcher DECIMAL_MATCHER = DECIMAL_REGEXP.matcher("");
    private static final Matcher BOOLEAN_MATCHER = BOOLEAN_REGEXP.matcher("");

    private final Map<String, String> valueMap;

    public MapExpressionContext(Map<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    @Override
    public Iterable<String> identifiers() {
        return valueMap.keySet();
    }

    @Override
    public Object resolve(String identifier) {
        return convertType(valueMap.get(identifier));
    }

    private Object convertType(String strValue) {
        if (Strings.isNullOrEmpty(strValue)) {
            return strValue;
        }
        if (INTEGER_MATCHER.reset(strValue).matches()) {
            return Long.parseLong(strValue);
        }
        if (DECIMAL_MATCHER.reset(strValue).matches()) {
            return Double.parseDouble(strValue);
        }
        if (BOOLEAN_MATCHER.reset(strValue).matches()) {
            return Boolean.parseBoolean(strValue);
        }
        return strValue;
    }

    @Override
    public String resolveString(String identifier) {
        return valueMap.get(identifier);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("valueMap", Joiner.on(", ").withKeyValueSeparator("=").useForNull("null").join(valueMap))
                .toString();
    }
}
