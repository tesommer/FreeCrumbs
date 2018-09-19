package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Exits the script.
 * Syntax:
 * {@code exit}.
 * 
 * @author Tone Sommerland
 */
public final class Exit extends Command {
    
    public static final GestureParser INSTANCE = new Exit();
    
    public static final String NAME = "exit";
    
    private Exit() {
        super(NAME, 0, 0);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> System.exit(0);
    }

}
