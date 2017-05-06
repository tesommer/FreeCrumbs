package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Exits the script.
 * Syntax:
 * {@code exit}.
 * 
 * @author Tone Sommerland
 */
public class Exit extends Command {
    
    private static final String NAME = "exit";
    
    public Exit() {
        super(NAME, 0, 0);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> System.exit(0);
    }

}
