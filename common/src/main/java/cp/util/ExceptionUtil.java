package cp.util;

import com.google.common.base.Strings;

public class ExceptionUtil {

    /**
     * Instead of showing a long stack trace, summarize the stack trace in a nice neat block of text.
     *
     * @param t The throwable supplying the stack trace
     * @return A formatted string
     */
    public static String summarizeThrowableChain(Throwable t) {
        StringBuilder str = new StringBuilder();
        while (t != null) {
            if (str.length() == 0) {
                str.append("Cause: ");
            } else {
                str.append("        Caused by: ");
            }

            StackTraceElement stackTrace[] = t.getStackTrace();
            str.append(t.getClass().getSimpleName())
                    .append(" in ")
                    .append(stackTrace[0].getFileName())
                    .append(" at line ")
                    .append(stackTrace[0].getLineNumber())
                    .append(": ");

            str.append(Strings.isNullOrEmpty(t.getMessage()) ? "No message" : t.getMessage());
            str.append("\n\n");
            t = t.getCause();
        }
        return str.toString();

    }

}
