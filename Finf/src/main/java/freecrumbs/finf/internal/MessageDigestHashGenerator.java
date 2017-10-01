package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import freecrumbs.finf.HashGenerator;
import freecrumbs.finf.InfoFormat;
import freecrumbs.finf.InfoGenerator;

/**
 * A hash generator implemented using a {@code MessageDigest}.
 * 
 * @author Tone Sommerland
 */
public final class MessageDigestHashGenerator implements HashGenerator {
    
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    
    private final String algorithm;
    private final int bufferSize;

    private MessageDigestHashGenerator(
            final String algorithm, final int bufferSize) {
        
        this.algorithm = algorithm;
        this.bufferSize = bufferSize;
    }

    private MessageDigestHashGenerator(final String algorithm) {
        this(algorithm, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * If the info format requires hash,
     * this method returns the info generator using
     * an instance of this class with the given algorithm.
     * Otherwise it just returns the info generator.
     */
    public static InfoGenerator with(
            final String hashAlgorithm,
            final InfoGenerator infoGenerator,
            final InfoFormat infoFormat) {
        
        requireNonNull(hashAlgorithm, "hashAlgorithm");
        requireNonNull(infoGenerator, "infoGenerator");
        requireNonNull(infoFormat, "infoFormat");
        if (infoFormat.requiresHash()) {
            return infoGenerator.use(
                    new MessageDigestHashGenerator(hashAlgorithm));
        }
        return infoGenerator;
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
