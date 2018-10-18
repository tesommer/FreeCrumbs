package freecrumbs.finf.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

import freecrumbs.finf.FieldReader;
import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormat;

/**
 * This class extracts, from the properties file, four parts of the config:
 * {@code infoFormat},
 * {@code infoGenerator},
 * {@code fileFilter} and
 * {@code order}.
 * This is done according to the {@code prefilter} config-setting;
 * if the setting is turned on,
 * the fields referenced by the {@code output} and {@code order} settings
 * will have a combined field reader and info cache,
 * while each filter will have its own field reader and info cache.
 * Otherwise all fields referenced in the config
 * will share field reader/info cache.
 * 
 * @author Tone Sommerland
 */
public final class Manifold {
    
    private static final int BUFFER_SIZE = 2048;
    private static final int REGEX_FLAGS = 0;

    private final TokenInfoFormat infoFormat;
    private final Function<File, Info> infoGenerator;
    private final FileFilter fileFilter;
    private final Comparator<Info> order;

    public Manifold(final Properties props, final Locale locale)
            throws IOException {
        
        this.infoFormat = Settings.getOutput(props);
        final AvailableFields availableFields
            = Settings.getAvailableFields(props, locale);
        final OrderParser orderParser
            = Settings.getOrderParser(props, availableFields.getNames());
        this.order = orderParser.getOrder();
        final Collection<FilterParser> filterParsers
            = Settings.getFilterParsers(props);
        if (Settings.isPrefilter(props)) {
            this.infoGenerator = getPrefilterInfoGenerator(
                    availableFields,
                    infoFormat,
                    orderParser);
            this.fileFilter = nullOrAsOne(getPrefilterFileFilters(
                    filterParsers, availableFields));
        } else {
            this.infoGenerator = getNonPrefilterInfoGenerator(
                    availableFields,
                    infoFormat,
                    orderParser,
                    filterParsers);
            this.fileFilter = nullOrAsOne(getNonPrefilterFileFilters(
                    filterParsers, infoGenerator));
        }
    }
    
    public Function<File, Info> getInfoGenerator() {
        return infoGenerator;
    }
    
    public InfoFormat getInfoFormat() {
        return infoFormat;
    }
    
    public FileFilter getFileFilter() {
        return fileFilter;
    }
    
    public Comparator<Info> getOrder() {
        return order;
    }
    
    private static Function<File, Info> getPrefilterInfoGenerator(
            final AvailableFields availableFields,
            final TokenInfoFormat infoFormat,
            final OrderParser orderParser) {
        
        final String[] usedByOutput = infoFormat.getUsedFieldNames(
                availableFields.getNames());
        final String[] usedByOrder = orderParser.getUsedFieldNames();
        final FieldReader reader = availableFields.getReader(
                BUFFER_SIZE,
                concat(usedByOutput, usedByOrder).distinctToArray());
        final var cache = new HashMap<File, Info>();
        return file -> CachedInfo.getInstance(reader, file, cache);
    }
    
    private static Function<File, Info> getNonPrefilterInfoGenerator(
            final AvailableFields availableFields,
            final TokenInfoFormat infoFormat,
            final OrderParser orderParser,
            final Collection<FilterParser> filterParsers) {
        
        final String[] availableFieldNames = availableFields.getNames();
        final String[] usedByOutput = infoFormat.getUsedFieldNames(
                availableFieldNames);
        final String[] usedByOrder = orderParser.getUsedFieldNames();
        final String[] usedByFilters = filterParsers.stream()
                .map(parser -> parser.getUsedFieldNames(availableFieldNames))
                .flatMap(Stream::of)
                .toArray(String[]::new);
        final FieldReader reader = availableFields.getReader(
                BUFFER_SIZE,
                concat(usedByOutput, usedByOrder).concat(usedByFilters)
                    .distinctToArray());
        final var cache = new HashMap<File, Info>();
        return file -> CachedInfo.getInstance(reader, file, cache);
    }
    
    private static Collection<FileFilter> getPrefilterFileFilters(
            final Collection<? extends FilterParser> filterParsers,
            final AvailableFields availableFields)
                    throws IOException {
        
        final var fileFilters = new ArrayList<FileFilter>(filterParsers.size());
        for (final var filterParser : filterParsers) {
            fileFilters.add(
                    getPrefilterFileFilter(filterParser, availableFields));
        }
        return fileFilters;
    }
    
    private static Collection<FileFilter> getNonPrefilterFileFilters(
            final Collection<? extends FilterParser> filterParsers,
            final Function<? super File, ? extends Info> infoGenerator)
                    throws IOException {
        
        final var fileFilters = new ArrayList<FileFilter>(filterParsers.size());
        for (final var filterParser : filterParsers) {
            fileFilters.add(
                    filterParser.getFileFilter(REGEX_FLAGS, infoGenerator));
        }
        return fileFilters;
    }
    
    private static FileFilter getPrefilterFileFilter(
            final FilterParser filterParser,
            final AvailableFields availableFields) throws IOException {
        
        final var cache = new HashMap<File, Info>();
        final String[] usedFieldNames = filterParser.getUsedFieldNames(
                availableFields.getNames());
        final FieldReader reader = availableFields.getReader(
                BUFFER_SIZE, usedFieldNames);
        final Function<File, Info> infoGenerator
            = file -> CachedInfo.getInstance(reader, file, cache);
        return filterParser.getFileFilter(REGEX_FLAGS, infoGenerator);
    }
    
    private static FileFilter nullOrAsOne(
            final Collection<? extends FileFilter> fileFilters) {
        
        if (fileFilters.isEmpty()) {
            return null;
        }
        return file -> {
            return fileFilters.stream()
                .filter(ff -> !ff.accept(file))
                .findFirst()
                .map(ff -> false)
                .orElse(true);
        };
    }
    
    private static Concatenator concat(String[] array1, String[] array2) {
        return new Concatenator(
                Stream.concat(Stream.of(array1), Stream.of(array2)));
    }
    
    private static final class Concatenator {
        private final Stream<String> stream;

        private Concatenator(final Stream<String> stream) {
            assert stream != null;
            this.stream = stream;
        }
        
        private Concatenator concat(final String[] arr) {
            return new Concatenator(Stream.concat(stream, Stream.of(arr)));
        }
        
        private String[] distinctToArray() {
            return stream.distinct().toArray(String[]::new);
        }
        
    }

}
