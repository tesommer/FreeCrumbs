package freecrumbs.finf.field;

import freecrumbs.finf.Field;

/**
 * Fields containing whitespace:
 * <ul>
 * <li>space</li>
 * <li>tab</li>
 * </ul>
 * 
 * @author Tone Sommerland
 */
public final class Whitespace
{
    private static final String SPACE_FIELD_NAME = "space";
    private static final String TAB_FIELD_NAME = "tab";

    private Whitespace()
    {
    }
    
    public static Field[] fields()
    {
        return new Field[]
        {
                Field.simple(SPACE_FIELD_NAME,   file -> " "),
                Field.simple(TAB_FIELD_NAME,   file -> "\t"),
        };
    }

}
