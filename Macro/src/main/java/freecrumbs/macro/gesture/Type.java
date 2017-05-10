package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Macros;

/**
 * Types a value.
 * Syntax:
 * {@code type value}.
 * 
 * @author Tone Sommerland
 */
public class Type extends Command {
    
    private static final String NAME = "type";

    public Type() {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot)
                -> Macros.type(robot, script.getValue(params[0]));
    }

}
