package freecrumbs.macro;

/**
 * Creates gestures from lines in a script.
 * 
 * @author Tone Sommerland
 */
public interface GestureParser {
    
    /**
     * Whether or not this parser supports the given line.
     */
    boolean supports(String line);
    
    /**
     * Parses the given line and returns a gesture instance.
     * {@link #supports(String)} have returned {@code true}
     * for the given line before this method is called.
     */
    Gesture parse(String line) throws MacroException;

}
