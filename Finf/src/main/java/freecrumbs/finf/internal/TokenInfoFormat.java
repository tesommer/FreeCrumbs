package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormat;

/**
 * An info format that replaces tokens on the form
 * ${field name} with the corresponding field value.
 * 
 * @author Tone Sommerland
 */
public class TokenInfoFormat implements InfoFormat {
    private final String format;

    /**
     * Creates a new token info format.
     * @param format the format string containing tokens to be replaced
     */
    public TokenInfoFormat(final String format) {
        this.format = requireNonNull(format, "format");
    }
    
    @Override
    public String toString(final Info info) throws IOException {
        String result = format;
        for (final String fieldName : info.getFieldNames()) {
            result = replace(result, fieldName, info);
        }
        return result;
    }
    
    private static String replace(
            final String format,
            final String fieldName,
            final Info info) throws IOException {
        
        final String token = getToken(fieldName);
        if (format.contains(token)) {
            return format.replace(token, info.getValue(fieldName));
        }
        return format;
    }

    private static String getToken(final String fieldName) {
        return "${" + fieldName + "}";
    }
}
