package freecrumbs.viewscreen.main;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.calclipse.lib.dispatch.Dispatcher;

import freecrumbs.viewscreen.ExecutionContext;
import freecrumbs.viewscreen.Executor;
import freecrumbs.viewscreen.ViewScreen;

public final class Main {

    private Main() {
    }
    
    public static void main(final String[] args) throws IOException {
        final var context = new ExecutionContext(
                new ViewScreen(),
                Dispatcher.of(
                        SwingUtilities::isEventDispatchThread,
                        SwingUtilities::invokeLater),
                System.in,
                System.out,
                System.err,
                (ex, err) -> err.println(ex.toString()));
        for (
                String line = context.getReader().readLine();
                line != null;
                line = context.getReader().readLine()) {
            
            processInput(line, context);
        }
    }
    
    private static void processInput(
            final String input, final ExecutionContext context) {
        
        try {
            Executor.execute(input, context);
        } catch (final IOException ex) {
            context.handle(ex);
        }
    }

}
