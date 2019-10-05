package freecrumbs.viewscreen;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import javax.swing.SwingUtilities;

import com.calclipse.lib.dispatch.Dispatcher;

public final class ExecutionContext {
    
    @FunctionalInterface
    public static interface Task {
        public abstract void perform() throws IOException;
    }
    
    private final ViewScreen viewScreen = new ViewScreen();
    
    private final Dispatcher
    dispatcher = Dispatcher.of(
            SwingUtilities::isEventDispatchThread, SwingUtilities::invokeLater);
    
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    
    private final BiConsumer<? super IOException, ? super PrintStream>
    errorHandler;
    
    private final BufferedReader reader;

    public ExecutionContext(
            final InputStream in,
            final PrintStream out,
            final PrintStream err,
            final BiConsumer<? super IOException, ? super PrintStream>
                  errorHandler) {

        this.in = requireNonNull(in, "in");
        this.out = requireNonNull(out, "out");
        this.err = requireNonNull(err, "err");
        this.errorHandler = requireNonNull(errorHandler, "errorHandler");
        this.reader = new BufferedReader(new InputStreamReader(in));
    }
    
    /**
     * Schedules a task to be performed on the EDT ASAP.
     */
    public void schedule(final Task task) {
        requireNonNull(task, "task");
        dispatcher.schedule(() -> {
            try {
                task.perform();
            } catch (final IOException ex) {
                errorHandler.accept(ex, err);
            }
        });
    }
    
    /**
     * Schedules the error to be handled on the EDT.
     */
    public void handle(final IOException ex) {
        dispatcher.schedule(() -> errorHandler.accept(ex, err));
    }
    
    public String readLine() throws IOException {
        return reader.readLine();
    }

    public ViewScreen getViewScreen() {
        return viewScreen;
    }

    public InputStream getIn() {
        return in;
    }

    public PrintStream getOut() {
        return out;
    }

    public PrintStream getErr() {
        return err;
    }

}
