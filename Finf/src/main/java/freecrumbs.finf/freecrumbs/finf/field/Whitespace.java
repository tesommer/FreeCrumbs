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
public final class Whitespace {
    private static final String SPACE_FIELD_NAME = "space";
    private static final String TAB_FIELD_NAME = "tab";

    private Whitespace() {
    }
    
    public static Field[] getFields() {
        return new Field[] {
                Field.getInstance(SPACE_FIELD_NAME,   file -> " "),
                Field.getInstance(TAB_FIELD_NAME,   file -> "\t"),
        };
    }

}
