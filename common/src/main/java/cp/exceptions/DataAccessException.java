package cp.exceptions;

/**
 * Created by misha on 2/4/15.
 */
@SuppressWarnings({"serial", "SuppressionAnnotation"})
public class DataAccessException extends ProcessException {

    public DataAccessException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
