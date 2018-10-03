package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import freecrumbs.finf.FieldValue;

/**
 * An abstract base class for fields representing timestamp values.
 * 
 * @author Tone Sommerland
 */
public abstract class TimeFieldValue implements FieldValue {
    private final DateFormat dateFormat;

    /**
     * Constructor.
     * @param dateFormat the date format
     * @param locale the locale
     * @throws IOException if the date format is incorrect
     */
    protected TimeFieldValue(final String dateFormat, final Locale locale)
            throws IOException {
        
        try {
            this.dateFormat = new SimpleDateFormat(dateFormat, locale);
        } catch (final IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }
    
    protected abstract long getTime(File file) throws IOException;

    @Override
    public final String get(final File file) throws IOException {
        return dateFormat.format(new Date(getTime(file)));
    }

}
