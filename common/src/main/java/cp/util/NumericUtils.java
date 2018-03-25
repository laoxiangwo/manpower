package cp.util;

/**
 * @author jzafeiropoulos
 */
public class NumericUtils {

    /**
     * Convenient function similar to Guava.Strings.isNullOrEmpty()
     * @param number to be checked
     * @return true if number is Null or Empty
     */
    public static boolean isNullOrZero( final Number number) {
        return null == number || number.doubleValue() == 0.0d;
    }

}
