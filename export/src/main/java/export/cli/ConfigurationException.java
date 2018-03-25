package export.cli;


import export.model.ExportException;

/**
 * Thrown when the configuration is bad.
 *
 * Created by shengli on 12/29/15.
 */
public class ConfigurationException extends ExportException {
    private static final long serialVersionUID = 6807080484172295547L;

    public ConfigurationException() {
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
