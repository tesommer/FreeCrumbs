package freecrumbs.macro.gesture.delay;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

public class Delay implements Gesture {
    private final String millis;

    public Delay(final String millis) {
        this.millis = requireNonNull(millis, "millis");
    }

    @Override
    public void play(final Script script, final Robot robot)
            throws MacroException {
        
        robot.delay(script.getValue(millis));
    }

}
