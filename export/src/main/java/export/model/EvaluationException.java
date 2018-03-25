package export.model;

/**
 * Exception thrown during the expression evaluation process,
 * indicating that evaluation failed in some way.
 *
 * Created by shengli on 12/27/15.
 */
public class EvaluationException extends ExportException {
    private static final long serialVersionUID = -7196030012521319608L;

    public EvaluationException() {
    }

    public EvaluationException(Throwable cause) {
        super(cause);
    }

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EvaluationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
