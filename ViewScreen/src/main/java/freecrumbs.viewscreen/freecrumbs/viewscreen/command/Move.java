package freecrumbs.viewscreen.command;

import java.io.IOException;

import freecrumbs.viewscreen.Command;
import freecrumbs.viewscreen.CommandContext;

public final class Move implements Command {
    private final int x;
    private final int y;

    private Move(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public static Command getInstance(final int x, final int y) {
        return new Move(x, y);
    }

    @Override
    public void execute(final CommandContext context) throws IOException {
        context.schedule(() -> context.getViewScreen().move(x, y));
    }

}
