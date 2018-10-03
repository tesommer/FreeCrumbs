package freecrumbs.finf.field;

import freecrumbs.finf.Field;

/**
 * The file size in bytes.
 * The name of this field is {@code "size"}.
 * 
 * @author Tone Sommerland
 */
public final class Size {
    
    private static final String NAME = "size";
    
    public static final Field
    FIELD = Field.getInstance(NAME, file -> String.valueOf(file.length()));

    private Size() {
    }

}
