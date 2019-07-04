package freecrumbs.macro.gesture;

import java.awt.Robot;
import java.awt.image.BufferedImage;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Scanner;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

/**
 * Wait for an image within the current screen capture
 * to either appear or disappear.
 * Syntax:
 * {@code wait from-x from-y to-x to-y image [gone=0 [millis=100]]}
 * {@code gone} is non-zero to wait until the image is no longer there.
 * {@code millis} is the delay in milliseconds between checks.
 * The from/to parameters work the same way as in
 * {@link freecrumbs.macro.gesture.Scan}.
 * 
 * @author Tone Sommerland
 */
public final class Wait extends Command {
    
    public static final GestureParser INSTANCE = new Wait();
    
    public static final String NAME = "wait";

    private static final String DEFAULT_GONE = "0";
    private static final String DEFAULT_MILLIS = "100";
    
    private Wait() {
        super(NAME, 5, 7);
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
        
        final BufferedImage image = getImage(script, params);
        final boolean gone = getGone(script, params);
        final int millis = getMillis(script, params);
        while (true) {
            final int[] xy = getScanner(script, robot, params)
                .xyOf(image, 1);
            if (gone == (xy.length == 0)) {
                break;
            }
            robot.delay(millis);
        }
    }

    private static BufferedImage getImage(
            final Script script, final String[] params) throws MacroException {
        
        return script.images().getOrLoad(params[4]);
    }
    
    private static boolean getGone(final Script script, final String[] params)
            throws MacroException {
        
        return script.variables().value(
                paramOrDefault(params, 5, DEFAULT_GONE)) != 0;
    }
    
    private static int getMillis(final Script script, final String[] params)
            throws MacroException {
        
        return script.variables().value(
                paramOrDefault(params, 6, DEFAULT_MILLIS));
    }
    
    private static Scanner getScanner(
            final Script script,
            final Robot robot,
            final String[] params) throws MacroException {
        
        final int fromX = script.variables().value(params[0]);
        final int fromY = script.variables().value(params[1]);
        final int toX = script.variables().value(params[2]);
        final int toY = script.variables().value(params[3]);
        return new Scanner(
                Util.createScreenCapture(robot), fromX, fromY, toX, toY);
    }

}
