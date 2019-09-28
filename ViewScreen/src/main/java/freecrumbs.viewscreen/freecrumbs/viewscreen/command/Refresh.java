package freecrumbs.viewscreen.command;

import java.io.IOException;

import freecrumbs.viewscreen.Command;
import freecrumbs.viewscreen.CommandContext;

public enum Refresh implements Command {
    INSTANCE;

    @Override
    public void execute(final CommandContext context) throws IOException {
        context.schedule(() -> context.getViewScreen().refresh());
    }

}
