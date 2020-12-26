package freecrumbs.macro.gesture;

import java.awt.MouseInfo;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * Saves the x-y coordinates of the current pointer location to variables.
 * Syntax:
 * {@code xy x-variable y-variable}
 * 
 * @author Tone Sommerland
 */
public final class Xy extends Command
{
    public static final GestureParser INSTANCE = new Xy();
    
    public static final String NAME = "xy";

    private Xy()
    {
        super(NAME, 2, 2);
    }

    @Override
    protected Gesture gesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot) -> storeXyInVariables(script, params);
    }

    private void storeXyInVariables(Script script, final String[] params)
    {
        final java.awt.Point xy = MouseInfo.getPointerInfo().getLocation();
        script.variables().set(params[0], xy.x);
        script.variables().set(params[1], xy.y);
    }

}
