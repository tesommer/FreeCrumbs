package freecrumbs.macro;

/**
 * Creates gestures from script lines.
 * 
 * @author Tone Sommerland
 */
public interface GestureParser {
    
    /**
     * Whether or not this parser supports the given line.
     */
    public abstract boolean supports(String line);
    
    /**
     * Parses the given line and returns a gesture instance.
     * {@link #supports(String)} has returned {@code true}
     * for the given line before this method is called.
     */
    public abstract Gesture parse(String line) throws MacroException;

}
