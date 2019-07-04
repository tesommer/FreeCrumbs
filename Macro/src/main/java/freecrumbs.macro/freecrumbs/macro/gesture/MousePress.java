package freecrumbs.macro.gesture;

import java.awt.event.InputEvent;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * Generates a mouse-press event.
 * Syntax:
 * {@code mouse_press button1 [button2 [button3]]}.
 * A button is an integer where nonzero is pressed and zero is not.
 * Buttons are numbered left to right.
 * 
 * @author Tone Sommerland
 */
public final class MousePress extends Command {
    
    public static final GestureParser INSTANCE = new MousePress();
    
    public static final String NAME = "mouse_press";
    
    private MousePress() {
        super(NAME, 1, 3);
    }

    static int getButtons(final Script script, final String[] params)
            throws MacroException {
        
        int buttons = 0;
        for (int i = 0; i < params.length; i++) {
            if (script.variables().value(params[i]) != 0) {
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
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> robot.mousePress(getButtons(script, params));
    }

}
