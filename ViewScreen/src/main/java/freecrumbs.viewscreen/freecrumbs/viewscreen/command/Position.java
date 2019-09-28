package freecrumbs.viewscreen.command;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import freecrumbs.viewscreen.Command;
import freecrumbs.viewscreen.CommandContext;

public final class Position implements Command {
    private final String variable;
    private final int x;
    private final int y;

    private Position(final String variable, final int x, final int y) {
        this.variable = variable;
        this.x = x;
        this.y = y;
    }
    
    public static Command getInstance(
            final String variable, final int x, final int y) {
        
        return new Position(requireNonNull(variable, "variable"), x, y);
    }
    
    public static Command getInstance(final int x, final int y) {
        return new Position(null, x, y);
    }

    @Override
    public void execute(final CommandContext context) throws IOException {
        if (variable == null) {
            context.schedule(() -> context.getViewScreen().setPosition(x, y));
        } else {
            context.schedule(()
                    -> context.getViewScreen().setPosition(variable, x, y));
        }
    }

}
