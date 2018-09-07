package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import freecrumbs.finf.CachedInfo;
import freecrumbs.finf.Config;
import freecrumbs.finf.ConfigLoader;
import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFields;
import freecrumbs.finf.InfoFormat;
import freecrumbs.finf.field.FilenameField;
import freecrumbs.finf.field.HashField;
import freecrumbs.finf.field.ModifiedField;
import freecrumbs.finf.field.PathField;
import freecrumbs.finf.field.SizeField;

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
     * Creates a properties config-loader.
     * @param overrides overrides keys in the properties file
     * (a key with a value of {@code null} means 'use default')
     */
    public PropertiesConfigLoader(
            final Locale locale, final Map<String, String> overrides) {
        
        this.locale = requireNonNull(locale, "locale");
        this.overrides = Map.copyOf(overrides);
    }
    
    @Override
    public Config loadConfig(final Reader reader) throws IOException {
        final Properties props = getProperties(reader);
        final InfoFields fields = getInfoFields(props);
        final Function<File, Info> infoGenerator = getInfoGenerator(
                props, fields);
        return new Config.Builder(infoGenerator, getInfoFormat(props))
                .setFileFilter(getFileFilter(props, infoGenerator))
                .setOrder(getOrder(props, fields))
                .setCount(getCount(props))
                .build();
    }

    private Properties getProperties(final Reader reader) throws IOException {
        final Properties props = new Properties();
        props.load(reader);
        applyOverrides(props, overrides);
        return props;
    }
    
    private InfoFields getInfoFields(final Properties props)
            throws IOException {

        final String hashAlgorithm = props.getProperty(
                HASH_ALGORITHM_KEY, DEFAULT_HASH_ALGORITHM);
        final String dateFormat = props.getProperty(
                DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT);
        return InfoFields.of(
                PathField.INSTANCE,
                FilenameField.INSTANCE,
                SizeField.INSTANCE,
                ModifiedField.getInstance(dateFormat, locale),
                HashField.getInstance(hashAlgorithm));
    }
    
    private static Function<File, Info> getInfoGenerator(
            final Properties props, final InfoFields fields) {
        
        return file -> CachedInfo.getInstance(fields, file);
    }

    private static InfoFormat getInfoFormat(final Properties props) {
        return new TokenInfoFormat(
                props.getProperty(INFO_FORMAT_KEY, DEFAULT_INFO_FORMAT));
    }

    private static FileFilter getFileFilter(
            final Properties props,
            final Function<? super File, ? extends Info> infoGenerator)
                    throws IOException {
        
        final String setting = props.getProperty(FILE_FILTER_KEY);
        if (setting == null) {
            return null;
        }
        return getFileFilterParser(infoGenerator).parse(setting);
    }

    private static Comparator<Info> getOrder(
            final Properties props, final InfoFields fields) {
        
        final String setting = props.getProperty(ORDER_KEY);
        if (setting == null) {
            return null;
        }
        return getOrderParser(fields).parse(setting);
    }

    private static int getCount(final Properties props) throws IOException {
        try {
            return Integer.parseInt(props.getProperty(COUNT_KEY, "-1"));
        } catch (final NumberFormatException ex) {
            throw new IOException(ex);
        }
    }

    private static void applyOverrides(
            final Properties props, final Map<String, String> overrides) {
        
        for (final String key : overrides.keySet()) {
            final String value = overrides.get(key);
            if (value == null) {
                props.remove(key);
            } else {
                props.put(key, value);
            }
        }
    }
    
    private static FileFilterParser getFileFilterParser(
            final Function<? super File, ? extends Info> infoGenerator) {
        
        return new FileFilterParser(REGEX_FLAGS, infoGenerator);
    }
    
    private static OrderParser getOrderParser(final InfoFields fields) {
        return new OrderParser(fields.getNames());
    }
    
}
