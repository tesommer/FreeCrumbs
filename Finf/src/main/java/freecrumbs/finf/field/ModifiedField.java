package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import freecrumbs.finf.InfoField;

public final class ModifiedField extends TimeField {
    
    private static final String NAME = "modified";

    private ModifiedField(
            final String name,
            final String dateFormat,
            final Locale locale) throws IOException {
        
        super(name, dateFormat, locale);
    }
    
    public static InfoField getInstance(
            final String dateFormat, final Locale locale) throws IOException {
        
        return new ModifiedField(NAME, dateFormat, locale);
    }

    @Override
    protected long getTime(final File file) {
        return file.lastModified();
    }

}
