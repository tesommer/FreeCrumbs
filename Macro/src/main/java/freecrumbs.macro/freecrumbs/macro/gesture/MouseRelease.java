package freecrumbs.macro.gesture;

import static freecrumbs.macro.gesture.MousePress.buttons;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Generates a mouse-release event.
 * Syntax:
 * {@code mouse_release button1 [button2 [button3]]}.
 * A button is an integer where nonzero is released and zero is not.
 * Buttons are numbered left to right.
 * 
 * @author Tone Sommerland
 */
public final class MouseRelease extends Command
{
    public static final GestureParser INSTANCE = new MouseRelease();
    
    public static final String NAME = "mouse_release";
    
    private MouseRelease()
    {
        super(NAME, 1, 3);
    }

    @Override
    protected Gesture gesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot) -> robot.mouseRelease(buttons(script, params));
    }

}
