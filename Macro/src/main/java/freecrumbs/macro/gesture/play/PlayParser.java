package freecrumbs.macro.gesture.play;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

public class PlayParser implements GestureParser {
    
    private static final String PREFIX = "play ";

    @Override
    public boolean supports(final String line) {
        return line.startsWith(PREFIX);
    }

    @Override
    public Gesture parse(final String line) throws MacroException {
        final String[] parts = line.split(" ");
        if (parts.length == 2 || parts.length == 3) {
            final String macroName = parts[1];
            final String times = parts.length == 3 ? parts[2] : "1";
            return new Play(macroName, times);
        }
        throw new MacroException("Syntax incorrect: " + line);
    }

}
