package freecrumbs.macro.gesture;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * {@code play <macro name> [times]}
 * 
 * @author Tone Sommerland
 */
public class Play extends Command {
    
    private static final String NAME = "play";
    
    public Play() {
        super(NAME, 1, 2);
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        final String macroName = params[0];
        final String times = params.length == 2 ? params[1] : "1";
        return (script, robot)
                -> script.play(robot, macroName, script.getValue(times));
    }

}
