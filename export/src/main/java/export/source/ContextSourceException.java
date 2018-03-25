package export.source;


import export.model.ExportException;

/**
 * Thrown when a context source cannot be initialized or generate evaluation contexts.
 *
 * Created by shengli on 12/28/15.
 */
public class ContextSourceException extends ExportException {

    private static final long serialVersionUID = -5411881829094536160L;

    public ContextSourceException() {
    }

    public ContextSourceException(Throwable cause) {
        super(cause);
    }

    public ContextSourceException(String message) {
        super(message);
    }

    public ContextSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
