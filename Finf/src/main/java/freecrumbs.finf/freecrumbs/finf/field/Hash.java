package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.calclipse.lib.util.EncodingUtil;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

/**
 * A hash of the file content.
 * The name of this field is {@code "hash"}.
 * 
 * @author Tone Sommerland
 */
public final class Hash {
    
    private static final String NAME = "hash";
    
    private Hash() {
    }
    
    public static Field getField(final String algorithm) {
        return Field.getInstance(NAME, new HashFieldComputation(algorithm));
    }
    
    private static final class HashFieldComputation
        implements FieldComputation {
        
        private final String algorithm;
        private MessageDigest messageDigest;

        private HashFieldComputation(final String algorithm) {
            this.algorithm = requireNonNull(algorithm, "algorithm");
        }

        @Override
        public void reset() throws IOException {
            try {
                messageDigest = MessageDigest.getInstance(algorithm);
            } catch (final NoSuchAlgorithmException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public void update(
                final byte[] input,
                final int offset,
                final int length) throws IOException {
            
            messageDigest.update(input, offset, length);
        }

        @Override
        public String get() throws IOException {
            return EncodingUtil.bytesToHex(false, messageDigest.digest());
        }
        
    }

}
