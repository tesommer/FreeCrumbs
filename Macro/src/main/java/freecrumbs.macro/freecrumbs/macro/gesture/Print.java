package freecrumbs.macro.gesture;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

/**
 * Prints output to STDOUT.
 * Script variables may be referenced in the output by preceding them with $.
 * Syntax:
 * {@code print output}.
 * 
 * @author Tone Sommerland
 */
public class Print implements GestureParser {
    
    public static final String NAME = "print";
    
    public Print() {
    }

    @Override
    public boolean supports(final String line) {
        return Util.isFirstWord(NAME, line);
    }

    @Override
    public Gesture parse(final String line) throws MacroException {
        return (script, robot) -> printLine(script, line);
    }

    private static void printLine(final Script script, final String line)
            throws MacroException {
        
        String output = line.trim().substring(NAME.length()).trim();
        for (final String variableName : script.variables().getNames()) {
            output = output.replace(
                    "$" + variableName,
                    String.valueOf(script.variables().get(variableName)));
        }
        System.out.println(output);
    }

}
