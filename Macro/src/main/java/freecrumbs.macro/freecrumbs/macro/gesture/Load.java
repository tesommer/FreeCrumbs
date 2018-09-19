package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Loads an image and stores it in the script.
 * Syntax:
 * {@code load variable location}.
 * 
 * @author Tone Sommerland
 */
public final class Load extends Command {
    
    public static final GestureParser INSTANCE = new Load();
    
    public static final String NAME = "load";
    
    private Load() {
        super(NAME, 2, 2);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> script.images()
                .set(params[0], script.images().load(params[1]));
    }

}
