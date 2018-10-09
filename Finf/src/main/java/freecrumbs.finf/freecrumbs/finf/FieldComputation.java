package freecrumbs.finf;

import java.io.IOException;

/**
 * An field-value computation.
 * 
 * @author Tone Sommerland
 */
public interface FieldComputation {
    
    /**
     * Resets the computation.
     */
    public abstract void reset() throws IOException;
    
    /**
     * Updates the computation with the given input.
     * The input array should not be modified by this method.
     * If this method returns {@code false},
     * it it will not be called again during the ongoing computation.
     * @param input the next input to process
     * @param offset the offset to start from in the input array
     * @param length the number of bytes to devour
     * @return {@code true}
     * if this computation requires more input for the current computation
     */
    public abstract boolean update(byte[] input, int offset, int length)
            throws IOException;
    
    /**
     * Finishes the computation and returns the calculated value.
     */
    public abstract String get() throws IOException;

}
