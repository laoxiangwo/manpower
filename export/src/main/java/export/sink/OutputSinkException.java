package export.sink;


import export.model.ExportException;

/**
 * Thrown when unable to write to an output target
 *
 * Created by shengli on 12/29/15.
 */
public class OutputSinkException extends ExportException {
    private static final long serialVersionUID = -5513611275021432356L;

    public OutputSinkException() {
    }

    public OutputSinkException(Throwable cause) {
        super(cause);
    }

    public OutputSinkException(String message) {
        super(message);
    }

    public OutputSinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutputSinkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
