package freecrumbs.macro;

/**
 * Macro script loader.
 * 
 * @author Tone Sommerland
 */
public interface ScriptLoader {
    
    /**
     * Loads the script file at the specified location.
     */
    public abstract Script load(String location) throws MacroException;

}
