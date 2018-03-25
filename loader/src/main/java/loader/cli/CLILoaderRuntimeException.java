package loader.cli;

/**
 * Generic exception superclass
 */
public class CLILoaderRuntimeException extends RuntimeException {
    public CLILoaderRuntimeException() {
    }

    public CLILoaderRuntimeException(Throwable cause) {
        super(cause);
    }

    public CLILoaderRuntimeException(String message) {
        super(message);
    }

    public CLILoaderRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CLILoaderRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
