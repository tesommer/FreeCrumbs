package freecrumbs.macro;

import java.io.InputStream;

import freecrumbs.macro.internal.ScriptFile;
import freecrumbs.macro.internal.StdinLocation;

/**
 * A script location.
 * 
 * @author Tone Sommerland
 */
public interface Location
{
    /**
     * Returns a location referencing a script file.
     * The returned location supports both
     * platform-dependent and platform-independent file paths.
     * In the latter case,
     * forward slash {@code '/'} is used as the file separator.
     * @param filePath path to the file
     */
    public static Location fromFilePath(final String filePath)
    {
        return new ScriptFile(filePath);
    }
    
    public static Location stdin()
    {
        return StdinLocation.INSTANCE;
    }
    
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
