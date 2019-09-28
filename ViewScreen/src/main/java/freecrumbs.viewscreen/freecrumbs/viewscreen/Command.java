package freecrumbs.viewscreen;

import java.io.IOException;

@FunctionalInterface
public interface Command {
    
    public abstract void execute(CommandContext context) throws IOException;

}
