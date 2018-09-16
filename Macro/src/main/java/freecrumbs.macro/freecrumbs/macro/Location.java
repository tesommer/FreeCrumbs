package freecrumbs.macro;

import java.io.InputStream;

/**
 * A script location.
 * 
 * @author Tone Sommerland
 */
public interface Location {
    
    /**
     * The string specifying this location.
     */
    public abstract String getBase();
    
    /**
     * Returns a location that points to the given target.
     * @param target the absolute or relative location to refer to
     */
    public abstract Location refer(String target) throws MacroException;
    
    /**
     * Opens this location.
     */
    public abstract InputStream open() throws MacroException;

}
