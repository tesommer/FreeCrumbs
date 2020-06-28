package freecrumbs.finf.field;

import java.io.File;

import freecrumbs.finf.Field;

/**
 * The path without the filename.
 * The path is empty if the file is in the root directory.
 * Otherwise it ends with a file separator.
 * The name of this field is {@code "path"}.
 * 
 * @author Tone Sommerland
 */
public final class Path
{
    private static final String NAME = "path";
    
    public static final Field FIELD = Field.simple(NAME, Path::value);

    private Path()
    {
    }
    
    private static String value(final File file)
    {
        final int index = file.getPath().lastIndexOf(File.separatorChar);
        if (index < 0)
        {
            return "";
        }
        return file.getPath().substring(0, index + 1);
    }

}
