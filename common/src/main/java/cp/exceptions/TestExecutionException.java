package cp.exceptions;

/**
 * Exception for test harness purposes
 */
public class TestExecutionException extends ProcessException {

    public TestExecutionException(String message) {
        super(message);
    }

    public TestExecutionException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TestExecutionException(Throwable throwable) {
        super(throwable);
    }
}
