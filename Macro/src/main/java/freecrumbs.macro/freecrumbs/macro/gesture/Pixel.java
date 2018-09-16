package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * Samples a pixel in the current screen capture,
 * and saves it to a variable as an RGB value.
 * Syntax:
 * {@code pixel variable x y}
 * 
 * @author Tone Sommerland
 */
public class Pixel extends Command {
    
    public static final String NAME = "pixel";
    
    public Pixel() {
        super(NAME, 3, 3);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot)
                -> script.getVariables().set(
                        params[0],
                        robot.getPixelColor(
                                script.getVariables().valueOf(params[1]),
                                script.getVariables().valueOf(params[2]))
                                    .getRGB());
    }

}