package freecrumbs.finf;

import java.io.File;
import java.io.IOException;

/**
 * A field-value retriever.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface FieldValue {
    
    /**
     * Retrieves the value from the given file.
     */
    public abstract String get(File file) throws IOException;

}
