package freecrumbs.finf;

/**
 * File info format.
 *
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface InfoFormat {

    public abstract String toString(Info info);
    
    /**
     * Whether or not this info format requires the hash.
     * By default it does.
     * If this method returns false,
     * it would allow an optimization by skipping the hash generation.
     */
    public default boolean requiresHash() {
        return true;
    }
}
