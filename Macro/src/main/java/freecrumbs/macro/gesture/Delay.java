package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Delays further execution a specified number of milliseconds
 * or sets the auto delay.
 * Syntax:
 * {@code delay millis [auto]}.
 * 
 * @author Tone Sommerland
 */
public class Delay extends Command {
    
    public static final String NAME = "delay";
    
    public static final String AUTO = "auto";

    public Delay() {
        super(NAME, 1, 2);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        if (params.length == 2) {
            if (!AUTO.equals(params[1])) {
                throw new MacroException(line);
            }
            return (script, robot) ->
                robot.setAutoDelay(script.getVariables().valueOf(params[0]));
        }
        return (script, robot) ->
            robot.delay(script.getVariables().valueOf(params[0]));
    }

}
