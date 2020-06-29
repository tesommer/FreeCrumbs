package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Generates a key-release event.
 * Syntax:
 * {@code key_release key-code}.
 * 
 * @author Tone Sommerland
 */
public final class KeyRelease extends Command
{
    public static final GestureParser INSTANCE = new KeyRelease();
    
    public static final String NAME = "key_release";

    private KeyRelease()
    {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture gesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot)
                -> robot.keyRelease(script.variables().value(params[0]));
    }

}
