package freecrumbs.viewscreen.command;

import static freecrumbs.viewscreen.Arguments.requireOnePluss;
import static java.util.Objects.requireNonNull;

import java.io.IOException;

import freecrumbs.viewscreen.Command;
import freecrumbs.viewscreen.CommandContext;

public final class MakeBuffer implements Command {
    private final String variable;
    private final int width;
    private final int height;

    private MakeBuffer(final String variable, final int width, final int height)
            throws IOException {
        
        this.variable = requireNonNull(variable, "variable");
        this.width = requireOnePluss(width, "width < 1: " + width);
        this.height = requireOnePluss(height, "height < 1: " + height);
    }
    
    public static Command getInstance(
            final String variable,
            final int width,
            final int height) throws IOException {
        
        return new MakeBuffer(variable, width, height);
    }

    @Override
    public void execute(final CommandContext context) throws IOException {
        context.schedule(()
                -> context.getViewScreen().makeBuffer(variable, width, height));
    }

}
