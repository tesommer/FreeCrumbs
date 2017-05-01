package freecrumbs.macro;

import java.awt.Robot;

/**
 * A macro step,
 * typically an automated gesture such as a key press.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface Gesture {
    
    /**
     * Plays this gesture.
     * @param script the script containing this gesture
     * @param robot the event generator
     */
    void play(Script script, Robot robot) throws MacroException;

}
