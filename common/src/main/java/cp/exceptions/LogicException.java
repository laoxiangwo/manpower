package cp.exceptions;

/**
 * Created by misha on 2/4/15.
 */
public class LogicException extends ProcessException {

    public LogicException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LogicException(String message) {
        super(message);
    }
}
