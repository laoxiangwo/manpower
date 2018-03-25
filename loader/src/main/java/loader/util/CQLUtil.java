package loader.util;

import com.google.common.base.Strings;

import java.util.Objects;

/**
 * Ridiculous that these things are even necessary.
 */
public class CQLUtil {

    public static enum CQLKeyword {
        ADD, ALL, ALLOW, ALTER, AND, ANY, APPLY, ASC, ASCII, AUTHORIZE, BATCH, BEGIN, BIGINT,
        BLOB, BOOLEAN, BY, CLUSTERING, COLUMNFAMILY, COMPACT, COUNT, COUNTER, CONSISTENCY,
        CREATE, DECIMAL, DELETE, DESC, DOUBLE, DROP, EACH_QUORUM, FILTERING, FLOAT, FROM,
        GRANT, IN, INDEX, INET, INSERT, INT, INTO, KEY, KEYSPACE, KEYSPACES, LEVEL, LIMIT,
        LIST, LOCAL_ONE, LOCAL_QUORUM, MAP, MODIFY, NORECURSIVE, NOSUPERUSER, OF, ON, ONE,
        ORDER, PASSWORD, PERMISSION, PERMISSIONS, PRIMARY, QUORUM, RENAME, REVOKE, SCHEMA,
        SELECT, SET, STORAGE, SUPERUSER, TABLE, TEXT, TIMESTAMP, TIMEUUID, TO, TOKEN, THREE,
        TRUNCATE, TTL, TWO, TYPE, UNLOGGED, UPDATE, USE, USER, USERS, USING, UUID, VALUES,
        VARCHAR, VARINT, WHERE, WITH, WRITETIME;

        // Why don't all enums provide this functionality by default?
        // Case-sensitivity does not belong in enum values.
        public static CQLKeyword parse(String text) {
            for (CQLKeyword value : values()) {
                if (value.toString().equalsIgnoreCase(text)) {
                    return value;
                }
            }
            return null;
        }
    }

    /**
     * Nobody needs to construct one of these.
     */
    private CQLUtil() {}

    /**
     * Determine if a CQL identifier will need escaping
     * @param identifier
     * @return
     */
    public static boolean identifierNeedsEscaping(String identifier) {
        return Objects.equals(identifier, escapeIdentifier(identifier));
    }

    /**
     * If the input string is not a valid CQL identifier, escape it with quotes.
     * @param identifier
     * @return
     */
    public static String escapeIdentifier(String identifier) {
        // ensure it's not empty. if so, that's the caller's problem
        if (Strings.isNullOrEmpty(identifier)) {
            return identifier;
        }

        // ensure it's not a CQL keyword
        if (CQLKeyword.parse(identifier) != null) {
            return '"' + identifier + '"';
        }

        // ensure that we have all alphanumeric characters
        boolean allAlphaNumericUnderscore = true;
        for (int i = 0; i < identifier.length() && allAlphaNumericUnderscore; i++) {
            char c = identifier.charAt(i);
            allAlphaNumericUnderscore =
                    (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_');
        }
        if (!allAlphaNumericUnderscore) {
            return new StringBuilder().append('"').append(escapeDoubleQuotes(identifier)).append('"').toString();
        }

        // ensure that it starts with a letter or underscore and not a digit
        char c = identifier.charAt(0);
        if (Character.isDigit(c)) {
            // this might be completely illegal, not sure
            return '"' + identifier + '"';
        }

        // at this point, we should be clean
        return identifier;
    }

    /**
     * Escape any double quotes in this identifier by replacing them with double-doublequotes.
     * @param identifier
     * @return
     */
    protected static CharSequence escapeDoubleQuotes(String identifier) {
        if (identifier.contains("\"")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < identifier.length(); i++) {
                char c = identifier.charAt(i);
                if (c == '"') {
                    sb.append('"').append('"');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return identifier;
        }
    }

    /**
     * If the input string contains single quotes, double them.
     * @param literal
     * @return
     */
    public static String escapeLiteral(String literal) {
        if (literal.contains("'")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < literal.length(); i++) {
                char c = literal.charAt(i);
                if (c == '\'') {
                    sb.append('\'').append('\'');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return literal;
        }
    }

}
