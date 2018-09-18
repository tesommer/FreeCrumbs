package freecrumbs.macro;

import freecrumbs.macro.internal.AtomicRecursionGuard;

/**
 * Prevents recursive macros from playing indefinitely.
 * 
 * @author Tone Sommerland
 */
public interface RecursionGuard {
    
    /**
     * Returns an instance that uses an atomic counter.
     * @param limit the number of recursions at which an exception is thrown
     * @throws IllegalArgumentException if the limit is less than zero
     */
    public static RecursionGuard getAtomic(final int limit) {
        return new AtomicRecursionGuard(limit);
    }
    
    /**
     * Increments the recursion count.
     * @throws MacroException
     * if the count has reached the maximum play limit
     */
    public abstract void increment() throws MacroException;
    
    /**
     * Decrements the count.
     */
    public abstract void decrement();

}
