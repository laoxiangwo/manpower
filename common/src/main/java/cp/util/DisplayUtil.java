package cp.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Various utility methods common to implementing JSON based display logic.
 */
public class DisplayUtil {
    private DisplayUtil() {
    }

    public static final NumberFormat COUNT_FORMAT = NumberFormat.getInstance();
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static String formatInt(int val) {
        return COUNT_FORMAT.format(val);
    }

    public static Date parseDate(@Nullable String str) {
        String input = trim(str);

        return input != null ? DATE_FORMAT.parseDateTime(str).toDate() : null;
    }

    public static String format(@Nullable Date date) {
        if (date == null)
            return null;

        return DATE_FORMAT.print(date.getTime());
    }

    public static String formatHuman(@Nullable Date date) {
        if (date == null)
            return null;

        return DATE_FORMAT_HUMAN.print(date.getTime());
    }

    public static final DateTimeFormatter DATE_FORMAT_HUMAN = DateTimeFormat.forPattern("MM/dd/yyyy");

    public static final DateTimeFormatter DATE_FORMAT_MULTI = new DateTimeFormatterBuilder()
       .append(null, new DateTimeParser[]{
          DATE_FORMAT.getParser(),
          DATE_FORMAT_HUMAN.getParser(),
          DateTimeFormat.forPattern("MM-dd-yyyy").getParser()
       })
       .toFormatter();

    public static Date parseDateLenient(String str) {
        String input = trim(str);

        return input != null ? DATE_FORMAT_MULTI.parseDateTime(str).toDate() : null;
    }

    public static boolean isLenientDateParseable(String str) {
        try {
            parseDateLenient(str);
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private static final Joiner SCHEMA_JOIN = Joiner.on('.').skipNulls();
    private static final Function<String, String> TO_DISPLAY = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return indexString(input);
        }
    };
    private static final Function<String, String> TO_VALUE = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return stringValue(input);
        }
    };

    public static String toDisplay(Object value) {
        return toDisplay(stringValue(value));
    }

    /**
     * Returns a capitol cased version of the given value with extraneous characters removed.
     *
     * @param value The value to create a display string for.
     *
     * @return The humanized version of value, or null if empty or null value given.
     */
    public static String toDisplay(@Nullable String value) {
        String ret = TO_DISPLAY.apply(value);

        return ret != null ? ValueCache.intern(ret) : null;
    }

    public static <T> T nonNull(@Nullable T first, @Nullable T second) {
        if (first == null && second == null)
            return null;

        return ObjectUtil.firstNonNull(first, second);
    }

    /**
     * Formats the given values to something suitable as a schema expression like tablset.tablename.column.
     *
     * @param val The values to join as a schema expression.
     *
     * @return The joined schema expression.
     */
    public static String toSchema(String... val) {
        return ValueCache.intern(SCHEMA_JOIN.join(Lists.transform(Arrays.asList(val), TO_VALUE)));
    }

    public static String trim(@Nullable String value) {
        return value == null ? null : emptyToNull(value.trim());
    }

    public static String[] trim(String... vals) {
        for (int i = 0; i < vals.length; i++) {
            vals[i] = trim(vals[i]);
        }

        return vals;
    }

    public static String camelCaseClean(String input) {
        if (isNullOrEmpty(input))
            return null;

        return splitByCharacterTypeCamelCase(input, true);
    }

    public static String toStringTrimmed(Object input) {
        return trim(stringValue(input));
    }

    public static String stringValue(@Nullable Object input) {
        if (input == null)
            return null;

        return input.toString();
    }

    public static final CharMatcher INDEX_CHARS = CharMatcher.anyOf("_.,-").precomputed();
    public static final int MAX_HUMAN_CHARS = 60;

    /**
     * Replaces certain strings like "_" and "." with spaces to make querying results more likely to get hits for partial terms.
     *
     * @param input The input string to replace tokens in.
     *
     * @return The converted string.
     */
    public static String indexString(String input) {
        if (isNullOrEmpty(input))
            return null;

        String str = input.length() > MAX_HUMAN_CHARS ? input.substring(0, MAX_HUMAN_CHARS) : input;

        return splitByCharacterTypeCamelCase(str, true);
    }

    private static final Function<String, String> TO_CAMEL_CASE = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        }
    };

    public static String splitByCharacterTypeCamelCase(String str, boolean camelCase) {
        String c = INDEX_CHARS.trimAndCollapseFrom(str, ' ');
        if (c.isEmpty())
            return c;

        char[] raw = c.toCharArray();

        StringBuilder list = new StringBuilder(str.length() + 10);
        int tokenStart = 0;
        int currentType = Character.getType(c.charAt(tokenStart));

        for (int pos = tokenStart; pos < c.length(); pos++) {
            int type = Character.getType(c.charAt(pos));
            if (type == currentType)
                continue;

            if (camelCase && type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.append(TO_CAMEL_CASE.apply(new String(raw, tokenStart, newTokenStart - tokenStart))).append(' ');
                    tokenStart = newTokenStart;
                }
            } else {
                int count = pos - tokenStart;
                if (count > 0)
                    list.append(TO_CAMEL_CASE.apply(new String(raw, tokenStart, count))).append(' ');

                tokenStart = type == Character.SPACE_SEPARATOR ? pos + 1 : pos;
            }

            currentType = type;
        }

        int count = c.length() - tokenStart;
        if (count > 0)
            list.append(TO_CAMEL_CASE.apply(new String(raw, tokenStart, count))).append(' ');

        return list.toString().trim();
    }

    /**
     * @author rkadushin this constant represents the avaerge pixles per character. It is used to calculate if a sring exceeds the
     * displayable width of the select box in method getOptionAttribues. This must be changed for larger point fonts and different
     * font styles.
     */
    public static final int AVG_PIXELS_PER_CHAR = 7;
    public static final int ABBREV_PAD = 2;

    /**
     * The maximum number of characters that will be retained in a string passed in to {@link #clip(String)} before the remaining
     * characters are clipped off from end and replaced with ".." to denote a missing continuation of text.
     */
    public static final int SELECT_CLIP_MAX = AVG_PIXELS_PER_CHAR * 200;

    /**
     * Clips the end of the given string value with ".." replacement when the string length exceeds the default of {@link
     * #SELECT_CLIP_MAX}.
     *
     * @param val The string to optionally clip if max size exceeded.
     *
     * @return The original string if length is short enough or the clipped version of string.
     */
    public static String clip(String val) {
        return clip(val, SELECT_CLIP_MAX);
    }

    public static String clip(String val, int maxLength) {
        if (isNullOrEmpty(val) || val.length() < maxLength)
            return val;

        return Strings.padEnd(val.substring(0, maxLength - ABBREV_PAD), maxLength, '.');
    }

    public static String clipBegin(String val, int maxLength) {
        if (isNullOrEmpty(val) || val.length() < maxLength)
            return val;

        return Strings.padStart(val.substring(val.length() - (maxLength - ABBREV_PAD)), maxLength, '.');
    }
}
