package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Moves the mouse wheel.
 * Syntax:
 * {@code mouse_wheel steps}.
 * Negate steps means scrolling up/away from user.
 * 
 * @author Tone Sommerland
 */
public class MouseWheel extends Command {
    
    public static final String NAME = "mouse_wheel";
    
    public MouseWheel() {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot)
                -> robot.mouseWheel(script.getVariables().valueOf(params[0]));
    }

}
