package cp.exceptions;

/**
 * Exception type to represent misconfiguration of artifacts
 */
public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = -690078846967187175L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ConfigurationException(Throwable throwable) {
        super(throwable);
    }
}
