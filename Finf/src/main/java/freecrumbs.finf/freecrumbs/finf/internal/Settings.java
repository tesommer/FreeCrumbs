package freecrumbs.finf.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Static config-settings methods.
 * 
 * @author Tone Sommerland
 */
public final class Settings {

    private static final String HASH_ALGORITHMS_KEY = "hash.algorithms";
    private static final String DATE_FORMAT_KEY = "date.format";
    private static final String PREFILTER_KEY = "prefilter";
    private static final String OUTPUT_KEY = "output";
    private static final String FILTER_KEY = "filter";
    private static final String ORDER_KEY = "order";
    private static final String COUNT_KEY = "count";
    
    private static final String DEFAULT_HASH_ALGORITHMS = "md5 sha-1 sha-256";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String DEFAULT_PREFILTER = "1";
    private static final String DEFAULT_OUTPUT = "${filename}${eol}";
    
    private static final String HASH_ALGORITHM_DELIMITER = "[ |\\t]+";
    private static final String FILTER_KEY_PREFIX = FILTER_KEY + '.';
    
    private Settings() {
    }
    
    public static AvailableFields getAvailableFields(
            final Properties props, final Locale locale) throws IOException {
        
        final String[] hashAlgorithms = getHashAlgorithms(props);
        final String dateFormat = props.getProperty(
                DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT);
        return new AvailableFields(dateFormat, locale, hashAlgorithms);
    }

    public static TokenInfoFormat getOutput(final Properties props) {
        return new TokenInfoFormat(
                props.getProperty(OUTPUT_KEY, DEFAULT_OUTPUT));
    }

    public static int getCount(final Properties props) throws IOException {
        try {
            return Integer.parseInt(props.getProperty(COUNT_KEY, "-1"));
        } catch (final NumberFormatException ex) {
            throw new IOException(ex);
        }
    }
    
    public static OrderParser getOrderParser(
            final Properties props, final String[] availableFieldNames) {
        
        final String setting = props.getProperty(ORDER_KEY);
        return new OrderParser(setting, availableFieldNames);
    }

    public static List<FilterParser> getFilterParsers(final Properties props)
            throws IOException {
        
        final var filterParsers = new ArrayList<FilterParser>();
        final String[] keys = props.stringPropertyNames().stream()
                .filter(Settings::isFilterKey)
                .sorted()
                .toArray(String[]::new);
        for (final String key : keys) {
            filterParsers.add(new FilterParser(props.getProperty(key)));
        }
        return filterParsers;
    }
    
    public static boolean isPrefilter(final Properties props) {
        return isTrue(props.getProperty(PREFILTER_KEY, DEFAULT_PREFILTER));
    }
    
    private static boolean isFilterKey(final String key) {
        return FILTER_KEY.equals(key) || key.startsWith(FILTER_KEY_PREFIX);
    }

    private static String[] getHashAlgorithms(final Properties props) {
        return props.getProperty(HASH_ALGORITHMS_KEY, DEFAULT_HASH_ALGORITHMS)
                .split(HASH_ALGORITHM_DELIMITER);
    }
    
    private static boolean isTrue(final String propertyValue) {
        return !"0".equals(propertyValue);
    }

}