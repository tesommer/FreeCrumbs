package freecrumbs.macro.gesture.keypress;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

public class KeyPress implements Gesture {
    private final String keyCode;

    public KeyPress(final String keyCode) {
        this.keyCode = requireNonNull(keyCode, "keyCode");
    }

    @Override
    public void play(final Script script, final Robot robot)
            throws MacroException {
        
        robot.keyPress(script.getValue(keyCode));
    }

}
