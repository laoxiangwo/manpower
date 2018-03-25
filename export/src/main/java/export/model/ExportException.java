package export.model;

/**
 * Superclass for all application exceptions thrown by export module code.
 * Created by shengli on 12/29/15.
 */
public class ExportException extends RuntimeException {
    private static final long serialVersionUID = 468667273953366406L;

    public ExportException() {
    }

    public ExportException(Throwable cause) {
        super(cause);
    }

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
