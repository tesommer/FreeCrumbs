package freecrumbs.macro.gesture;

import java.awt.event.InputEvent;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * {@code mouse_press <button1> [<button2> [<button3>]]}.
 * A button is an integer, zero is false.
 * 
 * @author Tone Sommerland
 */
public class MousePress extends Command {
    
    private static final String NAME = "mouse_press";
    
    public MousePress() {
        super(NAME, 1, 3);
    }

    static int getButtons(final String[] params, Script script)
            throws MacroException {
        
        int buttons = 0;
        for (int i = 0; i < params.length; i++) {
            if (script.getValue(params[i]) != 0) {
                if (i == 0) {
                    buttons += InputEvent.BUTTON1_DOWN_MASK;
                } else if (i == 1) {
                    buttons += InputEvent.BUTTON2_DOWN_MASK;
                } else {
                    buttons += InputEvent.BUTTON3_DOWN_MASK;
                }
            }
        }
        return buttons;
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        return (script, robot) -> robot.mousePress(getButtons(params, script));
    }

}
