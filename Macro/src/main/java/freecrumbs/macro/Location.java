package freecrumbs.macro;

import java.io.InputStream;

/**
 * A script location.
 * 
 * @author Tone Sommerland
 */
public interface Location {
    
    /**
     * The base location.
     */
    public abstract String getBase();
    
    /**
     * Returns an absolute location or one relative to this.
     * @param relative the location to refer to
     */
    public abstract Location refer(String relative) throws MacroException;
    
    /**
     * Opens this location.
     */
    public abstract InputStream open() throws MacroException;

}
