package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import freecrumbs.finf.InfoField;

/**
 * Last modified.
 * The name of this field is {@code "modified"}.
 * 
 * @author Tone Sommerland
 */
public final class ModifiedField extends TimeField {
    
    private static final String NAME = "modified";

    private ModifiedField(
            final String name,
            final String dateFormat,
            final Locale locale) throws IOException {
        
        super(name, dateFormat, locale);
    }
    
    /**
     * Returns an instance of this class.
     * @param dateFormat the date format
     * @param locale the locale
     * @throws IOException if the date format is incorrect
     */
    public static InfoField getInstance(
            final String dateFormat, final Locale locale) throws IOException {
        
        return new ModifiedField(NAME, dateFormat, locale);
    }

    @Override
    protected long getTime(final File file) throws IOException {
        return file.lastModified();
    }

}
