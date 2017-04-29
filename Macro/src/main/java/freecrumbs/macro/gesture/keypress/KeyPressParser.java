package freecrumbs.macro.gesture.keypress;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;

public class KeyPressParser implements GestureParser {
    
    private static final String PREFIX = "key_press ";

    public KeyPressParser() {
    }

    @Override
    public boolean supports(final String line) {
        return line.startsWith(PREFIX);
    }

    @Override
    public Gesture parse(final String line) throws MacroException {
        final String[] parts = line.split(" ");
        if (parts.length == 2) {
            return new KeyPress(parts[1]);
        }
        throw new MacroException("Syntax incorrect: " + line);
    }

}
