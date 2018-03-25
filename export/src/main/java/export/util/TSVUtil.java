package export.util;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shengli on 12/28/15.
 */
public class TSVUtil {
    public static final char TAB = '\t';
    public static final String TAB_STRING = "\t";

    public static List<String> parseTSVLine(String line) {
        if (Strings.isNullOrEmpty(line)) {
            return new ArrayList<>(1);
        }
        int tabCount = (int)line.chars().filter(value -> value == TAB).count();
        List<String> parsedHeaders = new ArrayList<>(tabCount + 1);
        final String[] splitLine = line.split(TAB_STRING, -1000);
        Collections.addAll(parsedHeaders, splitLine);
        return parsedHeaders;
    }

    public static String joinTSVLine(List<String> cellList) {
        return Joiner.on(TAB_STRING).useForNull("").join(cellList);
    }
}
