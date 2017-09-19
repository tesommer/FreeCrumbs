package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import freecrumbs.finf.HashGenerator;

public class MessageDigestHashGenerator implements HashGenerator {
    
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

    @Override
    public byte[] digest(final InputStream in) throws IOException {
        try {
            final MessageDigest msgDigest
                = MessageDigest.getInstance(algorithm);
            final byte[] buffer = new byte[bufferSize];
            int bytesRead = in.read(buffer);
            while (bytesRead > 0) {
                msgDigest.update(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            return msgDigest.digest();
        } catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

}
