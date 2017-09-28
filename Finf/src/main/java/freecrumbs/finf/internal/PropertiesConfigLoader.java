package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import freecrumbs.finf.Config;
import freecrumbs.finf.ConfigLoader;
import freecrumbs.finf.HashGenerator;
import freecrumbs.finf.Info;

/**
 * Loads configuration from a properties file.
 * Sample file:
 * <pre>
 * {@code
 * hash.algorithm=SHA-256
 * info.format=${path}${filename} ${size} ${modified} ${hash}
 * date.format=yyyy-MM-dd HH:mm
 * file.filter=.*\.html
 * order=filename size asc modified desc
 * count=100
 * }
 * </pre>
 * 
 * @author Tone Sommerland
 */
public class PropertiesConfigLoader implements ConfigLoader {
    
    public static final String HASH_ALGORITHM_KEY = "hash.algorithm";
    public static final String INFO_FORMAT_KEY = "info.format";
    public static final String DATE_FORMAT_KEY = "date.format";
    public static final String FILE_FILTER_KEY = "file.filter";
    public static final String ORDER_KEY = "order";
    public static final String COUNT_KEY = "count";
    
    private static final String DEFAULT_HASH_ALGORITHM = "MD5";
    private static final String DEFAULT_INFO_FORMAT = "${filename}";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    private static final int REGEX_FLAGS = 0;
    
    private final Locale locale;
    private final Map<String, String> overrides;

    /**
     * Creates a properties config loader.
     * @param overrides overrides keys in the properties file
     * (a null value means default)
     */
    public PropertiesConfigLoader(
            final Locale locale, final Map<String, String> overrides) {
        
        this.locale = requireNonNull(locale, "locale");
        this.overrides = new HashMap<>(overrides);
    }
    
    /**
     * Creates a properties config loader.
     */
    public PropertiesConfigLoader(final Locale locale) {
        this(locale, new HashMap<>());
    }
    
    @Override
    public Config loadConfig(final Reader reader) throws IOException {
        final Properties props = getProperties(reader);
        final TokenInfoFormat infoFormat = getInfoFormat(props);
        final HashGenerator hashGenerator = getHashGenerator(props, infoFormat);
        final FileFilter fileFilter = getFileFilter(props);
        final Comparator<Info> order = getOrder(props);
        final int count = getCount(props);
        return new Config(
                hashGenerator,
                infoFormat,
                fileFilter,
                order,
                count);
    }

    private Properties getProperties(final Reader reader) throws IOException {
        final Properties props = new Properties();
        props.load(reader);
        for (final String key : overrides.keySet()) {
            final String value = overrides.get(key);
            if (value == null) {
                props.remove(key);
            } else {
                props.put(key, value);
            }
        }
        return props;
    }

    private static HashGenerator getHashGenerator(
            final Properties props, final TokenInfoFormat infoFormat) {
        
        if (infoFormat.containsHash()) {
            return new MessageDigestHashGenerator(props.getProperty(
                    HASH_ALGORITHM_KEY, DEFAULT_HASH_ALGORITHM));
        }
        return HashGenerator.DUMMY;
    }

    private TokenInfoFormat getInfoFormat(final Properties props)
            throws IOException {
        
        return new TokenInfoFormat(
                props.getProperty(INFO_FORMAT_KEY, DEFAULT_INFO_FORMAT),
                props.getProperty(DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT),
                locale);
    }

    private FileFilter getFileFilter(final Properties props)
            throws IOException {
        
        final String setting = props.getProperty(FILE_FILTER_KEY);
        if (setting == null) {
            return null;
        }
        return getFileFilterParser(props, locale).parse(setting);
    }

    private Comparator<Info> getOrder(final Properties props) {
        final String setting = props.getProperty(ORDER_KEY);
        if (setting == null) {
            return null;
        }
        return new OrderSpecInfoSorter(getOrderSpecs(setting, locale));
    }

    private static int getCount(final Properties props) throws IOException {
        try {
            return Integer.parseInt(props.getProperty(COUNT_KEY, "-1"));
        } catch (final NumberFormatException ex) {
            throw new IOException(ex);
        }
    }
    
    private static FileFilterParser getFileFilterParser(
            final Properties props, final Locale locale) {
        
        return new FileFilterParser(
                props.getProperty(DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT),
                locale,
                props.getProperty(HASH_ALGORITHM_KEY, DEFAULT_HASH_ALGORITHM),
                REGEX_FLAGS);
    }
    
    private static OrderSpec[] getOrderSpecs(
            final String orderSetting, final Locale locale) {
        
        final String orderTLC = orderSetting.toLowerCase(locale);
        final Collection<OrderSpec> orderSpecs
            = new ArrayList<>(InfoField.values().length);
        for (final InfoField field : InfoField.values()) {
            boolean desc = false;
            final String name = field.name().toLowerCase(locale);
            int precedence = orderTLC.indexOf(name + " asc");
            if (precedence < 0) {
                precedence = orderTLC.indexOf(name + " desc");
                if (precedence > -1) {
                    desc = true;
                } else {
                    precedence = orderTLC.indexOf(name);
                }
            }
            if (precedence > -1) {
                orderSpecs.add(new OrderSpec(field, precedence, desc));
            }
        }
        return orderSpecs.stream().toArray(OrderSpec[]::new);
    }
    
}
