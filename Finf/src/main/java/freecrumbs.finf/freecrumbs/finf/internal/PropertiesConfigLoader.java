package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

import freecrumbs.finf.Config;
import freecrumbs.finf.ConfigLoader;
import freecrumbs.finf.FieldReader;
import freecrumbs.finf.Info;

/**
 * Loads configuration from a properties file.
 * Sample file:
 * <pre>
 * {@code
 * hash.algorithms=MD5 SHA-256
 * info.format=${path}${filename} ${size} ${modified} ${md5} ${sha-256}${eol}
 * date.format=yyyy-MM-dd HH:mm
 * file.filter=.*\.html
 * order=filename size asc modified desc
 * count=100
 * }
 * </pre>
 * Alternative file filter &ndash; format pattern:
 * <pre>
 * {@code
 * file.filter=${filename}++.+\.html--index\..{3,4}
 * }
 * </pre>
 * ({@code ++} precedes inclusion patterns and {@code --} exclusion patterns.)
 * 
 * @author Tone Sommerland
 */
public final class PropertiesConfigLoader implements ConfigLoader {
    
    private static final String HASH_ALGORITHMS_KEY = "hash.algorithms";
    private static final String INFO_FORMAT_KEY = "info.format";
    private static final String DATE_FORMAT_KEY = "date.format";
    private static final String FILE_FILTER_KEY = "file.filter";
    private static final String ORDER_KEY = "order";
    private static final String COUNT_KEY = "count";
    
    private static final String DEFAULT_HASH_ALGORITHMS = "md5 sha-1 sha-256";
    private static final String DEFAULT_INFO_FORMAT = "${filename}${eol}";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    private static final String HASH_ALGORITHM_DELIMITER = "[ |\\t]+";
    private static final int BUFFER_SIZE = 2048;
    private static final int REGEX_FLAGS = 0;
    
    private final Locale locale;
    private final Map<String, String> overrides;

    /**
     * Creates a properties config-loader.
     * @param overrides overrides keys in the properties file
     * (a key with a value of {@code null} means 'use default')
     */
    public PropertiesConfigLoader(
            final Locale locale, final Map<String, String> overrides) {
        
        this.locale = requireNonNull(locale, "locale");
        // Do not use Map.copyOf because overrides may contain null.
        this.overrides = new HashMap<>(overrides);
    }
    
    @Override
    public Config loadConfig(final Reader reader) throws IOException {
        final Properties props = getProperties(reader);
        final AvailableFields availableFields = getAvailableFields(props);
        final String[] availableFieldNames = availableFields.getNames();
        final TokenInfoFormat infoFormat = getInfoFormat(props);
        final FileFilterParser fileFilterParser = getFileFilterParser(props);
        final OrderParser orderParser = getOrderParser(
                props, availableFieldNames);
        final String[] usedFieldNames = getUsedFieldNames(
                availableFieldNames, infoFormat, fileFilterParser, orderParser);
        final FieldReader fieldReader = availableFields.getReader(
                BUFFER_SIZE, usedFieldNames);
        final Function<File, Info> infoGenerator = getInfoGenerator(
                fieldReader);
        return new Config.Builder(infoGenerator, infoFormat)
                .setFileFilter(fileFilterParser.getFileFilter(
                        REGEX_FLAGS, infoGenerator))
                .setOrder(orderParser.getOrder())
                .setCount(getCount(props))
                .build();
    }

    private Properties getProperties(final Reader reader) throws IOException {
        final var props = new Properties();
        props.load(reader);
        applyOverrides(props, overrides);
        return props;
    }

    private static void applyOverrides(
            final Properties props, final Map<String, String> overrides) {
        
        overrides.forEach((key, value) -> {
            if (value == null) {
                props.remove(key);
            } else {
                props.put(key, value);
            }
        });
    }
    
    private AvailableFields getAvailableFields(final Properties props)
            throws IOException {
        
        final String[] hashAlgorithms = getHashAlgorithms(props);
        final String dateFormat = props.getProperty(
                DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT);
        return new AvailableFields(locale, dateFormat, hashAlgorithms);
    }

    private static String[] getHashAlgorithms(final Properties props) {
        return props.getProperty(HASH_ALGORITHMS_KEY, DEFAULT_HASH_ALGORITHMS)
                .split(HASH_ALGORITHM_DELIMITER);
    }

    private static String[] getUsedFieldNames(
            final String[] availableFieldNames,
            final TokenInfoFormat infoFormat,
            final FileFilterParser fileFilterParser,
            final OrderParser orderParser) {
        
        final String[] usedByInfoFormat = infoFormat.getUsedFieldNames(
                availableFieldNames);
        final String[] usedByFileFilter = fileFilterParser.getUsedFieldNames(
                availableFieldNames);
        final String[] usedByOrder = orderParser.getUsedFieldNames();
        return Stream.concat(
                Stream.concat(
                        Stream.of(usedByInfoFormat),
                        Stream.of(usedByFileFilter)),
                Stream.of(usedByOrder))
                .distinct()
                .toArray(String[]::new);
    }

    private static Function<File, Info> getInfoGenerator(
            final FieldReader fieldReader) {
        
        final var cache = new HashMap<File, Info>();
        return file -> CachedInfo.getInstance(fieldReader, file, cache);
    }

    private static TokenInfoFormat getInfoFormat(final Properties props) {
        return new TokenInfoFormat(
                props.getProperty(INFO_FORMAT_KEY, DEFAULT_INFO_FORMAT));
    }

    private static int getCount(final Properties props) throws IOException {
        try {
            return Integer.parseInt(props.getProperty(COUNT_KEY, "-1"));
        } catch (final NumberFormatException ex) {
            throw new IOException(ex);
        }
    }

    private static FileFilterParser getFileFilterParser(final Properties props)
            throws IOException {
        
        final String setting = props.getProperty(FILE_FILTER_KEY);
        return new FileFilterParser(setting);
    }
    
    private static OrderParser getOrderParser(
            final Properties props, final String[] availableFieldNames) {
        
        final String setting = props.getProperty(ORDER_KEY);
        return new OrderParser(setting, availableFieldNames);
    }
    
}
