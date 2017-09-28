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
        
        if (config.getOrder().isPresent()) {
            final List<Info> items = filterAndSort(files, config);
            output(items, info -> info, config, out);
        } else {
            final List<File> items = filter(files, config);
            output(
                    items,
                    file -> getInfo(file, config.getHashGenerator()),
                    config,
                    out);
        }
    }
    
    /**
     * Returns the file info of a single file.
     * @throws IOException if the hash generator does.
     */
    public static Info getInfo(
            final File file,
            final HashGenerator hashGenerator) throws IOException {
        
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
        final String hash = getHash(file, hashGenerator);
        return new Info(
                path, filename, file.length(), file.lastModified(), hash);
    }
    
    private static List<Info> filterAndSort(
            final Collection<? extends File> files,
            final Config config) throws IOException {
        
        final List<Info> items = new ArrayList<>(files.size());
        for (final File file : files) {
            if (acceptsInput(file, config)) {
                items.add(getInfo(file, config.getHashGenerator()));
            }
        }
        return items.stream()
                .sorted(config.getOrder().get())
                .collect(Collectors.toList());
    }

    private static List<File> filter(
            final Collection<? extends File> files, final Config config) {
        
        return files.stream()
                .filter(file -> acceptsInput(file, config))
                .collect(Collectors.toList());
    }
    
    @FunctionalInterface
    private interface Informer<T> {
        public abstract Info provide(T item) throws IOException;
    }

    private static <T> void output(
            final List<T> items,
            final Informer<? super T> informer,
            final Config config,
            final PrintStream out) throws IOException {
        
        for (int i = 0; (config.getCount() < 0 || i < config.getCount())
                && i < items.size(); i++) {
            final Info info = informer.provide(items.get(i));
            out.println(config.getInfoFormat().toString(info));
        }
    }
    
    private static String getHash(
            final File file,
            final HashGenerator hashGenerator) throws IOException {
        
        try (
            final InputStream in = new FileInputStream(file);
        ) {
            return EncodingUtil.bytesToHex(false, hashGenerator.digest(in));
        }
    }
    
    private static boolean acceptsInput(final File file, final Config config) {
        return config.getFileFilter().map(ff -> ff.accept(file)).orElse(true);
    }

}
