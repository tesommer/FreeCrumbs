package freecrumbs.macro.gesture;

import java.awt.Toolkit;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Creates an audible alert.
 * Syntax:
 * {@code beep}.
 * 
 * @author Tone Sommerland
 */
public class Beep extends Command {
    
    public static final String NAME = "beep";
    
    public Beep() {
        super(NAME, 0, 0);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> Toolkit.getDefaultToolkit().beep();
    }

}
