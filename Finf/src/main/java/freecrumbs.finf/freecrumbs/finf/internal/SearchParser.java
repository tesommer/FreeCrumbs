package freecrumbs.finf.internal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import freecrumbs.finf.field.DynamicValue;
import freecrumbs.finf.field.Search;

/**
 * Parses search config-settings.
 * Format: {@code /regex/param1,param2,...}.
 * Parameters: occurrence, groups and charset.
 * A default is used for empty parameters.
 * 
 * @author Tone Sommerland
 */
public final class SearchParser {

    private SearchParser() {
    }
    
    /**
     * Returns an available-fields instance
     * containing the given available fields
     * with additional search fields
     * resulting from parsing a search setting.
     * @param availableFields current available fields
     * @param fieldNamePrefix field-name prefix
     * @param setting the search setting
     * @return the available fields with the additional search
     */
    public static AvailableFields withAnotherSearch(
            final AvailableFields availableFields,
            final String fieldNamePrefix,
            final String setting) throws IOException {
        
        final var searchParams = new Search.Params(
                getRegex(availableFields, setting))
                    .withFieldNamePrefix(fieldNamePrefix);
        return availableFields.coCaching(availableFields.getParams()
                .withAnotherSearch(
                        remainingSearchParams(searchParams, setting)));
    }
    
    private static DynamicValue getRegex(
            final AvailableFields availableFields,
            final String setting) throws IOException {
        
        final String regexString = getRegexString(setting);
        final var regexFormat = new TokenInfoFormat(regexString);
        final String[] usedByRegex = regexFormat.getUsedFieldNames(
                availableFields.getNames());
        if (usedByRegex.length == 0) {
            return DynamicValue.of(regexString);
        }
        return DynamicValue.of(
                availableFields.getReader(usedByRegex), regexFormat);
    }
    
    private static String getRegexString(final String setting)
            throws IOException {
        
        if (!setting.startsWith("/")) {
            throw new IOException(setting);
        }
        final int endOfRegex = setting.lastIndexOf('/');
        if (endOfRegex < 1) {
            throw new IOException(setting);
        }
        return setting.substring(1, endOfRegex);
    }
    
    private static Search.Params remainingSearchParams(
            final Search.Params searchParams,
            final String setting) throws IOException {
        
        final int endOfRegex = setting.lastIndexOf('/');
        final String params[] = setting.substring(endOfRegex + 1).split(",");
        return searchParams
                .withOccurrence(getOccurrence(params, setting))
                .withGroups(getGroups(params, setting))
                .withCharset(getCharset(params, setting));
    }
    
    private static int getOccurrence(
            final String[] params, final String message) throws IOException {
        
        return params.length < 1 || params[0].isEmpty()
                ? 1
                : parseInt(params[0], "Occurrence: " + message);
    }
    
    private static int getGroups(
            final String[] params, final String message) throws IOException {
        
        return params.length < 2 || params[1].isEmpty()
                ? 0
                : requireZeroPlus(params[1], "Groups: " + message);
    }
    
    private static Charset getCharset(
            final String[] params, final String message) throws IOException {
        
        return params.length < 3 || params[2].isEmpty()
                ? Charset.defaultCharset()
                : parseCharset(params[2], "Charset: " + message);
    }
    
    private static int parseInt(final String param, final String message)
            throws IOException {
        
        try {
            return Integer.parseInt(param);
        } catch (final NumberFormatException ex) {
            throw new IOException(message, ex);
        }
    }
    
    private static int requireZeroPlus(final String param, final String message)
            throws IOException {
        
        final int i = parseInt(param, message);
        if (i < 0) {
            throw new IOException(message);
        }
        return i;
    }
    
    private static Charset parseCharset(
            final String param, final String message) throws IOException {
        
        try {
            return Charset.forName(param);
        } catch (final IllegalCharsetNameException|
                       UnsupportedCharsetException ex) {
            throw new IOException(message, ex);
        }
    }

}
