package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormat;

/**
 * An info format that replaces tokens on the form
 * ${field name} with the corresponding field value.
 * 
 * @author Tone Sommerland
 */
public class TokenInfoFormat implements InfoFormat {

    private static final String PATH_TOKEN = "${path}";
    private static final String FILENAME_TOKEN = "${filename}";
    private static final String SIZE_TOKEN = "${size}";
    private static final String MODIFIED_TOKEN = "${modified}";
    private static final String HASH_TOKEN = "${hash}";
    
    private final String infoFormat;
    private final DateFormat dateFormat;

    /**
     * Creates a new token info format.
     * @param infoFormat the info format containing tokens to be replaced
     * @param dateFormat a date format for the modified field
     * @param locale the locale for the date format
     */
    public TokenInfoFormat(
        final String infoFormat,
        final String dateFormat,
        final Locale locale) throws IOException {
        
        this.infoFormat = requireNonNull(infoFormat, "infoFormat");
        try {
            this.dateFormat = new SimpleDateFormat(dateFormat, locale);
        } catch (final IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }
    
    @Override
    public String toString(final Info info) {
        final String modified
            = dateFormat.format(new Date(info.getModified()));
        return infoFormat
            .replace(PATH_TOKEN, info.getPath())
            .replace(FILENAME_TOKEN, info.getFilename())
            .replace(SIZE_TOKEN, String.valueOf(info.getSize()))
            .replace(MODIFIED_TOKEN, modified)
            .replace(HASH_TOKEN, info.getHash());
    }

    @Override
    public boolean requiresHash() {
        return infoFormat.contains(HASH_TOKEN);
    }
}
