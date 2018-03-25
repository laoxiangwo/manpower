package loader.util;

import com.datastax.driver.core.DataType;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import loader.cli.CLILoaderRuntimeException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

public class CQLTypeUtil {
    private static final Logger log = LoggerFactory.getLogger(CQLTypeUtil.class);

    private CQLTypeUtil() {}

    /**
     * Using our best effort, convert String values (sourced from a file, usually)
     * to the Java type that corresponds to the CQL type.
     *
     * @param strValue The input value to convert
     * @param dataType The Datastax CQL type
     * @return The converted object
     * @throws CLILoaderRuntimeException if the conversion failed
     */
    public static Object convertObjectType(@Nullable String strValue, DataType dataType) {
        // short circuit nulls and blanks
        if (Strings.isNullOrEmpty(strValue)) {
            return null;
        }

        switch (dataType.getName())  {
            case ASCII    : //(1,  String.class),
                return strValue;
            case BIGINT   : //(2,  Long.class),
                return convertToLong(strValue);
            case BLOB     : //(3,  ByteBuffer.class),
                return convertToByteBuffer(strValue);
            case BOOLEAN  : //(4,  Boolean.class),
                return convertToBoolean(strValue);
            case COUNTER  : //(5,  Long.class),
                return convertToLong(strValue);
            case DECIMAL  : //(6,  BigDecimal.class),
                return convertToBigDecimal(strValue);
            case DOUBLE   : //(7,  Double.class),
                return convertToDouble(strValue);
            case FLOAT    : //(8,  Float.class),
                return convertToFloat(strValue);
            case INT      : //(9,  Integer.class),
                return convertToInteger(strValue);
            case TEXT     : //(10, String.class),
                return strValue;
            case TIMESTAMP: //(11, Date.class),
                return convertToDate(strValue);
            case UUID     : //(12, UUID.class),
                return convertToUUID(strValue);
            case VARCHAR  : //(13, String.class),
                return strValue;
            case VARINT   : //(14, BigInteger.class),
                return convertToBigInteger(strValue);
            case TIMEUUID : //(15, UUID.class),
                return convertToUUID(strValue);
            case LIST     : //(32, List.class),
                return convertToList(strValue, dataType);
            case SET      : //(34, Set.class),
                return convertToSet(strValue, dataType);
            case MAP      : //(33, Map.class),
                return convertToMap(strValue, dataType);
            case INET     : //(16, InetAddress.class),
                throw new CLILoaderRuntimeException("INET type not supported");
            case CUSTOM   : //(0,  ByteBuffer.class);
                throw new CLILoaderRuntimeException("CUSTOM CQL type not supported");
            default:
                throw new CLILoaderRuntimeException("Unknown CQL type " + dataType.getName() + " not supported");
        }

    }

    protected static ByteBuffer convertToByteBuffer(String strValue) {
        try {
            return ByteBuffer.wrap(strValue.getBytes());
        } catch (Exception e) {
            throw new CLILoaderRuntimeException("Unable to convert value to ByteBuffer: " + strValue, e);
        }
    }

    public static Boolean convertToBoolean(String strValue) {
        try {
            if ("T".equalsIgnoreCase(strValue) || "Y".equalsIgnoreCase(strValue) || "YES".equalsIgnoreCase(strValue)) {
                return Boolean.TRUE;
            }
            if ("F".equalsIgnoreCase(strValue) || "N".equalsIgnoreCase(strValue) || "NO".equalsIgnoreCase(strValue)) {
                return Boolean.FALSE;
            }
            return Boolean.valueOf(strValue);
        } catch (Exception e) {
            throw new CLILoaderRuntimeException("Unable to convert value to Boolean: " + strValue, e);
        }
    }

    public static Long convertToLong(String strValue) {
        try {
            return Long.valueOf(strValue);
        } catch (NumberFormatException e) {
            throw new CLILoaderRuntimeException("Unable to convert value to Long: " + strValue, e);
        }
    }

    protected static BigDecimal convertToBigDecimal(String strValue) {
        try {
            // if it has a decimal point, parse as double
            if (strValue.indexOf('.') != -1) {
                return BigDecimal.valueOf(Double.parseDouble(strValue));
            } else {
                // otherwise, parse as long
                return BigDecimal.valueOf(Long.parseLong(strValue));
            }
        } catch (NumberFormatException e) {
            throw new CLILoaderRuntimeException("Unable to convert value to BigDecimal: " + strValue, e);
        }
    }

    public static Double convertToDouble(String strValue) {
        try {
            return Double.valueOf(strValue);
        } catch (NumberFormatException e) {
            throw new CLILoaderRuntimeException("Unable to convert value to Double: " + strValue, e);
        }
    }

    public static Float convertToFloat(String strValue) {
        try {
            return Float.valueOf(strValue);
        } catch (NumberFormatException e) {
            throw new CLILoaderRuntimeException("Unable to convert value to Float: " + strValue, e);
        }
    }

    public static Integer convertToInteger(String strValue) {
        try {
            return Integer.valueOf(strValue);
        } catch (NumberFormatException e) {
            throw new CLILoaderRuntimeException("Unable to convert value to Integer: " + strValue, e);
        }
    }

