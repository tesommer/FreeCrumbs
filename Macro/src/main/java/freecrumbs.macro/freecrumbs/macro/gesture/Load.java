package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Loads an image and stores it in the script.
 * Syntax:
 * {@code load variable location}.
 * 
 * @author Tone Sommerland
 */
public class Load extends Command {
    
    public static final String NAME = "load";
    
    public Load() {
        super(NAME, 2, 2);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> script.getImages()
                .set(params[0], script.getImages().load(params[1]));
    }

}
