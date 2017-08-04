package freecrumbs.macro;

/**
 * A script location.
 * 
 * @author Tone Sommerland
 */
public interface ScriptLocation {
    
    /**
     * The base location.
     */
    public abstract String getBase();
    
    /**
     * Returns a location relative to this one.
     * @param relative an absolute or relative location
     */
    public abstract ScriptLocation refer(String relative) throws MacroException;
    
    /**
     * Loads the macro script at this location.
     * @param loader the macro loader to use
     */
    public abstract Script open(MacroLoader loader) throws MacroException;

}
