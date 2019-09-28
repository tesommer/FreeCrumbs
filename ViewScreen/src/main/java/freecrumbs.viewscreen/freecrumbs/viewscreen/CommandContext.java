package freecrumbs.viewscreen;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import com.calclipse.lib.dispatch.Dispatcher;

public final class CommandContext {
    
    @FunctionalInterface
    public static interface Task {
        public abstract void perform() throws IOException;
    }
    
    private final ViewScreen viewScreen;
    private final Dispatcher dispatcher;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    
    private final BiConsumer<? super IOException, ? super PrintStream>
    errorHandler;

    public CommandContext(
            final ViewScreen viewScreen,
            final Dispatcher dispatcher,
            final InputStream in,
            final PrintStream out,
            final PrintStream err,
            final BiConsumer<? super IOException, ? super PrintStream>
                  errorHandler) {

        this.viewScreen = requireNonNull(viewScreen, "viewScreen");
        this.dispatcher = requireNonNull(dispatcher, "dispatcher");
        this.in = requireNonNull(in, "in");
        this.out = requireNonNull(out, "out");
        this.err = requireNonNull(err, "err");
        this.errorHandler = requireNonNull(errorHandler, "errorHandler");
    }
    
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
    
    public void handle(final IOException ex) {
        errorHandler.accept(ex, err);
    }

    public ViewScreen getViewScreen() {
        return viewScreen;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
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
