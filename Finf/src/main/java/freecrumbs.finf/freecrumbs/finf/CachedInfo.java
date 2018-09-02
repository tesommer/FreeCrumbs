package freecrumbs.finf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Cached file info.
 * 
 * @author Tone Sommerland
 */
public final class CachedInfo extends Info {
    
    private static final Map<File, Info> INFO_CACHE = new HashMap<>();
    
    private final Map<String, String> cache = new HashMap<>();

    private CachedInfo(final InfoFields fields, final File file) {
        super(fields, file);
    }
    
    /**
     * Returns either a new or cached instance for the given file.
     * The field values retrieved by the returned instance, are also cached.
     */
    public static Info getInstance(final InfoFields fields, final File file) {
        synchronized (INFO_CACHE) {
            try {
                return getCached(
                        INFO_CACHE, file, () -> new CachedInfo(fields, file));
            } catch (final IOException ex) {
                throw new AssertionError(ex);
            }
        }
    }

    @Override
    protected String getValue(final InfoField field, final File file)
            throws IOException {
        
        return getCached(cache, field.getName(), () -> field.getValue(file));
    }
    
    @FunctionalInterface
    private interface Initial<T> {
        T get() throws IOException;
    }
    
    private static <K, V> V getCached(
            final Map<? super K, V> cache,
            final K key,
            final Initial<? extends V> initial) throws IOException {
        
        V value = cache.get(key);
        if (value == null) {
            value = initial.get();
            cache.put(key, value);
        }
        return value;
    }

}
