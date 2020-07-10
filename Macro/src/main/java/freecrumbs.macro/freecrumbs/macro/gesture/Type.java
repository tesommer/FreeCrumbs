package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Util;

/**
 * Types a value.
 * Syntax:
 * {@code type value}.
 * 
 * @author Tone Sommerland
 */
public final class Type extends Command
{
    public static final GestureParser INSTANCE = new Type();
    
    public static final String NAME = "type";

    private Type()
    {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture gesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot)
                -> Util.type(script.variables().value(params[0]), robot);
    }

}
