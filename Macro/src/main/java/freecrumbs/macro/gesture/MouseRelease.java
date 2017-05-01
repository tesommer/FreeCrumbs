package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * {@code mouse_press <button1> [<button2> [<button3>]]}.
 * A button is an integer, zero is false.
 * 
 * @author Tone Sommerland
 */
public class MouseRelease extends Command {
    
    private static final String NAME = "mouse_release";
    
    public MouseRelease() {
        super(NAME, 1, 3);
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        return (script, robot)
                -> robot.mouseRelease(MousePress.getButtons(params, script));
    }

}
