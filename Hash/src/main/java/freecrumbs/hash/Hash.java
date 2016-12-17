package freecrumbs.hash;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Logger;

import com.calclipse.lib.util.EncodingUtil;

/**
 * A simple program that generates file checksums.
 * 
 * @author Tone Sommerland
 */
public final class Hash {
    
    private static final Logger LOGGER = Logger.getLogger(Hash.class.getName());
    
    private static final int BUFFER_SIZE = 2048;
    
    private static final String
    HELP = "@hashhelp@";
    
    private static final String OUTPUT_FORMAT = "{1}: {0}";
    
    private static final String[]
    DEFAULT_ALGORITHMS = {"MD5", "SHA-1", "SHA-256" };
    
    private Hash() {
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            printHelpAndExit();
        }
        final String[] algorithms;
        if (args.length == 1) {
            algorithms = DEFAULT_ALGORITHMS;
        } else {
            algorithms = Arrays.copyOfRange(args, 1, args.length);
        }
        final boolean upperCase = false;
        try (
            final InputStream in = new FileInputStream(args[0]);
        ) {
            byte[][] hashes = getHashes(in, algorithms);
            for (int i = 0; i < algorithms.length; i++) {
                printHash(hashes[i], algorithms[i], upperCase);
            }
        } catch (final IOException ex) {
            LOGGER.warning(ex.toString());
        }
    }
    
    private static byte[][] getHashes(
        final InputStream in,
        final String... algorithms) throws IOException {
            
        final MessageDigest[] mds = new MessageDigest[algorithms.length];
        try {
            for (int i = 0; i < algorithms.length; i++) {
                mds[i] = MessageDigest.getInstance(algorithms[i]);
            }
        } catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
        final byte[] bytes = new byte[BUFFER_SIZE];
        int bytesRead = in.read(bytes);
        while (bytesRead > 0) {
            for (final MessageDigest md : mds) {
                md.update(bytes, 0, bytesRead);
            }
            bytesRead = in.read(bytes);
        }
        final byte[][] hashes = new byte[algorithms.length][];
        for (int i = 0; i < mds.length; i++) {
            hashes[i] = mds[i].digest();
        }
        return hashes;
    }
    
    private static void printHash(
        final byte[] hash, final String algorithm, final boolean upperCase) {
            
        System.out.println(MessageFormat.format(
            OUTPUT_FORMAT,
            EncodingUtil.bytesToHex(upperCase, hash),
            algorithm));
    }
    
    private static void printHelpAndExit() {
        System.out.println(HELP);
        System.exit(0);
    }
}
