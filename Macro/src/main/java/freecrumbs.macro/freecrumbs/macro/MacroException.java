package freecrumbs.macro;

public class MacroException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public MacroException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MacroException(final String message) {
        super(message);
    }

    public MacroException(final Throwable cause) {
        super(cause);
    }

}
