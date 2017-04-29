package freecrumbs.macro;

import java.io.Reader;

/**
 * Macro script loader.
 * 
 * @author Tone Sommerland
 */
public interface ScriptLoader {
    
    Script load(Reader reader) throws MacroException;

}
