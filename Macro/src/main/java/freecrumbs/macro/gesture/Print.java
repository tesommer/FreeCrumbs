package freecrumbs.macro.gesture;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Macros;

/**
 * {@code print [args...]}.
 * 
 * @author Tone Sommerland
 */
public class Print implements GestureParser {
    
    private static final String NAME = "print";
    
    public Print() {
    }

    @Override
    public boolean supports(final String line) {
        return Macros.isFirstPiece(NAME, line);
    }

    @Override
    public Gesture parse(final String line) throws MacroException {
        return (script, robot) -> {
            String output = line.trim().substring(NAME.length()).trim();
            for (final String variableName : script.getVariableNames()) {
                output = output.replace(
                        "$" + variableName,
                        String.valueOf(script.getVariable(variableName)));
            }
            System.out.println(output);
        };
    }

}
