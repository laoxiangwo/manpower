package cp.exceptions;

/**
 * Encapsulates general errors thrown during
 * person loading process.
 */
public class PersonLoadException extends ProcessException {
    private static final long serialVersionUID = -2961935888014602137L;

    public PersonLoadException(Throwable throwable) {
        super(throwable);
    }

    public PersonLoadException(String message) {
        super(message);
    }

    public PersonLoadException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
