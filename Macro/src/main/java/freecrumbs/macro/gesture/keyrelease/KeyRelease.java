package freecrumbs.macro.gesture.keyrelease;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

public class KeyRelease implements Gesture {
    private final String keyCode;

    public KeyRelease(final String keyCode) {
        this.keyCode = requireNonNull(keyCode, "keyCode");
    }

    @Override
    public void play(final Script script, final Robot robot)
            throws MacroException {
        
        robot.keyRelease(script.getValue(keyCode));
    }

}
