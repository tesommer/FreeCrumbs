package freecrumbs.macro;

import java.awt.Robot;

/**
 * A macro step,
 * typically an automated gesture such as a key press.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface Gesture
{
    /**
     * Plays this gesture.
     * Any {@code IllegalArgumentException} or {@code SecurityException}
     * thrown by this method
     * will be re-thrown as {@code MacroException}.
     * @param script the script containing this gesture
     * @param robot the event generator
     */
    public abstract void play(Script script, Robot robot) throws MacroException;

}
