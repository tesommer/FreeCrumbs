package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;

/**
 * A macro contains automated gestures that can be played
 * to perform human-like interaction with the computer.
 * 
 * @author Tone Sommerland
 */
public class Macro {
    private final RecursionGuard recursionGuard;
    private final String name;
    private final Gesture[] gestures;

    /**
     * Creates a macro.
     * @param recursionGuard guards against infinite recursion
     * @param name the name of this macro
     * @param gestures the gestures that this macro performs
     */
    public Macro(
            final RecursionGuard recursionGuard,
            final String name,
            final Gesture... gestures) {
        
        this.recursionGuard = requireNonNull(recursionGuard, "recursionGuard");
        this.name = requireNonNull(name, "name");
        this.gestures = gestures.clone();
    }
    
    /**
     * Creates a nameless macro.
     * @param recursionGuard guards against infinite recursion
     * @param gestures the gestures that this macro performs
     */
    public Macro(
            final RecursionGuard recursionGuard, final Gesture... gestures) {
        
        this(recursionGuard, "", gestures);
    }

    /**
     * The name of this macro.
     * @return the empty string if this macro doesn't have a name.
     */
    public String getName() {
        return name;
    }

    /**
     * Plays this macro.
     * @param script the script containing this macro
     * @param robot the event generator
     */
    public void play(final Script script, final Robot robot)
            throws MacroException {
        
        recursionGuard.increment();
        for (final Gesture gesture : gestures) {
            gesture.play(script, robot);
        }
        recursionGuard.decrement();
    }

}
