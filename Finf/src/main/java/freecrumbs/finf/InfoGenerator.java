package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;

/**
 * Generates file info.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface InfoGenerator {
    
    /**
     * Generates info about the given file using the given info generator.
     */
    public static Info getInfo(
            final File file,
            final InfoGenerator infoGenerator) throws IOException {
        
        return infoGenerator.getInfo(file, infoGenerator.getHashGenerator());
    }
    
    public abstract Info getInfo(File file, HashGenerator hashGenerator)
            throws IOException;
    
    /**
     * Returns the hash generator associated with this info generator.
     * The default implementation returns a dummy hash generator
     * that always returns an empty array.
     */
    public default HashGenerator getHashGenerator() {
        return file -> new byte[0];
    }
    
    /**
     * Returns an info generator that is like this one,
     * except it's associated with the given hash generator.
     */
    public default InfoGenerator use(final HashGenerator hashGenerator) {
        requireNonNull(hashGenerator, "hashGenerator");
        return new InfoGenerator() {
            @Override
            public Info getInfo(
                    final File file,
                    final HashGenerator hashGenerator) throws IOException {
                return InfoGenerator.this.getInfo(file, hashGenerator);
            }
            @Override
            public HashGenerator getHashGenerator() {
                return hashGenerator;
            }
        };
    }

}
