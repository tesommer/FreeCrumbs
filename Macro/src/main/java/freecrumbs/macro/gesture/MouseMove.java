package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Moves the mouse to specified x-y coordinates.
 * Syntax:
 * {@code mouse_move x y}.
 * 
 * @author Tone Sommerland
 */
public class MouseMove extends Command {
    
    public static final String NAME = "mouse_move";
    
    public MouseMove() {
        super(NAME, 2, 2);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> robot.mouseMove(
                script.getValue(params[0]), script.getValue(params[1]));
    }

}
