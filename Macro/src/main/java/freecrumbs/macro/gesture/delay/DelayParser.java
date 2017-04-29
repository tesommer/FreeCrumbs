package freecrumbs.macro.gesture.delay;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

public class DelayParser implements GestureParser {
    
    private static final String PREFIX = "delay ";

    public DelayParser() {
    }

    @Override
    public boolean supports(final String line) {
        return line.startsWith(PREFIX);
    }

    @Override
    public Gesture parse(final String line) throws MacroException {
        final String[] parts = line.split(" ");
        if (parts.length == 2) {
            return new Delay(parts[1]);
        }
        throw new MacroException("Syntax incorrect: " + line);
    }

}
