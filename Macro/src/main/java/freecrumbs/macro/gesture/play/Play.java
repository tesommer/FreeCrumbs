package freecrumbs.macro.gesture.play;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * A gesture that plays a named macro a certain number of times.
 * 
 * @author Tone Sommerland
 */
public class Play implements Gesture {
    private final String macroName;
    private final String times;

    public Play(final String macroName, final String times) {
        this.macroName = requireNonNull(macroName, "macroName");
        this.times = requireNonNull(times, "times");
    }

    @Override
    public void play(final Script script, final Robot robot)
            throws MacroException {
        

        script.play(robot, macroName, script.getValue(times));
    }

}
