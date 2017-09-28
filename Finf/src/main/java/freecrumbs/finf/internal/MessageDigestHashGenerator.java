package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import freecrumbs.finf.HashGenerator;

/**
 * A hash generator implemented using a {@code MessageDigest}.
 * 
 * @author Tone Sommerland
 */
public class MessageDigestHashGenerator implements HashGenerator {
    
    private static final HashGenerator DUMMY = file -> new byte[0];
    
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    
    private final String algorithm;
    private final int bufferSize;

    public MessageDigestHashGenerator(
            final String algorithm, final int bufferSize) {
        
        if (bufferSize < 1) {
            throw new IllegalArgumentException("bufferSize < 1");
        }
        this.algorithm = requireNonNull(algorithm, "algorithm");
        this.bufferSize = bufferSize;
    }

    public MessageDigestHashGenerator(final String algorithm) {
        this(algorithm, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * A convenience method that returns a dummy hash generator
     * if the info format does not contain a hash.
     */
    public static HashGenerator getInstance(
            final String algorithm, final TokenInfoFormat infoFormat) {
        
        if (infoFormat.containsHash()) {
            return new MessageDigestHashGenerator(algorithm);
        }
        return DUMMY;
    }

    @Override
    public byte[] digest(final File file) throws IOException {
        try (final InputStream in = new FileInputStream(file)) {
            return digest(MessageDigest.getInstance(algorithm), in);
        } catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    private byte[] digest(
            final MessageDigest messageDigest,
            final InputStream in) throws IOException {

        final byte[] buffer = new byte[bufferSize];
        int bytesRead = in.read(buffer);
        while (bytesRead > 0) {
            messageDigest.update(buffer, 0, bytesRead);
            bytesRead = in.read(buffer);
        }
        return messageDigest.digest();
    }

}
