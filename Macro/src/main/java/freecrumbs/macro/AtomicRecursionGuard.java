package freecrumbs.macro;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A recursion guard using an atomic counter.
 * 
 * @author Tone Sommerland
 */
public class AtomicRecursionGuard implements RecursionGuard {
    private final AtomicInteger count = new AtomicInteger();
    private final int limit;
    
    /**
     * Creates a new atomic recursion counter.
     * @param limit the number of recursions at which an exception is thrown
     */
    public AtomicRecursionGuard(final int limit) {
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
