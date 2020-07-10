package freecrumbs.macro;

import java.io.InputStream;

import freecrumbs.macro.internal.DefaultLoader;

/**
 * Loads macros from an input stream.
 * 
 * @author Tone Sommerland
 */
public interface Loader
{
    /**
     * Returns a default loader supporting the given parsers.
     */
    public static Loader supporting(final GestureParser... parsers)
    {
        return new DefaultLoader(parsers);
    }
    
    /**
     * Loads macros from the specified input stream.
     */
    public abstract Macro[] load(InputStream in) throws MacroException;
    
    /**
     * The recursion guard instance to use
     * by all scripts produced by this loader.
     */
    public abstract RecursionGuard recursionGuard();

}
