package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * {@code mouse_move <x> <y>}.
 * 
 * @author Tone Sommerland
 */
public class MouseMove extends Command {
    
    private static final String NAME = "mouse_move";
    
    public MouseMove() {
        super(NAME, 2, 2);
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        return (script, robot) -> robot.mouseMove(
                script.getValue(params[0]), script.getValue(params[1]));
    }

}
