package freecrumbs.macro;

import java.io.InputStream;

import freecrumbs.macro.internal.DefaultMacroLoader;

/**
 * Reads macros from an input stream.
 * 
 * @author Tone Sommerland
 */
public interface MacroLoader {
    
    /**
     * Creates a default macro loader.
     */
    public static MacroLoader getDefault(final GestureParser... parsers) {
        return new DefaultMacroLoader(parsers);
    }
    
    /**
     * Loads macros from the specified input stream.
     */
    public abstract Macro[] load(InputStream in) throws MacroException;

}
