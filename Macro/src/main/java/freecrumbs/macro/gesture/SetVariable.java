package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Sets a script variable.
 * Syntax:
 * <ul>
 * <li>{@code set_variable <name> <value>}</li>
 * <li>{@code set_variable <name> <value> <+|-|*|/|%> <value>}</li>
 * </ul>
 * 
 * @author Tone Sommerland
 */
public class SetVariable extends Command {
    
    private static final String NAME = "set_variable";
    
    public SetVariable() {
        super(NAME, 2, 4);
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        if (params.length == 2) {
            return (script, robot) -> script.setVariable(
                    params[0], script.getValue(params[1]));
        }
        if ("+".equals(params[2])) {
            return (script, robot) -> script.setVariable(
                    params[0],
                    script.getValue(params[1]) + script.getValue(params[3]));
        } else if ("-".equals(params[2])) {
            return (script, robot) -> script.setVariable(
                    params[0],
                    script.getValue(params[1]) - script.getValue(params[3]));
        } else if ("*".equals(params[2])) {
            return (script, robot) -> script.setVariable(
                    params[0],
                    script.getValue(params[1]) * script.getValue(params[3]));
        } else if ("/".equals(params[2])) {
            return (script, robot) -> {
                try {
                    script.setVariable(
                            params[0],
                            script.getValue(params[1])
                                / script.getValue(params[3]));
                } catch (final ArithmeticException ex) {
                    throw new MacroException(ex);
                }
            };
        } else if ("%".equals(params[2])) {
            return (script, robot) -> {
                try {
                    script.setVariable(
                            params[0],
                            script.getValue(params[1])
                                % script.getValue(params[3]));
                } catch (final ArithmeticException ex) {
                    throw new MacroException(ex);
                }
            };
        }
        throw new MacroException("Invalid operator: " + params[2]);
    }

}
