package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * {@code key_released <key code>}.
 * 
 * @author Tone Sommerland
 */
public class KeyRelease extends Command {
    
    private static final String NAME = "key_release";

    public KeyRelease() {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        return (script, robot) -> robot.keyRelease(script.getValue(params[0]));
    }

}
