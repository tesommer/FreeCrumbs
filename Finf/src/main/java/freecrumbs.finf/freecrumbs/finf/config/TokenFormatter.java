package freecrumbs.finf.config;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.stream.Stream;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormatter;

/**
 * An info formatter that replaces tokens on the form
 * <field-name> with the corresponding field value.
 * 
 * @author Tone Sommerland
 */
public final class TokenFormatter implements InfoFormatter
{
    private final String format;

    /**
     * Creates a new token-based info-formatter.
     * @param format the format string containing tokens to be replaced
     */
    public TokenFormatter(final String format)
    {
        this.format = requireNonNull(format, "format");
    }
    
    /**
     * Returns the field names used by this format.
     */
    public String[] usedFieldNames(final String[] availableFieldNames)
    {
        return Stream.of(availableFieldNames)
                .filter(name -> format.contains(token(name)))
                .toArray(String[]::new);
    }
    
    @Override
    public String stringify(final Info info) throws IOException
    {
        String result = format;
        for (final String fieldName : info.fieldNames())
        {
            result = replaceTokensWithValues(result, fieldName, info);
        }
        return result;
    }
    
    private static String replaceTokensWithValues(
            final String format,
            final String fieldName,
            final Info info) throws IOException
    {
        final String token = token(fieldName);
        if (format.contains(token))
        {
            return format.replace(token, info.value(fieldName));
        }
        return format;
    }

    private static String token(final String fieldName)
    {
        return "<" + fieldName + ">";
    }
}
