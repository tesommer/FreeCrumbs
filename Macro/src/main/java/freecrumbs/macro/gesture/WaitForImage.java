package freecrumbs.macro.gesture;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

/**
 * Wait for an image within the current screen capture
 * to either appear or disappear.
 * Syntax:
 * {@code wait_for_image image [gone [millis]]}
 * {@code gone} is non-zero to wait until the image is no longer there.
 * {@code millis} is the delay in milliseconds between checks.
 * 
 * @author Tone Sommerland
 */
public class WaitForImage extends Command {
    
    public static final String NAME = "wait_for_image";

    private static final String DEFAULT_GONE = "0";
    private static final String DEFAULT_MILLIS = "100";
    
    public WaitForImage() {
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
        
        final Dimension screenSize
            = Toolkit.getDefaultToolkit().getScreenSize();
        final BufferedImage capture
            = robot.createScreenCapture(new Rectangle(screenSize));
        final BufferedImage image = script.images().getOrLoad(params[0]);
        final boolean gone = getGone(script, params);
        final int millis = getMillis(script, params);
        waitLoop(robot, capture, image, gone, millis);
    }
    
    private static boolean getGone(final Script script, final String[] params)
            throws MacroException {
        
        return script.variables().getValue(
                paramOrDefault(params, 1, DEFAULT_GONE)) != 0;
    }
    
    private static int getMillis(final Script script, final String[] params)
            throws MacroException {
        
        return script.variables().getValue(
                paramOrDefault(params, 2, DEFAULT_MILLIS));
    }

    private static void waitLoop(
            final Robot robot,
            final BufferedImage capture,
            final BufferedImage image,
            final boolean gone,
            final int millis) {
        
        while (true) {
            final int[] xy = Util.xyOf(image, capture, 1);
            if (gone) {
                if (xy.length == 0) {
                    break;
                }
            } else if (xy.length > 0) {
                break;
            }
            robot.delay(millis);
        }
    }

}
