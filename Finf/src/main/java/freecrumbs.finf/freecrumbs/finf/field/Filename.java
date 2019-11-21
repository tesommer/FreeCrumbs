package freecrumbs.finf.field;

import freecrumbs.finf.Field;

/**
 * The filename.
 * The name of this field is {@code "filename"}.
 * 
 * @author Tone Sommerland
 */
public final class Filename
{
    private static final String NAME = "filename";
    
    public static final Field
    FIELD = Field.getInstance(NAME, file -> file.getName());

    private Filename()
    {
    }

}
