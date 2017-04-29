package freecrumbs.macro;

import java.awt.Robot;

/**
 * Represents a macro step,
 * typically an automated gesture like as a mouse click.
 * 
 * @author Tone Sommerland
 */
public interface Gesture {
    
    /**
     * Performs this gesture.
     * @param script the script containing the macro of this gesture
     * @param robot the event generator
     */
    void play(Script script, Robot robot) throws MacroException;

}
