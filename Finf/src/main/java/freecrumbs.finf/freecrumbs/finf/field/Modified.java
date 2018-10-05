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
public final class Modified {
    
    private static final String NAME = "modified";

    private Modified() {
    }
    
    public static Field getField(final String dateFormat, final Locale locale)
            throws IOException {
        
        return Field.getInstance(
                NAME, new ModifiedFieldValue(dateFormat, locale));
    }
    
    private static final class ModifiedFieldValue extends TimeFieldValue {

        private ModifiedFieldValue(
                final String dateFormat,
                final Locale locale) throws IOException {
            
            super(dateFormat, locale);
        }

        @Override
        protected long getTime(final File file) throws IOException {
            return file.lastModified();
        }
        
    }

}