package freecrumbs.macro.gesture;

import java.awt.Robot;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * Waits until idle or sets auto wait for idle.
 * Idle means that no events are being processed.
 * Syntax:
 * {@code idle [auto [onOffToggle=1]]}.
 * {@code onOffToggle}
 * is zero to turn off,
 * greater than zero to turn on
 * or less than zero to toggle.
 * 
 * @author Tone Sommerland
 */
public final class Idle extends Command {
    
    public static final GestureParser INSTANCE = new Idle();
    
    public static final String NAME = "idle";
    
    public static final String AUTO = "auto";
    
    private Idle() {
        super(NAME, 0, 2);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        if (params.length > 0) {
            if (!AUTO.equals(params[0])) {
                throw new MacroException(line);
            }
            return (script, robot) -> {
                setAutoWaitForIdle(script, robot, params);
            };
        }
        return (script, robot) -> robot.waitForIdle();
    }

    private static void setAutoWaitForIdle(
            final Script script,
            final Robot robot,
            final String[] params) throws MacroException {
        
        final int onOffToggle
            = script.variables().value(paramOrDefault(params, 1, "1"));
        robot.setAutoWaitForIdle(isOn(robot, onOffToggle));
    }

    private static boolean isOn(final Robot robot, final int onOffToggle) {
        if (onOffToggle == 0) {
            return false;
        } else if (onOffToggle > 0) {
            return true;
        }
        return !robot.isAutoWaitForIdle();
    }

}
