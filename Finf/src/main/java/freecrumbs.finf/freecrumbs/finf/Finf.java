package freecrumbs.finf;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generates and prints
 * {@link freecrumbs.finf.Info file info}.
 * 
 * @author Tone Sommerland
 */
public final class Finf
{
    private Finf()
    {
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
            final PrintStream out) throws IOException
    {
        if (config.getOrder().isPresent())
        {
            outputOrdered(files, config, out);
        }
        else
        {
            outputUnordered(files, config, out);
        }
    }

    private static void outputOrdered(
            final Collection<? extends File> files,
            final Config config,
            final PrintStream out) throws IOException
    {
        final List<Info> items = filterAndSort(files, config);
        output(items, config, out, info -> info);
    }

    private static void outputUnordered(
            final Collection<? extends File> files,
            final Config config,
            final PrintStream out) throws IOException
    {
        final List<File> items = filter(files, config);
        output(
                items,
                config,
                out,
                config.getInfoGenerator()::getInfo);
    }
    
    private static List<Info> filterAndSort(
            final Collection<? extends File> files,
            final Config config) throws IOException
    {
        final var items = new ArrayList<Info>();
        for (final File file : filter(files, config))
        {
            items.add(config.getInfoGenerator().getInfo(file));
        }
        return items.stream().sorted(config.getOrder().get()).collect(toList());
    }

    private static List<File> filter(
            final Collection<? extends File> files,
            final Config config)
    {
        return files.stream()
                .filter(file -> acceptsInput(file, config))
                .collect(toList());
    }
    
    @FunctionalInterface
    private interface Informer<T>
    {
        public abstract Info provide(T item) throws IOException;
    }

    private static <T> void output(
            final List<T> items,
            final Config config,
            final PrintStream out,
            final Informer<? super T> informer) throws IOException
    {
        for (int i = 0; (config.getCount() < 0 || i < config.getCount())
                && i < items.size(); i++)
        {
            final Info info = informer.provide(items.get(i));
            out.print(config.getInfoFormat().toString(info));
        }
    }
    
    private static boolean acceptsInput(final File file, final Config config)
    {
        return config.getFileFilter().map(ff -> ff.accept(file)).orElse(true);
    }

}
