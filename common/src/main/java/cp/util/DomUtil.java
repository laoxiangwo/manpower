package cp.util;

import com.google.common.escape.Escaper;
import com.google.common.xml.XmlEscapers;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DomUtil {
    private DomUtil() {
    }

    public static final Escaper XML_ESCAPER = XmlEscapers.xmlContentEscaper();

    /**
     * Escapes the given input for suitable inclusion within the content of
     * an xml node.
     * @param val The value to escape.
     * @return The escaped value.
     */
    public static String escape(String val) {
        return XML_ESCAPER.escape(val);
    }

    /**
     * Short hand method that will produce a string of:
     * <p/>
     *
     * {@code
     * <name>value</name>
     * }
     *
     * @param str The builder to append to.
     * @param name Name of node to create.
     * @param value Text content of node.
     * @return StringBuilder instance passed in.
     */
    public static StringBuilder cnode(StringBuilder str, String name, Object value) {
        String strValue = toString(value);

        if (isNullOrEmpty(strValue))
            return str;

        return str.append('<').append(name).append('>')
           .append(XML_ESCAPER.escape(strValue))
           .append("</").append(name).append('>');
    }

    /**
     * Short hand method that will produce a string of:
     * <p/>
     *
     * {@code
     * <name>value</name>
     * }
     *
     * @param str The builder to append to.
     * @param name Name of node to create.
     * @param value Text content of node.
     * @return StringBuilder instance passed in.
     */
    public static StringBuilder printCnode(StringBuilder str, String name, Object value) {
        String strValue = toString(value);

        if (isNullOrEmpty(strValue))
            return str;

        return str.append('<').append(name).append('>')
           .append(XML_ESCAPER.escape(strValue))
           .append("</").append(name).append('>')
           .append("\r\n");
    }

    private static String toString(Object value) {
        if (value == null)
            return null;

        return String.class.isInstance(value) ? (String) value : value.toString();
    }
}
