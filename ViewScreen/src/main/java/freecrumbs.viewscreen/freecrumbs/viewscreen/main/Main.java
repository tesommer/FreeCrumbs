package freecrumbs.viewscreen.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

import com.calclipse.lib.dispatch.Dispatcher;

import freecrumbs.viewscreen.CommandContext;
import freecrumbs.viewscreen.CommandParser;
import freecrumbs.viewscreen.ViewScreen;

public final class Main {

    private Main() {
    }
    
    public static void main(final String[] args) throws IOException {
        final var context = new CommandContext(
                new ViewScreen(),
                Dispatcher.of(
                        SwingUtilities::isEventDispatchThread,
                        SwingUtilities::invokeLater),
                System.in,
                System.out,
                System.err,
                (ex, err) -> err.println(ex.toString()));
        final var reader = new BufferedReader(new InputStreamReader(
                context.getIn()));
        for (
                String line = reader.readLine();
                line != null;
                line = reader.readLine()) {
            
            processInput(line, context);
        }
    }
    
    private static void processInput(
            final String input, final CommandContext context) {
        
        try {
            CommandParser.parse(input).execute(context);
        } catch (final IOException ex) {
            context.handle(ex);
        }
    }

}
