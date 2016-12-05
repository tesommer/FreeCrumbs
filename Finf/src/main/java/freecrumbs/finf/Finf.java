package freecrumbs.finf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.calclipse.lib.util.EncodingUtil;
import com.calclipse.lib.util.IOUtil;

/**
 * Utility methods.
 * 
 * @author Tone Sommerland
 */
public final class Finf {

    private Finf() {
    }
    
    /**
     * Outputs a list of file info units.
     * @param infoList the file info units
     * @param config configuration
     * @param out the output destination
     */
    public static void output(
            final List<? extends Info> infoList,
            final Config config,
            final PrintStream out) {
        
        final List<? extends Info> sorted = new ArrayList<>(infoList);
        config.getOrder().ifPresent(sorted::sort);
        for (int i = 0; (config.getCount() < 0 || i < config.getCount())
                && i < sorted.size(); i++) {
            out.println(config.getInfoFormat().toString(sorted.get(i)));
        }
    }
    
    /**
     * Gets the file info for a file.
     * @param file the file to get info of
     * @param config configuration
     */
    public static Info getInfo(final File file, final Config config)
            throws IOException {
        
        final String path;
        final String filename;
        final int lastSep = file.getPath().lastIndexOf(File.separator);
        if (lastSep < 0) {
            path = "";
            filename = file.getName();
        } else {
            path = file.getPath().substring(0, lastSep + 1);
            filename = file.getPath().substring(lastSep + 1);
        }
        final String hash = getHash(file, config);
        return new Info(
                path, filename, file.length(), file.lastModified(), hash);
    }
    
    private static String getHash(final File file, final Config config)
            throws IOException {

        try (
            final InputStream in = new FileInputStream(file);
        ) {
            final MessageDigest msgDigest = config.getMessageDigest();
            msgDigest.update(IOUtil.readAll(in));
            return EncodingUtil.bytesToHex(false, msgDigest.digest());
        }
    }
    
    /**
     * Whether or not the file filter of the given configuration
     * accepts the file specified.
     */
    public static boolean acceptsInput(final File file, final Config config) {
        return config.getFileFilter().map(ff -> ff.accept(file)).orElse(true);
    }

}
