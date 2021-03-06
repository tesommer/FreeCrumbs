package freecrumbs.finf.field;

import java.io.File;

import freecrumbs.finf.Field;

/**
 * The path without the filename.
 * The path ends in a file separator,
 * unless the path is devoid of separators,
 * in which case it will be the empty string.
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
