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
    
    private Map<String, String> values;

    private CachedInfo(final FieldReader reader, final File file) {
        super(reader, file);
    }
    
    /**
     * Returns either a new or cached instance for the given file.
     * The field values retrieved by the returned instance, are also cached.
     */
    public static Info getInstance(final FieldReader reader, final File file) {
        synchronized (INFO_CACHE) {
            return INFO_CACHE.computeIfAbsent(
                    file, f -> new CachedInfo(reader, f));
        }
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
