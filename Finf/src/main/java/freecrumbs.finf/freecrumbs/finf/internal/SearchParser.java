package freecrumbs.finf.internal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import freecrumbs.finf.field.DynamicValue;
import freecrumbs.finf.field.Search;

/**
 * Parses search config-settings.
 * Format: {@code /regex/key=value,key=value,...}.
 * Key-value-parameters: o=occurrence, g=groups and c=charset.
 * A default is used for missing parameters.
 * 
 * @author Tone Sommerland
 */
public final class SearchParser {
    
    private static final String OCCURRENCE_KEY = "o";
    private static final String GROUP_KEY = "g";
    private static final String CHARSET_KEY = "c";

    private SearchParser() {
    }
    
    /**
     * Returns an available-fields instance
     * containing the given available fields
     * with additional search fields
     * resulting from parsing a search setting.
     * @param availableFields current available fields
     * @param initialSearchParams initial search parameters
     * @param setting the search setting
     * @return the available fields with the additional search
     */
    public static AvailableFields withAnotherSearch(
            final AvailableFields availableFields,
            final Search.Params initialSearchParams,
            final String setting) throws IOException {
        
        final var parameterizedSetting = new ParameterizedSetting(setting);
        final Search.Params searchParams = initialSearchParams.withRegex(
                getRegex(availableFields, parameterizedSetting));
        return availableFields.coCaching(availableFields.getParams()
                .withAnotherSearch(remainingSearchParams(
                        searchParams, parameterizedSetting)));
    }
    
    private static DynamicValue getRegex(
            final AvailableFields availableFields,
            final ParameterizedSetting setting) throws IOException {
        
        final String regexString = setting.mainPart();
        final var regexFormat = new TokenInfoFormat(regexString);
        final String[] usedByRegex = regexFormat.getUsedFieldNames(
                availableFields.getNames());
        if (usedByRegex.length == 0) {
            return DynamicValue.of(regexString);
        }
        return DynamicValue.of(
                availableFields.getReader(usedByRegex), regexFormat);
    }
    
    private static Search.Params remainingSearchParams(
            final Search.Params searchParams,
            final ParameterizedSetting setting) throws IOException {
        
        final Map<String, String> params = setting.params();
        return searchParams
                .withOccurrence(getOccurrence(params, setting.whole()))
                .withGroups(getGroups(params, setting.whole()))
                .withCharset(getCharset(params, setting.whole()));
    }
    
    private static int getOccurrence(
            final Map<? super String, String> params,
            final String message) throws IOException {
        
        return parseInt(
                params.getOrDefault(OCCURRENCE_KEY, "1"),
                "Occurrence: " + message);
    }
    
    private static int getGroups(
            final Map<? super String, String> params,
            final String message) throws IOException {
        
        return requireZeroPlus(
                params.getOrDefault(GROUP_KEY, "0"),
                "Groups: " + message);
    }
    
    private static Charset getCharset(
            final Map<? super String, String> params,
            final String message) throws IOException {
        
        if (params.containsKey(CHARSET_KEY)) {
            return parseCharset(params.get(CHARSET_KEY), "Charset: " + message);
        }
        return Charset.defaultCharset();
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
