package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.calclipse.lib.util.EncodingUtil;

import freecrumbs.finf.InfoField;

public final class HashField extends AbstractInfoField {
    
    private static final String NAME = "hash";
    
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    
    private final String algorithm;
    private final int bufferSize;
    
    private HashField(
            final String name, final String algorithm, final int bufferSize) {
        
        super(name);
        this.algorithm = algorithm;
        this.bufferSize = bufferSize;
    }
    
    public static InfoField getInstance(final String algorithm) {
        return new HashField(
                NAME,
                requireNonNull(algorithm, "algorithm"),
                DEFAULT_BUFFER_SIZE);
    }

    @Override
    public String getValue(final File file) throws IOException {
        try (final InputStream in = new FileInputStream(file)) {
            return EncodingUtil.bytesToHex(
                    false, digest(MessageDigest.getInstance(algorithm), in));
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
