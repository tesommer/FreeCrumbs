package freecrumbs.macro;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A recursion guard using an atomic integer.
 * 
 * @author Tone Sommerland
 */
public class AtomicPlayCounter implements RecursionGuard {
    private final AtomicInteger count = new AtomicInteger();
    private final int limit;
    
    /**
     * Creates a new atomic play counter.
     * @param limit the number of recursions at which an exception is thrown
     */
    public AtomicPlayCounter(final int limit) {
        this.limit = limit;
    }

    @Override
    public void increment() throws MacroException {
        if (count.incrementAndGet() >= limit) {
            throw new MacroException("Infinite recursion");
        }
    }

    @Override
    public void decrement() {
        count.decrementAndGet();
    }
    
}
