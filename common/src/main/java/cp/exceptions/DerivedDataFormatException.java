package cp.exceptions;

/**
 * Created to represent specific problems encountered in the derived data
 */
public class DerivedDataFormatException extends ProcessException {
    private static final long serialVersionUID = 956713931201694381L;

    public DerivedDataFormatException(String message) {
        super(message);
    }

    public DerivedDataFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DerivedDataFormatException(Throwable throwable) {
        super(throwable);
    }
}
