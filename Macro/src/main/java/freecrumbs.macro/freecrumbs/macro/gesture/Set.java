package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Util;

/**
 * Sets a script variable.
 * Syntax:
 * <ul>
 * <li>{@code set name value}</li>
 * <li>{@code set name value +|-|*|/|% value}</li>
 * </ul>
 * 
 * @author Tone Sommerland
 */
public class Set extends Command {
    
    public static final String NAME = "set";
    
    public Set() {
        super(NAME, 2, 4);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        if (params.length == 3) {
            throw new MacroException("Syntax error: " + line);
        }
        if (params.length == 2) {
            return (script, robot) -> script.variables().set(
                    params[0], script.variables().value(params[1]));
        }
        return (script, robot) -> script.variables().set(
                params[0],
                Util.evaluateArithmetic(
                        script, params[1], params[2], params[3]));
    }

}