    // we will only support a few date formats for now
    // no Euro-style day first formats!
    private static final DateTimeFormatter YYYYMMDD_Dashes_DateTimeFormatter =
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YYYYMMDD_Dashes_DateFormatter =
            DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MMDDYYYY_Slashes_DateTime24HFormatter =
            DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
    private static final DateTimeFormatter MMDDYYYY_Slashes_DateFormatter =
            DateTimeFormat.forPattern("MM/dd/yyyy");
    private static final DateTimeFormatter MMDDYYYY_Dashes_DateTime24HFormatter =
            DateTimeFormat.forPattern("MM-dd-yyyy HH:mm:ss");
    private static final DateTimeFormatter MMDDYYYY_Dashes_DateFormatter =
            DateTimeFormat.forPattern("MM-dd-yyyy");
    private static final DateTimeFormatter YYYYMMDD_Dashes_T_DateTimeFormatter =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    // this array defines the order in which we will apply date formats
    // to attempt parsing.
    private static final DateTimeFormatter DATE_PARSER_ORDERING[] =
            { YYYYMMDD_Dashes_DateTimeFormatter, YYYYMMDD_Dashes_DateFormatter, MMDDYYYY_Slashes_DateTime24HFormatter,
                MMDDYYYY_Slashes_DateFormatter, MMDDYYYY_Dashes_DateTime24HFormatter, MMDDYYYY_Dashes_DateFormatter,
                YYYYMMDD_Dashes_T_DateTimeFormatter};

    protected static Date convertToDate(String strValue) {
        for (DateTimeFormatter parser : DATE_PARSER_ORDERING) {
            try {
                return parser.parseDateTime(strValue).toDate();
            } catch (Exception e) {
                // swallow these
            }
        }
        throw new CLILoaderRuntimeException("Unable to convert value to Date using any known format: " + strValue);
    }

    protected static BigInteger convertToBigInteger(String strValue) {
        try {
            return BigInteger.valueOf(Long.parseLong(strValue));
        } catch (NumberFormatException e) {
            throw new CLILoaderRuntimeException("Unable to convert value to BigInteger: " + strValue, e);
        }
    }

    protected static UUID convertToUUID(String strValue) {
        try {
            return UUID.fromString(strValue);
        } catch (Exception e) {
            throw new CLILoaderRuntimeException("Unable to convert value to UUID: " + strValue, e);
        }
    }

    /**
     * Assumes list is delimited by commas
     * @param strValue Comma-delimited list of values
     * @param dataType The List datatype
     * @return a list of converted element values
     */
    protected static List<?> convertToList(String strValue, DataType dataType) {
        String elements[] = strValue.split(",");
        DataType elementType = dataType.getTypeArguments().get(0);
        List<Object> resultList = Lists.newArrayListWithExpectedSize(elements.length);
        for (String elementValue : elements) {
            try {
                resultList.add(convertObjectType(elementValue, elementType));
            } catch (Exception e) {
                throw new CLILoaderRuntimeException("Unable to convert value to List because a list element type conversion failed");
            }
        }
        return resultList;
    }

    /**
     * Assumes set is delimited by commas
     * @param strValue Comma-delimited list of values
     * @param dataType The Set datatype
     * @return a set of converted element values
     */
    protected static Set<?> convertToSet(String strValue, DataType dataType) {
        String elements[] = strValue.split(",");
        DataType elementType = dataType.getTypeArguments().get(0);
        Set<Object> resultSet = Sets.newHashSetWithExpectedSize(elements.length);
        for (String elementValue : elements) {
            try {
                resultSet.add(convertObjectType(elementValue, elementType));
            } catch (Exception e) {
                throw new CLILoaderRuntimeException("Unable to convert value to Set because a set element type conversion failed");
            }
        }
        return resultSet;
    }

    /**
     * Assumes map entries are delimited by commas, and key-value pairs are delimited by equals.
     * @param strValue Comma- and equals-delimited list of map entries
     * @param dataType The Map data type
     * @return a map of converted element keys and values
     */
    protected static Map<?,?> convertToMap(String strValue, DataType dataType) {
        String elements[] = strValue.split(",");
        DataType keyType = dataType.getTypeArguments().get(0);
        DataType valueType = dataType.getTypeArguments().get(1);
        Map<Object, Object> resultMap = Maps.newHashMapWithExpectedSize(elements.length);
        for (String kvPairStr : elements) {
            String kvPair[] = kvPairStr.split("=");
            if (kvPair.length != 2) {
                throw new CLILoaderRuntimeException("Unable to convert value to Map because a key-value pair is missing an =");
            }
            Object key = null;
            try {
                key = convertObjectType(kvPair[0], keyType);
            } catch (Exception e) {
                throw new CLILoaderRuntimeException("Unable to convert value to Set because a key type conversion failed");
            }
            Object value = null;
            try {
                value = convertObjectType(kvPair[1], valueType);
            } catch (Exception e) {
                throw new CLILoaderRuntimeException("Unable to convert value to Set because a value type conversion failed");
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }

}
