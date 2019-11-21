package freecrumbs.macro.internal;

import java.util.concurrent.atomic.AtomicInteger;

import freecrumbs.macro.MacroException;
import freecrumbs.macro.RecursionGuard;

/**
 * A recursion guard using an atomic counter.
 * 
 * @author Tone Sommerland
 */
public final class AtomicRecursionGuard implements RecursionGuard
{
    private final AtomicInteger count = new AtomicInteger();
    private final int limit;
    
    /**
     * Creates a new atomic recursion counter.
     * @param limit the number of recursions at which an exception is thrown
     * @throws IllegalArgumentException if the limit is less than zero
     */
    public AtomicRecursionGuard(final int limit)
    {
        if (limit < 0)
        {
            throw new IllegalArgumentException("limit < 0: " + limit);
        }
        this.limit = limit;
    }

    @Override
    public void increment() throws MacroException
    {
        if (count.incrementAndGet() >= limit)
        {
            throw new MacroException(
                    "Recursion limit reached. limit=" +  limit);
        }
    }

    @Override
    public void decrement()
    {
        count.decrementAndGet();
    }
    
}
