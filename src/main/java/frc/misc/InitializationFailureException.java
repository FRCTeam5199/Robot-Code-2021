package frc.misc;

public class InitializationFailureException extends RuntimeException {
    /**
     * Include this to help others debug in the future
     *
     * @see InitializationFailureException#getPossibleFix()
     */
    private final String possibleFix;

    /**
     * Creates new exception that includes a possible fix.
     *
     * @param message     The error message for what went wrong
     * @param possibleFix The possible fix that the programmers believe may be the issue
     */
    public InitializationFailureException(String message, String possibleFix) {
        super(message);
        this.possibleFix = possibleFix;
    }


    /**
     * Get the possible fix that the original programmers believe may be the issue
     *
     * @return The possible fix that the programmers believe may be the issue
     * @author jojo2357
     */
    public String getPossibleFix() {
        return this.possibleFix;
    }

    /**
     * Gets the {@link RuntimeException#toString()} default toString plus the possible fix
     *
     * @return the standard error messsage with the possible fix
     * @author jojo2357
     */
    @Override
    public String toString() {
        return super.toString() + " " + possibleFix;
    }
}
