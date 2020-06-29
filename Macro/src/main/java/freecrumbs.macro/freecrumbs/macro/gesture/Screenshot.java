package freecrumbs.macro.gesture;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * Takes a screenshot of the current screen and stores it as a script image.
 * Syntax:
 * {@code screenshot variable [x [y [width [height]]]]}.
 * 
 * @author Tone Sommerland
 */
public final class Screenshot extends Command
{
    public static final GestureParser INSTANCE = new Screenshot();
    
    public static final String NAME = "screenshot";
    
    private Screenshot()
    {
        super(NAME, 1, 5);
    }

    @Override
    protected Gesture gesture(final String line, final String[] params)
            throws MacroException
    {
        return (script, robot) -> takeScreenshot(script, robot, params);
    }
    
    private static void takeScreenshot(
            final Script script,
            final Robot robot,
            final String[] params) throws MacroException
    {
        script.images().set(
                params[0],
                robot.createScreenCapture(rectangle(script, params)));
    }
    
    private static Rectangle rectangle(
            final Script script, final String[] params) throws MacroException
    {
        final Dimension screenSize
            = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle(
                paramValue(script, params, 1, 0),
                paramValue(script, params, 2, 0),
                paramValue(script, params, 3, screenSize.width),
                paramValue(script, params, 4, screenSize.height));
    }

    private static int paramValue(
            final Script script,
            final String[] params,
            final int index,
            final int defaultValue) throws MacroException
    {
        return script.variables().value(
                paramOrDefault(params, index, String.valueOf(defaultValue)));
    }

}
