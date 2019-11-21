package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import freecrumbs.finf.Config;
import freecrumbs.finf.ConfigLoader;

/**
 * Loads configuration from a properties file.
 * Sample file:
 * <pre>
 * {@code
 * hash.algorithms=MD5 SHA-1 SHA-256
 * output=${path}${filename} ${size} ${modified} ${md5} ${sha-256}${eol}
 * date.format=yyyy-MM-dd HH:mm
 * filter=.*\.html
 * order=filename size asc modified desc
 * count=100
 * }
 * </pre>
 * Alternative filter &ndash; format pattern:
 * <pre>
 * {@code
 * filter=${filename}++.+\.html--index\..{3,4}
 * }
 * </pre>
 * ({@code ++} precedes inclusion patterns and {@code --} exclusion patterns.)
 * 
 * @author Tone Sommerland
 */
public final class PropertiesConfigLoader implements ConfigLoader
{
    private final Locale locale;
    private final Map<String, String> overrides;

    /**
     * Creates a properties config-loader.
     * @param overrides overrides keys in the properties file
     * (a key with a value of {@code null} means 'use default')
     */
    public PropertiesConfigLoader(
            final Locale locale, final Map<String, String> overrides)
    {
        this.locale = requireNonNull(locale, "locale");
        // Do not use Map.copyOf because overrides may contain null.
        this.overrides = new HashMap<>(overrides);
    }
    
    @Override
    public Config loadConfig(final Reader reader) throws IOException
    {
        final Properties props = getProperties(reader);
        final Manifold manifold = new Manifold(props, locale);
        return new Config.Builder(
                manifold.getInfoGenerator(), manifold.getInfoFormat())
                    .setFileFilter(manifold.getFileFilter())
                    .setOrder(manifold.getOrder())
                    .setCount(Settings.getCount(props))
                    .build();
    }

    private Properties getProperties(final Reader reader) throws IOException
    {
        final var props = new Properties();
        props.load(reader);
        applyOverrides(props, overrides);
        return props;
    }

    private static void applyOverrides(
            final Properties props, final Map<String, String> overrides)
    {
        overrides.forEach((key, value) ->
        {
            if (value == null)
            {
                props.remove(key);
            }
            else
            {
                props.put(key, value);
            }
        });
    }
    
}
