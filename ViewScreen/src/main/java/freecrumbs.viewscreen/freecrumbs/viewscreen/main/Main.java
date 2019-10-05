package freecrumbs.viewscreen.main;

import java.io.IOException;

import freecrumbs.viewscreen.ExecutionContext;
import freecrumbs.viewscreen.Executor;

public final class Main {

    private Main() {
    }
    
    public static void main(final String[] args) throws IOException {
        final var context = new ExecutionContext(
                System.in,
                System.out,
                System.err,
                (ex, err) -> err.println(ex.toString()));
        for (
                String line = context.readLine();
                line != null;
                line = context.readLine()) {
            try {
                Executor.execute(line, context);
            } catch (final IOException ex) {
                context.handle(ex);
            }
        }
    }

}
