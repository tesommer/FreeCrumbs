package freecrumbs.finf;

import java.io.File;
import java.io.IOException;

/**
 * File checksum generator.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface HashGenerator {
    
    public abstract byte[] digest(File file) throws IOException;

}
