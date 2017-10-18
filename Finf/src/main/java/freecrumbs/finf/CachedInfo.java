package freecrumbs.finf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This info caches the info fields.
 * 
 * @author Tone Sommerland
 */
public class CachedInfo extends Info {
    private final Map<String, String> cache = new HashMap<>();

    public CachedInfo(final File file, final InfoField... fields) {
        super(file, fields);
    }

    @Override
    protected String getValue(final InfoField field, final File file)
            throws IOException {
        
        final String fieldName = field.getName();
        String value = cache.get(fieldName);
        if (value == null) {
            value = field.getValue(file);
            cache.put(fieldName, value);
        }
        return value;
    }

}
