package cp.exceptions;


public class AuditorFailureException extends ProcessException {

    public AuditorFailureException(String message){
        super(message);
    }

    public AuditorFailureException(String message, Throwable throwable){
        super(message, throwable);
    }
}

