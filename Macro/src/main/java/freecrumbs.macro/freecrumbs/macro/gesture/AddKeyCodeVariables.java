package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Util;

/**
 * Creates key code variables VK_A, VK_ALT, etc.
 * Syntax:
 * {@code add_key_code_variables}.
 * 
 * @author Tone Sommerland
 */
public final class AddKeyCodeVariables extends Command
{
    public static final GestureParser INSTANCE = new AddKeyCodeVariables();
    
    public static final String NAME = "add_key_code_variables";

    private AddKeyCodeVariables()
    {
        super(NAME, 0, 0);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot) -> Util.addKeyCodeVariables(script);
    }

}
