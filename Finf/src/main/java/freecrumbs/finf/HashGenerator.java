package freecrumbs.finf;

import java.io.IOException;
import java.io.InputStream;

/**
 * File checksum generator.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface HashGenerator {
    
    public abstract byte[] digest(InputStream in) throws IOException;

}
