package freecrumbs.viewscreen.command;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import freecrumbs.viewscreen.Command;
import freecrumbs.viewscreen.CommandContext;

public final class Visible implements Command {
    private final String variable;
    private final boolean visible;

    private Visible(final String variable, final boolean visible) {
        this.variable = variable;
        this.visible = visible;
    }
    
    public static Command getInstance(
            final String variable, final boolean visible) {
        
        return new Visible(requireNonNull(variable, "variable"), visible);
    }
    
    public static Command getInstance(final boolean visible) {
        return new Visible(null, visible);
    }

    @Override
    public void execute(final CommandContext context) throws IOException {
        if (variable == null) {
            context.schedule(() -> context.getViewScreen().setVisible(visible));
        } else {
            context.schedule(()
                    -> context.getViewScreen().setVisible(variable, visible));
        }
    }

}
