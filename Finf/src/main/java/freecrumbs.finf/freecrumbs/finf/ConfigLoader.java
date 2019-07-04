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
     * If {@code overrides} contains a key with a value of {@code null},
     * the default setting for that key will be used.
     * @param overrides configuration overrides
     */
    public static ConfigLoader getDefault(final Map<String, String> overrides) {
        return new PropertiesConfigLoader(Locale.getDefault(), overrides);
    }

    public abstract Config loadConfig(Reader reader) throws IOException;
}
