package cp.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by misha on 2/4/15.
 */
public class ProcessException extends RuntimeException {

    protected Logger logger;

    public ProcessException(Throwable throwable) {
        super(throwable);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public ProcessException(String message) {
        super(message);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public ProcessException(String message, Throwable throwable) {
        super(message, throwable);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public void warn() {
        logger.warn(super.getMessage());
    }

    public void debug() {
        logger.debug(super.getMessage());
    }

    public void trace() {
        logger.debug(super.getMessage());
        logger.trace(Arrays.toString(super.getStackTrace()));
    }
}
