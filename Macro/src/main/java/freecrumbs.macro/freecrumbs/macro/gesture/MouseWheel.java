package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Moves the mouse wheel.
 * Syntax:
 * {@code mouse_wheel steps}.
 * Negative steps means scrolling up/away from user.
 * 
 * @author Tone Sommerland
 */
public final class MouseWheel extends Command
{
    public static final GestureParser INSTANCE = new MouseWheel();
    
    public static final String NAME = "mouse_wheel";
    
    private MouseWheel()
    {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot)
                -> robot.mouseWheel(script.variables().value(params[0]));
    }

}
