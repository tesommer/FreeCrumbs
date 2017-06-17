package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Generates a key press event.
 * Syntax:
 * {@code key_press key-code}.
 * 
 * @author Tone Sommerland
 */
public class KeyPress extends Command {
    
    public static final String NAME = "key_press";

    public KeyPress() {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> robot.keyPress(script.getValue(params[0]));
    }

}
