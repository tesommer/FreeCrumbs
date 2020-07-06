package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.calclipse.lib.misc.EncodingUtil;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

/**
 * File-content hash.
 * 
 * @author Tone Sommerland
 */
public final class Hash
{
    private Hash()
    {
    }
    
    /**
     * Returns a hash-computation field.
     * @param name the field name
     * @param algorithm the hash algorithm
     */
    public static Field field(final String name, final String algorithm)
    {
        return Field.computed(name, new HashComputation(algorithm));
    }
    
    private static final class HashComputation implements FieldComputation
    {
        private final String algorithm;
        private MessageDigest messageDigest;

        private HashComputation(final String algorithm)
        {
            this.algorithm = requireNonNull(algorithm, "algorithm");
        }

        @Override
        public void reset(final File file) throws IOException
        {
            try
            {
                messageDigest = MessageDigest.getInstance(algorithm);
            }
            catch (final NoSuchAlgorithmException ex)
            {
                throw new IOException(ex);
            }
        }

        @Override
        public boolean update(
                final byte[] input,
                final int offset,
                final int length) throws IOException
        {
            messageDigest.update(input, offset, length);
            return true;
        }

        @Override
        public String finish() throws IOException
        {
            return EncodingUtil.bytesToHex(false, messageDigest.digest());
        }
        
    }

}
