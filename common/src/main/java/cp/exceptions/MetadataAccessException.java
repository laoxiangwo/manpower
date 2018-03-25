package cp.exceptions;

/**
 * Exception class for metadata access.
 */
public class MetadataAccessException extends ProcessException {

    public MetadataAccessException(String message) {
        super(message);
    }

    public MetadataAccessException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MetadataAccessException(Throwable throwable) {
        super(throwable);
    }
}
