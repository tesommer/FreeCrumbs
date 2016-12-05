package freecrumbs.finf;

/**
 * Fields in a file info unit.
 * 
 * @author Tone Sommerland
 */
public enum InfoField {
    
    /**
     * The path without the filename.
     */
    PATH,
    
    /**
     * The filename.
     */
    FILENAME,
    
    /**
     * The size in bytes.
     */
    SIZE,
    
    /**
     * The last modification timestamp.
     */
    MODIFIED,
    
    /**
     * Checksum.
     */
    HASH,
}
