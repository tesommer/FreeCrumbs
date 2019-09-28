package freecrumbs.viewscreen.command;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import freecrumbs.viewscreen.Command;
import freecrumbs.viewscreen.CommandContext;

public final class RemoveBuffer implements Command {
    private final String variable;

    private RemoveBuffer(final String variable) {
        this.variable = requireNonNull(variable, "variable");
    }
    
    public static Command getInstance(final String variable) {
        return new RemoveBuffer(variable);
    }

    @Override
    public void execute(final CommandContext context) throws IOException {
        context.schedule(() -> context.getViewScreen().removeBuffer(variable));
    }

}
