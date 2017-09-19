package freecrumbs.finf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.calclipse.lib.util.EncodingUtil;

/**
 * Generates and prints
 * {@link freecrumbs.finf.Info file info}.
 * 
 * @author Tone Sommerland
 */
public final class Finf {

    private Finf() {
    }
    
    /**
     * Generates and prints file info.
     * @param files the input files
     * @param config the configuration
     * @param out the output destination
     */
    public static void output(
            final Collection<? extends File> files,
            final Config config,
            final PrintStream out) throws IOException {
        
        final List<Info> infoList = getInfo(files, config);
        final List<Info> ordered;
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

    private static List<Info> getInfo(
            final Collection<? extends File> files,
            final Config config) throws IOException {
        
        final List<Info> infoList = new ArrayList<>();
        for (final File file : files) {
            if (acceptsInput(file, config)) {
                infoList.add(getInfo(file, config));
            }
        }
        return infoList;
    }
    
    private static Info getInfo(final File file, final Config config)
            throws IOException {
        
        final String path;
        final String filename;
        final int index = file.getPath().lastIndexOf(File.separatorChar);
        if (index < 0) {
            path = "";
            filename = file.getName();
        } else {
            path = file.getPath().substring(0, index + 1);
            filename = file.getPath().substring(index + 1);
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
            return EncodingUtil.bytesToHex(
                    false, config.getHashGenerator().digest(in));
        }
    }
    
    private static boolean acceptsInput(final File file, final Config config) {
        return config.getFileFilter().map(ff -> ff.accept(file)).orElse(true);
    }

}
