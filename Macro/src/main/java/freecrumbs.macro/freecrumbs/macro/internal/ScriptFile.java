package freecrumbs.macro.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import freecrumbs.macro.Location;
import freecrumbs.macro.MacroException;

/**
 * A script location referencing a file in the file system.
 * This class supports both
 * platform-dependent and platform-independent file paths.
 * In the latter case,
 * forward slash {@code '/'} is used as the file separator.
 * 
 * @author Tone Sommerland
 */
public final class ScriptFile implements Location
{
    private static final char SEPARATOR = '/';
    
    private final String base;

    /**
     * Returns a location referencing a script file.
     * @param base path to the file
     */
    public ScriptFile(final String base)
    {
        this.base = neutral(base);
    }

    @Override
    public Location refer(final String target) throws MacroException
    {
        final int index = base.lastIndexOf(SEPARATOR);
        if (index >= 0)
        {
            final File relative = new File(
                    dependent(base.substring(0, index)),
                    dependent(target));
            if (relative.isFile())
            {
                return new ScriptFile(relative.getPath());
            }
        }
        return new ScriptFile(target);
    }

    @Override
    public InputStream open() throws MacroException
    {
        try
        {
            return new FileInputStream(dependent(base));
        }
        catch (final IOException ex)
        {
            throw new MacroException(ex);
        }
    }
    
    private static String dependent(final String filePath)
    {
        return filePath.replace(SEPARATOR, File.separatorChar);
    }
    
    private static String neutral(final String filePath)
    {
        return filePath.replace(File.separatorChar, SEPARATOR);
    }

}
