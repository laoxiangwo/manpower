package export.model;

/**
 * Exception thrown during the value resolution process
 * indicating that binding an identifier to a value failed in some way.
 *
 * Created by shengli on 12/27/15.
 */
public class ResolutionException extends RuntimeException {
    private static final long serialVersionUID = 886453348139576375L;

    public ResolutionException() {
    }

    public ResolutionException(Throwable cause) {
        super(cause);
    }

    public ResolutionException(String message) {
        super(message);
    }

    public ResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
