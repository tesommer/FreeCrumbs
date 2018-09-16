package freecrumbs.macro.gesture;

import static freecrumbs.macro.gesture.MousePress.getButtons;
import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Generates a mouse release event.
 * Syntax:
 * {@code mouse_release button1 [button2 [button3]]}.
 * A button is an integer where nonzero is released and zero is not.
 * Buttons are numbered left to right.
 * 
 * @author Tone Sommerland
 */
public class MouseRelease extends Command {
    
    public static final String NAME = "mouse_release";
    
    public MouseRelease() {
        super(NAME, 1, 3);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot)
                -> robot.mouseRelease(getButtons(script, params));
    }

}
