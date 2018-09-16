package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * An abstract base class for fields representing timestamp values.
 * 
 * @author Tone Sommerland
 */
public abstract class TimeField extends AbstractInfoField {
    private final DateFormat dateFormat;

    /**
     * Constructor.
     * @param name the name of this field
     * @param dateFormat the date format
     * @param locale the locale
     * @throws IOException if the date format is incorrect
     */
    protected TimeField(
            final String name,
            final String dateFormat,
            final Locale locale) throws IOException {
        
        super(name);
        try {
            this.dateFormat = new SimpleDateFormat(dateFormat, locale);
        } catch (final IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }
    
    protected abstract long getTime(File file) throws IOException;

    @Override
    public final String getValue(final File file) throws IOException {
        return dateFormat.format(new Date(getTime(file)));
    }

}