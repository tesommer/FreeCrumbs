package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Macros;

/**
 * Creates key code variables VK_A, VK_ALT, etc.
 * Syntax:
 * {@code add_key_code_variables}.
 * 
 * @author Tone Sommerland
 */
public class AddKeyCodeVariables extends Command {
    
    private static final String NAME = "add_key_code_variables";

    public AddKeyCodeVariables() {
        super(NAME, 0, 0);
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        return (script, robot) -> Macros.addKeyCodeVariables(script);
    }

}
