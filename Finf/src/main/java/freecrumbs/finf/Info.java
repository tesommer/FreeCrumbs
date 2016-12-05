package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

/**
 * A file info unit.
 * Includes the path without the filename,
 * the filename,
 * the size in bytes,
 * last modified
 * and a checksum of the file.
 *
 * @author Tone Sommerland
 */
public class Info {
    private final String path;
    private final String filename;
    private final long size;
    private final long modified;
    private final String hash;

    public Info(
                final String path,
                final String filename,
                final long size,
                final long modified,
                final String hash) {
        
        this.path = requireNonNull(path, "path");
        this.filename = requireNonNull(filename, "filename");
        this.size = size;
        this.modified = modified;
        this.hash = requireNonNull(hash, "hash");
    }
    
    public String getPath() {
        return path;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public long getSize() {
        return size;
    }
    
    public long getModified() {
        return modified;
    }
    
    public String getHash() {
        return hash;
    }
    
}
