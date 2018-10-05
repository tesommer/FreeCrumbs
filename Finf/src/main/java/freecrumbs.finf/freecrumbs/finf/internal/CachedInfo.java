package freecrumbs.finf.internal;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import freecrumbs.finf.FieldReader;
import freecrumbs.finf.Info;

/**
 * Cached file info.
 * 
 * @author Tone Sommerland
 */
public final class CachedInfo extends Info {
    private Map<String, String> values;

    private CachedInfo(final FieldReader reader, final File file) {
        super(reader, file);
    }
    
    /**
     * Returns either a new or cached instance for the given file.
     * The field values retrieved by the returned instance, are also cached.
     * @param cache the info cache
     */
    public static Info getInstance(
            final FieldReader reader,
            final File file,
            final Map<File, Info> cache) {
        
        return cache.computeIfAbsent(file, f -> new CachedInfo(reader, f));
    }
    
    @Override
    protected Map<String, String> getValues(
            final FieldReader reader, final File file) throws IOException {
        
        if (values == null) {
            values = Map.copyOf(reader.readFieldValues(file));
        }
        return values;
    }

}
