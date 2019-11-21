package freecrumbs.macro.gesture;

import java.awt.Toolkit;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Creates an audible alert.
 * Syntax:
 * {@code beep}.
 * 
 * @author Tone Sommerland
 */
public final class Beep extends Command
{
    public static final GestureParser INSTANCE = new Beep();
    
    public static final String NAME = "beep";
    
    private Beep()
    {
        super(NAME, 0, 0);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot) -> Toolkit.getDefaultToolkit().beep();
    }

}
