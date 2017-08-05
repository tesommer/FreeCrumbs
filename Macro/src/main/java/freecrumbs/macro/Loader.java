package freecrumbs.macro;

import java.io.InputStream;

import freecrumbs.macro.internal.DefaultLoader;

/**
 * Loads macros from an input stream.
 * 
 * @author Tone Sommerland
 */
public interface Loader {
    
    /**
     * Creates a default loader.
     */
    public static Loader getDefault(final GestureParser... parsers) {
        return new DefaultLoader(parsers);
    }
    
    /**
     * Loads macros from the specified input stream.
     */
    public abstract Macro[] load(InputStream in) throws MacroException;

}
