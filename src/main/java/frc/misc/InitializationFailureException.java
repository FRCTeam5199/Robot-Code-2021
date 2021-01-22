package frc.misc;

public class InitializationFailureException extends RuntimeException {

    private String possibleFix;

    public InitializationFailureException() {
        super();
    }

    public InitializationFailureException(String message) {
        super(message);
    }

    public InitializationFailureException(String message, String possibleFix) {
        super(message);
        this.possibleFix = possibleFix;
    }

    public InitializationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationFailureException(Throwable cause) {
        super(cause);
    }

    public String getPossibleFix() {
        return this.possibleFix;
    }
}
