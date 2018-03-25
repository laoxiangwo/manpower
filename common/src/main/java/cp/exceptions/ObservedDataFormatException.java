package cp.exceptions;

/**
 * Created to represent specific problems encountered in the observed data
 */
public class ObservedDataFormatException extends ProcessException {
    private static final long serialVersionUID = 9076225378995512950L;

    public ObservedDataFormatException(String message) {
        super(message);
    }

    public ObservedDataFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ObservedDataFormatException(Throwable throwable) {
        super(throwable);
    }
}
