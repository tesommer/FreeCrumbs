package freecrumbs.macro.gesture;

import java.awt.Robot;
import java.util.stream.IntStream;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * Generates a key chord by holding down one or more keys simultaneously,
 * and then releasing them in reverse order.
 * Syntax:
 * {@code key_chord key-code1 key-code2 ...}.
 * 
 * @author Tone Sommerland
 */
public class KeyChord extends Command {
    
    public static final String NAME = "key_chord";

    public KeyChord() {
        super(NAME, 1, Integer.MAX_VALUE);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return (script, robot) -> playChord(script, robot, params);
    }
    
    private static void playChord(
            final Script script,
            final Robot robot,
            final String[] params) throws MacroException {
        
        final int[] keyCodes = getKeyCodes(script, params);
        IntStream.of(keyCodes).forEach(robot::keyPress);
        for (int i = keyCodes.length - 1; i >= 0; i--) {
            robot.keyRelease(keyCodes[i]);
        }
    }

    private static int[] getKeyCodes(
            final Script script, final String[] params) throws MacroException {
        
        final int[] keyCodes = new int[params.length];
        for (int i = 0; i < keyCodes.length; i++) {
            keyCodes[i] = script.variables().value(params[i]);
        }
        return keyCodes;
    }

}
