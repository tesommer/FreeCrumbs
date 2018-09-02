package freecrumbs.finf;

import java.io.File;
import java.io.IOException;

/**
 * A file-info field.
 * 
 * @author Tone Sommerland
 */
public interface InfoField {
    
    /**
     * The (unique) name of this field.
     */
    public abstract String getName();
    
    /**
     * Retrieves the value of this field from the given file.
     */
    public abstract String getValue(File file) throws IOException;

}
