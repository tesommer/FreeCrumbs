package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Delays further execution a specified number of milliseconds.
 * Syntax:
 * {@code delay millis}.
 * 
 * @author Tone Sommerland
 */
public class Delay extends Command {
    
    private static final String NAME = "delay";

    public Delay() {
        super(NAME, 1, 1);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> robot.delay(script.getValue(params[0]));
    }

}
