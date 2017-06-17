package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Macros;

/**
 * Plays a named macro a certain number of times (default is one).
 * Syntax:
 * <ul>
 * <li>{@code play macro-name [times]}</li>
 * <li>{@code play macro-name [times left operator right]}:
 * Plays the macro if a logical expression is true,
 * e.g.: {@code play macro-name 1 x == y}.
 * Supported operators: {@code == != <= >= < >}</li>
 * </ul>
 * 
 * @author Tone Sommerland
 */
public class Play extends Command {
    
    public static final String NAME = "play";
    
    public Play() {
        super(NAME, 1, 5);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        if (params.length == 3 || params.length == 4) {
            throw new MacroException("Syntax error: " + line);
        }
        final String macroName = params[0];
        final String times = params.length >= 2 ? params[1] : "1";
        return (script, robot) -> {
            if (params.length != 5 || Macros.evaluateLogical(
                    script, params[2], params[3], params[4])) {
                script.play(robot, macroName, script.getValue(times));
            }
        };
    }

}
