package freecrumbs.macro.gesture;

import java.util.Arrays;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;

/**
 * {@code print [args...]}.
 * 
 * @author Tone Sommerland
 */
public class Print extends Command {
    
    private static final String NAME = "print";
    
    public Print() {
        super(NAME, 0, Integer.MAX_VALUE);
    }

    @Override
    protected Gesture getGesture(final String[] params) throws MacroException {
        return (script, robot) -> {
            Arrays.stream(params).map(s -> s + ' ').forEach(System.out::print);
            System.out.println();
        };
    }

}
