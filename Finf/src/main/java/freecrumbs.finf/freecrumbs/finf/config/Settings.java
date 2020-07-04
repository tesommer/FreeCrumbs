package freecrumbs.finf.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import freecrumbs.finf.config.filter.FilterParser;
import freecrumbs.finf.config.order.OrderParser;
import freecrumbs.finf.config.search.SearchParser;
import freecrumbs.finf.field.Classification;
import freecrumbs.finf.field.DynamicValue;
import freecrumbs.finf.field.Search;

/**
 * Static config-settings methods.
 * 
 * @author Tone Sommerland
 */
public final class Settings
{
    private static final String HASH_ALGORITHMS_KEY = "hash.algorithms";
    private static final String DATE_FORMAT_KEY = "date.format";
    private static final String PREFILTER_KEY = "prefilter";
    private static final String OUTPUT_KEY = "output";
    private static final String FILTER_KEY = "filter";
    private static final String ORDER_KEY = "order";
    private static final String COUNT_KEY = "count";
    private static final String SEARCH_KEY = "search";
    
    private static final String DEFAULT_HASH_ALGORITHMS = "md5 sha-1 sha-256";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String DEFAULT_PREFILTER = "1";
    private static final String DEFAULT_OUTPUT = "${filename}${eol}";
    
    private static final char KEYSEP = '.';
    private static final String HASH_ALGORITHM_DELIMITER = "[ |\\t]+";
    private static final String FILTER_KEY_PREFIX = FILTER_KEY + KEYSEP;
    private static final String SEARCH_KEY_PREFIX = SEARCH_KEY + KEYSEP;
    
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
        return withSearchFields(new AvailableFields(params), props);
    }
    
    private static AvailableFields withSearchFields(
            final AvailableFields availableFields,
            final Properties props) throws IOException
    {
        final String[] keys = props.stringPropertyNames().stream()
                .filter(Settings::isSearchKey)
                .sorted()
                .toArray(String[]::new);
        final var initialSearchParams = new Search.Params(DynamicValue.of(""));
        var result = availableFields;
        for (final String key : keys)
        {
            result = SearchParser.withAnotherSearch(
                    result,
                    initialSearchParams.withFieldNamePrefix(key +  KEYSEP),
                    props.getProperty(key));
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
     */
    public static List<FilterParser> filterParsers(final Properties props)
            throws IOException
    {
        final var filterParsers = new ArrayList<FilterParser>();
        final String[] keys = props.stringPropertyNames().stream()
                .filter(Settings::isFilterKey)
                .sorted()
                .toArray(String[]::new);
        for (final String key : keys)
        {
            filterParsers.add(new FilterParser(props.getProperty(key)));
        }
        return filterParsers;
    }
    
    public static boolean isPrefilter(final Properties props)
    {
        return isTrue(props.getProperty(PREFILTER_KEY, DEFAULT_PREFILTER));
    }
    
    private static boolean isFilterKey(final String key)
    {
        return FILTER_KEY.equals(key) || key.startsWith(FILTER_KEY_PREFIX);
    }
    
    private static boolean isSearchKey(final String key)
    {
        return SEARCH_KEY.equals(key) || key.startsWith(SEARCH_KEY_PREFIX);
    }

    private static String[] hashAlgorithms(final Properties props)
    {
        return props.getProperty(HASH_ALGORITHMS_KEY, DEFAULT_HASH_ALGORITHMS)
                .split(HASH_ALGORITHM_DELIMITER);
    }
    
    private static boolean isTrue(final String propertyValue)
    {
        return !"0".equals(propertyValue);
    }

}
