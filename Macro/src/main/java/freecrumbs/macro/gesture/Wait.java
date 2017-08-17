package freecrumbs.macro.gesture;

import java.awt.Robot;
import java.awt.image.BufferedImage;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Scanner;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

/**
 * Wait for an image within the current screen capture
 * to either appear or disappear.
 * Syntax:
 * {@code wait image [gone=0 [millis=100]]}
 * {@code gone} is non-zero to wait until the image is no longer there.
 * {@code millis} is the delay in milliseconds between checks.
 * 
 * @author Tone Sommerland
 */
public class Wait extends Command {
    
    public static final String NAME = "wait";

    private static final String DEFAULT_GONE = "0";
    private static final String DEFAULT_MILLIS = "100";
    
    public Wait() {
        super(NAME, 1, 3);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> waitForImage(script, robot, params);
    }

    private static void waitForImage(
            final Script script,
            final Robot robot,
            final String[] params) throws MacroException {
        
        final BufferedImage image = script.getImages().getOrLoad(params[0]);
        final boolean gone = getGone(script, params);
        final int millis = getMillis(script, params);
        waitLoop(robot, image, gone, millis);
    }
    
    private static boolean getGone(final Script script, final String[] params)
            throws MacroException {
        
        return script.getVariables().valueOf(
                paramOrDefault(params, 1, DEFAULT_GONE)) != 0;
    }
    
    private static int getMillis(final Script script, final String[] params)
            throws MacroException {
        
        return script.getVariables().valueOf(
                paramOrDefault(params, 2, DEFAULT_MILLIS));
    }

    private static void waitLoop(
            final Robot robot,
            final BufferedImage image,
            final boolean gone,
            final int millis) {
        
        while (true) {
            final int[] xy = new Scanner(Util.createScreenCapture(robot))
                .xyOf(image, 1);
            if (gone == (xy.length == 0)) {
                break;
            }
            robot.delay(millis);
        }
    }

}
