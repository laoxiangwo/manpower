package cp.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains commonly used/needed functionality as it relates to handling delimited (tsv, csv)
 * content.
 */
public class DelimitedUtil {
    private DelimitedUtil() {
    }

    public static final Splitter TAB_SPLIT = Splitter.on(CharMatcher.is('\t').precomputed()).trimResults();
    static final char DOUBLE_QUOTE = '"';

    public static void splitLine(char splitChar, String seq, String[] data) {
        char[] raw = seq.toCharArray();
        int dataIndex = 0;
        int lastIndex = 0;

        for (int i=0, nextI=1; dataIndex < data.length && i < raw.length; i++, nextI++) {
            if (raw[i] == splitChar || nextI == raw.length) {
                int segLength = i - lastIndex;

                if (nextI == raw.length)
                    segLength++;

                if (raw[lastIndex] == DOUBLE_QUOTE) {
                    data[dataIndex++] = unEscape(raw, lastIndex, segLength);
                } else {
                    if (segLength > 0) {
                        data[dataIndex++] = CharMatcher.ASCII.retainFrom(CharMatcher.BREAKING_WHITESPACE
                           .trimFrom(new String(raw, lastIndex, segLength)));
                    } else {
                        data[dataIndex++] = null;
                    }
                }

                lastIndex = i + 1;
            }
        }

        if (dataIndex < data.length)
            Arrays.fill(data, dataIndex, data.length - 1, null);
    }

    @SuppressWarnings("ConstantConditions")
    public static String[] splitLine(char splitChar, String seq) {
        char[] raw = seq.toCharArray();
        int lastIndex = 0;
        List<String> data = new ArrayList(16);

        for (int i=0, nextI=1; i < raw.length; i++, nextI++) {
            if (raw[i] == splitChar || nextI == raw.length) {
                int segLength = i - lastIndex;

                if (nextI == raw.length)
                    segLength++;

                if (raw[lastIndex] == DOUBLE_QUOTE) {
                    data.add(unEscape(raw, lastIndex, segLength));
                } else {
                    if (segLength > 0) {
                        data.add(CharMatcher.ASCII.retainFrom(CharMatcher.BREAKING_WHITESPACE
                           .trimFrom(new String(raw, lastIndex, segLength))));
                    } else {
                        data.add(null);
                    }
                }

                lastIndex = i + 1;
            }
        }

        return data.toArray(new String[data.size()]);
    }

    static String unEscape(char[] buf, int start, int length) {
        StringBuilder str = new StringBuilder(length);

        int offcount = start + length;
        char lastChar = buf[start] == DOUBLE_QUOTE ? DOUBLE_QUOTE : 0;

        while (start < offcount) {
            char curr = buf[start];

            if (curr != DOUBLE_QUOTE || (lastChar != DOUBLE_QUOTE && start + 1 < offcount)) {
                str.append(curr);
            }

            lastChar = curr;
            start++;
        }

        int endIdx = str.length() - 1;
        if (endIdx > -1 && str.charAt(endIdx) == DOUBLE_QUOTE)
            str.deleteCharAt(endIdx);

        return CharMatcher.ASCII.retainFrom(CharMatcher.BREAKING_WHITESPACE.trimFrom(str));
    }

    @Nullable
    public static String csvEscape(@Nullable String srcVal) {
        if (srcVal == null || srcVal.isEmpty())
            return srcVal;

        int pos = srcVal.indexOf(DOUBLE_QUOTE);
        if (pos < 0)
            return new StringBuilder(srcVal).insert(0, '"').append('"').toString();

        int len = srcVal.length();
        StringBuilder buf = new StringBuilder((len * 3 / 2) + 16)
           .append(DOUBLE_QUOTE);

        int oldpos = 0;
        do {
            buf.append(srcVal, oldpos, pos);
            buf.append('"').append('"');
            oldpos = pos + 1;
            pos = srcVal.indexOf(DOUBLE_QUOTE, oldpos);
        } while (pos != -1);

        buf.append(srcVal, oldpos, len)
           .append(DOUBLE_QUOTE);
        return buf.toString();
    }

    public static int length(@Nullable String str) {
        return str != null ? str.length() : 0;
    }

    @Nullable
    public static String clean(@Nullable String str) {
        if (str == null || str.trim().isEmpty())
            return null;

        return CharMatcher.BREAKING_WHITESPACE.trimFrom(str);
    }
}
