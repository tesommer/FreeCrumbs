package freecrumbs.macro;

/**
 * Makes sure recursive macros doesn't keep playing indefinitely.
 * 
 * @author Tone Sommerland
 */
public interface RecursionGuard {
    
    /**
     * Increments the play count.
     * @throws MacroException
     * if the count has reached the maximum play limit.
     */
    void increment() throws MacroException;
    
    /**
     * Decrements the count.
     */
    void decrement();

}
