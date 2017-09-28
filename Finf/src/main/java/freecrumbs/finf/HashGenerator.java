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
    
    /**
     * Always returns an empty array.
     */
    public static final HashGenerator DUMMY = in -> new byte[] {};
    
    public abstract byte[] digest(InputStream in) throws IOException;

}
