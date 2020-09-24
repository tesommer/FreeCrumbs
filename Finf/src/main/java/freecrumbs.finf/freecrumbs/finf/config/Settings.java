package freecrumbs.finf.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;

import freecrumbs.finf.config.command.CommandParser;
import freecrumbs.finf.config.filter.FilterParser;
import freecrumbs.finf.config.order.OrderParser;
import freecrumbs.finf.config.search.SearchParser;
import freecrumbs.finf.field.Classification;
import freecrumbs.finf.field.Command;
import freecrumbs.finf.field.Search;

/**
 * Static config-settings methods.
 * 
 * @author Tone Sommerland
 */
public final class Settings
{
    private static final String HASH_ALGORITHMS_KEY      = "hash.algorithms";
    private static final String DATE_FORMAT_KEY          = "date.format";
    private static final String PREFILTER_KEY            = "prefilter";
    private static final String OUTPUT_KEY               = "output";
    private static final String FILTER_KEY               = "filter";
    private static final String ORDER_KEY                = "order";
    private static final String COUNT_KEY                = "count";
    private static final String VAR_KEY                  = "var";
    
    private static final String DEFAULT_HASH_ALGORITHMS  = "md5 sha-1 sha-256";
    private static final String DEFAULT_DATE_FORMAT      = "yyyy-MM-dd HH:mm";
    private static final String DEFAULT_PREFILTER        = "1";
    private static final String DEFAULT_OUTPUT           = "<filename><eol>";
    
    private static final char   KEYSEP                   = '.';
    private static final String FILTER_KEY_PREFIX        = FILTER_KEY + KEYSEP;
    private static final String VAR_KEY_PREFIX           = VAR_KEY + KEYSEP;
    
    private static final String WS_DELIMITER             = "[ \\t]+";
    
    private Settings()
    {
    }
    
    public static AvailableFields availableFields(
            final Properties props, final Locale locale) throws IOException
    {
        final String dateFormat = props.getProperty(
                DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT);
        final var params = new AvailableFields.Params()
                .withTime(dateFormat, locale)
                .withClassification(Classification.Heuristic.DEFAULT)
                .withHash(hashAlgorithms(props));
        return withVarFields(new AvailableFields(params), props);
    }
    
    private static AvailableFields withVarFields(
            final AvailableFields availableFields,
            final Properties props) throws IOException
    {
        final var initialSearchParams = new Search.Params();
        final var initialCommandParams = new Command.Params();
        var result = availableFields;
        for (final String key : varKeys(props))
        {
            final String setting = props.getProperty(key);
            if (SearchParser.isSearch(setting))
            {
                result = SearchParser.withAnotherSearch(
                        result,
                        initialSearchParams.withFieldNamePrefix(key +  KEYSEP),
                        setting);
            }
            else if (CommandParser.isCommand(setting))
            {
                result = CommandParser.withAnotherCommand(
                        result,
                        initialCommandParams.withFieldNamePrefix(key +  KEYSEP),
                        setting);
            }
        }
        return result;
    }

    public static TokenFormatter output(final Properties props)
    {
        return new TokenFormatter(
                props.getProperty(OUTPUT_KEY, DEFAULT_OUTPUT));
    }

    public static int count(final Properties props) throws IOException
    {
        try
        {
            return Integer.parseInt(props.getProperty(COUNT_KEY, "-1"));
        }
        catch (final NumberFormatException ex)
        {
            throw new IOException(ex);
        }
    }
    
    public static OrderParser orderParser(
            final Properties props, final String[] availableFieldNames)
    {
        final String setting = props.getProperty(ORDER_KEY);
        return new OrderParser(setting, availableFieldNames);
    }

    /**
     * If the filter setting is not present,
     * the returned collection will be empty.
     * None of the filters from the returned parsers will be null.
     */
    public static List<FilterParser> filterParsers(final Properties props)
            throws IOException
    {
        final var filterParsers = new ArrayList<FilterParser>();
        for (final String key : filterKeys(props))
        {
            filterParsers.add(new FilterParser(props.getProperty(key)));
        }
        return filterParsers;
    }
    
    public static boolean isPrefilter(final Properties props)
    {
        return isTrue(props.getProperty(PREFILTER_KEY, DEFAULT_PREFILTER));
    }
    
    /**
     * Returns an array containing non-empty, trimmed
     * space/tab-delimited substrings of the given string.
     */
    public static String[] splitAtWhitespace(final String str)
    {
        return Stream.of(str.split(WS_DELIMITER))
                .map(s -> s.trim())
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }
    
    private static boolean isFilterKey(final String key)
    {
        return FILTER_KEY.equals(key) || key.startsWith(FILTER_KEY_PREFIX);
    }
    
    private static boolean isVarKey(final String key)
    {
        return VAR_KEY.equals(key) || key.startsWith(VAR_KEY_PREFIX);
    }

    private static String[] hashAlgorithms(final Properties props)
    {
        return props.getProperty(HASH_ALGORITHMS_KEY, DEFAULT_HASH_ALGORITHMS)
                .split(WS_DELIMITER);
    }
    
    private static boolean isTrue(final String propertyValue)
    {
        return !"0".equals(propertyValue);
    }

    private static String[] filterKeys(final Properties props) {
        return props.stringPropertyNames().stream()
                .filter(Settings::isFilterKey)
                .sorted()
                .toArray(String[]::new);
    }

    private static String[] varKeys(final Properties props) {
        return props.stringPropertyNames().stream()
                .filter(Settings::isVarKey)
                .sorted()
                .toArray(String[]::new);
    }

}
