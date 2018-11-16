package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.calclipse.lib.util.EncodingUtil;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

/**
 * File-content hash.
 * 
 * @author Tone Sommerland
 */
public final class Hash {
    
    private Hash() {
    }
    
    /**
     * Returns a hash-computation field.
     * @param name the field name
     * @param algorithm the hash algorithm
     */
    public static Field getField(final String name, final String algorithm) {
        return Field.getInstance(name, new HashFieldComputation(algorithm));
    }
    
    private static final class HashFieldComputation
        implements FieldComputation {
        
        private final String algorithm;
        private MessageDigest messageDigest;

        private HashFieldComputation(final String algorithm) {
            this.algorithm = requireNonNull(algorithm, "algorithm");
        }

        @Override
        public void reset(final File file) throws IOException {
            try {
                messageDigest = MessageDigest.getInstance(algorithm);
            } catch (final NoSuchAlgorithmException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public boolean update(
                final byte[] input,
                final int offset,
                final int length) throws IOException {
            
            messageDigest.update(input, offset, length);
            return true;
        }

        @Override
        public String get() throws IOException {
            return EncodingUtil.bytesToHex(false, messageDigest.digest());
        }
        
    }

}
