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
 * This class extracts, from the properties file, four parts for the config:
 * {@code infoGenerator},
 * {@code infoFormat},
 * {@code fileFilter} and
 * {@code order}.
 * This is done according to the {@code prefilter} setting:
 * If the setting is turned on,
 * the fields referenced by {@code output} and {@code order}
 * will have a combined info generator,
 * but each filter will have its own.
 * Otherwise, all fields referenced by all settings
 * will share one info generator.
 * 
 * @author Tone Sommerland
 */
public final class Manifold {
    
    private static final int BUFFER_SIZE = 2048;
    private static final int REGEX_FLAGS = 0;

    private final Function<File, Info> infoGenerator;
    private final TokenInfoFormat infoFormat;
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
                    this.infoFormat,
                    orderParser);
            this.fileFilter = nullOrAsOne(getPrefilterFileFilters(
                    filterParsers, availableFields));
        } else {
            this.infoGenerator = getNonPrefilterInfoGenerator(
                    availableFields,
                    this.infoFormat,
                    orderParser,
                    filterParsers);
            this.fileFilter = nullOrAsOne(getNonPrefilterFileFilters(
                    filterParsers, this.infoGenerator));
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
        
        final String[] used1 = infoFormat.getUsedFieldNames(
                availableFields.getNames());
        final String[] used2 = orderParser.getUsedFieldNames();
        return getInfoGenerator(availableFields, concatDistinct(used1, used2));
    }
    
    private static Function<File, Info> getNonPrefilterInfoGenerator(
            final AvailableFields availableFields,
            final TokenInfoFormat infoFormat,
            final OrderParser orderParser,
            final Collection<FilterParser> filterParsers) {
        
        final String[] availableFieldNames = availableFields.getNames();
        final String[] used1 = infoFormat.getUsedFieldNames(
                availableFieldNames);
        final String[] used2 = orderParser.getUsedFieldNames();
        final String[] used3 = filterParsers.stream()
                .map(parser -> parser.getUsedFieldNames(availableFieldNames))
                .flatMap(Stream::of)
                .toArray(String[]::new);
        return getInfoGenerator(
                availableFields, concatDistinct(used1, used2, used3));
    }
    
    private static Collection<FileFilter> getPrefilterFileFilters(
            final Collection<FilterParser> filterParsers,
            final AvailableFields availableFields) throws IOException {
        
        final var fileFilters = new ArrayList<FileFilter>(filterParsers.size());
        for (final var filterParser : filterParsers) {
            fileFilters.add(
                    getPrefilterFileFilter(availableFields, filterParser));
        }
        return fileFilters;
    }
    
    private static Collection<FileFilter> getNonPrefilterFileFilters(
            final Collection<FilterParser> filterParsers,
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
            final AvailableFields availableFields,
            final FilterParser filterParser) throws IOException {
        
        final String[] usedFieldNames = filterParser.getUsedFieldNames(
                availableFields.getNames());
        return filterParser.getFileFilter(
                REGEX_FLAGS, getInfoGenerator(availableFields, usedFieldNames));
    }
    
    private static Function<File, Info> getInfoGenerator(
            final AvailableFields availableFields,
            final String[] usedFieldNames) {
        
        final FieldReader reader = availableFields.getReader(
                BUFFER_SIZE, usedFieldNames);
        final var cache = new HashMap<File, Info>();
        return file -> CachedInfo.getInstance(reader, file, cache);
    }
    
    private static FileFilter nullOrAsOne(
            final Collection<FileFilter> fileFilters) {
        
        if (fileFilters.isEmpty()) {
            return null;
        }
        return file -> fileFilters.stream()
                .filter(ff -> !ff.accept(file))
                .findFirst()
                .map(ff -> false)
                .orElse(true);
    }
    
    private static String[] concatDistinct(
            final String[] array1, final String[] array2) {
        
        return Stream.concat(
                Stream.of(array1),
                Stream.of(array2))
                .distinct()
                .toArray(String[]::new);
    }
    
    private static String[] concatDistinct(
            final String[] array1,
            final String[] array2,
            final String[] array3) {
        
        return Stream.concat(
                Stream.of(array1),
                Stream.concat(
                        Stream.of(array2),
                        Stream.of(array3)))
                .distinct()
                .toArray(String[]::new);
    }

}
