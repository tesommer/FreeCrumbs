package freecrumbs.macro;

/**
 * Prevents recursive macros from playing indefinitely.
 * 
 * @author Tone Sommerland
 */
public interface RecursionGuard {
    
    /**
     * Increments the recursion count.
     * @throws MacroException
     * if the count has reached the maximum play limit.
     */
    public abstract void increment() throws MacroException;
    
    /**
     * Decrements the count.
     */
    public abstract void decrement();

}
