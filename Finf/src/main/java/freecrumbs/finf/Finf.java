package freecrumbs.finf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

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
        
        final List<? extends Info> ordered;
        if (config.getOrder().isPresent()) {
            ordered = infoList.stream()
                    .sorted(config.getOrder().get())
                    .collect(Collectors.toList());
        } else {
            ordered = infoList;
        }
        outputOrdered(ordered, config, out);
    }

    private static void outputOrdered(
            final List<? extends Info> infoList,
            final Config config,
            final PrintStream out) {
        
        for (int i = 0; (config.getCount() < 0 || i < config.getCount())
                && i < infoList.size(); i++) {
            out.println(config.getInfoFormat().toString(infoList.get(i)));
        }
    }
    
    /**
     * Gets the file info for a file.
     * @param config configuration
     * @param file the file to get info of
     */
    public static Info getInfo(final Config config, final File file)
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
        final String hash = getHash(config, file);
        return new Info(
                path, filename, file.length(), file.lastModified(), hash);
    }
    
    private static String getHash(final Config config, final File file)
            throws IOException {

        if (config.isHashUnused()) {
            return "";
        }
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
     * accepts the given file.
     */
    public static boolean acceptsInput(final Config config, final File file) {
        return config.getFileFilter().map(ff -> ff.accept(file)).orElse(true);
    }

}
