package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Moves the mouse to specified x-y coordinates.
 * Syntax:
 * {@code mouse_move x y}.
 * 
 * @author Tone Sommerland
 */
public final class MouseMove extends Command
{
    public static final GestureParser INSTANCE = new MouseMove();
    
    public static final String NAME = "mouse_move";
    
    private MouseMove()
    {
        super(NAME, 2, 2);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot) -> robot.mouseMove(
                script.variables().value(params[0]),
                script.variables().value(params[1]));
    }

}
