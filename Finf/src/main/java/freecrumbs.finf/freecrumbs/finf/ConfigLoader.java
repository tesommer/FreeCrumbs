package freecrumbs.finf;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import java.util.Map;

import freecrumbs.finf.internal.PropertiesConfigLoader;

/**
 * Configuration loader.
 *
 * @author Tone Sommerland
 */
public interface ConfigLoader {
    
    /**
     * Returns the default configuration loader.
     * @param overrides configuration overrides.
     * A key with a null value means use the default setting.
     */
    public static ConfigLoader getDefault(final Map<String, String> overrides) {
        return new PropertiesConfigLoader(Locale.getDefault(), overrides);
    }

    public abstract Config loadConfig(Reader reader) throws IOException;
}
