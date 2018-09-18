package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;

/**
 * A macro contains automated gestures that can be played
 * to perform human-like interaction with the computer.
 * 
 * @author Tone Sommerland
 */
public final class Macro {
    private final String name;
    private final Gesture[] gestures;

    private Macro(final String name, final Gesture... gestures) {
        this.name = requireNonNull(name, "name");
        this.gestures = gestures.clone();
    }
    
    /**
     * Returns a macro.
     * @param name the name of this macro
     * @param gestures the gestures that this macro performs
     */
    public static Macro get(final String name, final Gesture... gestures) {
        return new Macro(name, gestures);
    }
    
    /**
     * Returns a nameless macro.
     * @param gestures the gestures that this macro performs
     */
    public static Macro getNameless(final Gesture... gestures) {
        return new Macro("", gestures);
    }

    /**
     * The name of this macro.
     * @return the empty string if this macro doesn't have a name
     */
    String getName() {
        return name;
    }

    /**
     * Plays this macro.
     * @param script the script containing this macro
     * @param robot the event generator
     */
    void play(final Script script, final Robot robot)
            throws MacroException {
        
        for (final Gesture gesture : gestures) {
            play(script, robot, gesture);
        }
    }
    
    private static void play(
            final Script script,
            final Robot robot,
            final Gesture gesture) throws MacroException {
        
        try {
            gesture.play(script, robot);
        } catch (final IllegalArgumentException|SecurityException ex) {
            throw new MacroException(ex);
        }
    }

}
