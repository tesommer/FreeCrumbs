package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

/**
 * Samples a pixel in the current screen capture,
 * and saves it to a variable as an RGB value.
 * Syntax:
 * {@code pixel variable x y}
 * 
 * @author Tone Sommerland
 */
public final class Pixel extends Command {
    
    public static final GestureParser INSTANCE = new Pixel();
    
    public static final String NAME = "pixel";
    
    private Pixel() {
        super(NAME, 3, 3);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot)
                -> script.variables().set(
                        params[0],
                        robot.getPixelColor(
                                script.variables().value(params[1]),
                                script.variables().value(params[2]))
                                    .getRGB());
    }

}
