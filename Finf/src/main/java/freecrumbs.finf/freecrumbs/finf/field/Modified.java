package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import freecrumbs.finf.Field;

/**
 * Last modified.
 * The name of this field is {@code "modified"}.
 * 
 * @author Tone Sommerland
 */
public final class Modified
{
    private static final String NAME = "modified";

    private Modified()
    {
    }
    
    /**
     * Returns a field with a value as milliseconds since the epoch.
     */
    public static Field field()
    {
        return Field.simple(NAME, new ModifiedValue());
    }
    
    /**
     * Returns a field with a value formatted using a date format.
     * @param dateFormat the date format
     * @param locale the locale
     * @throws IOException if the date format is incorrect
     */
    public static Field field(final String dateFormat, final Locale locale)
            throws IOException
    {
        return Field.simple(
                NAME, new ModifiedValue(dateFormat, locale));
    }
    
    private static final class ModifiedValue extends TimeValue
    {
        private ModifiedValue()
        {
        }

        private ModifiedValue(
                final String dateFormat,
                final Locale locale) throws IOException
        {
            super(dateFormat, locale);
        }

        @Override
        protected long time(final File file) throws IOException
        {
            return file.lastModified();
        }
        
    }

}
