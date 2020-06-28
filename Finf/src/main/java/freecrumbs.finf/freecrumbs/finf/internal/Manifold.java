package freecrumbs.finf.internal;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormat;
import freecrumbs.finf.InfoGenerator;

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
 * Otherwise, all fields referenced by these settings
 * will share one info generator.
 * 
 * @author Tone Sommerland
 */
public final class Manifold
{
    private static final int REGEX_FLAGS = 0;

    private final InfoGenerator infoGenerator;
    private final TokenInfoFormat infoFormat;
    private final FileFilter fileFilter;
    private final Comparator<Info> order;

    public Manifold(final Properties props, final Locale locale)
            throws IOException
    {
        this.infoFormat = Settings.output(props);
        final AvailableFields availableFields = Settings.availableFields(
                props, locale);
        final OrderParser orderParser = Settings.orderParser(
                props, availableFields.names());
        this.order = orderParser.order();
        final Collection<FilterParser> filterParsers = Settings.filterParsers(
                props);
        if (Settings.isPrefilter(props))
        {
            this.infoGenerator = prefilterInfoGenerator(
                    availableFields,
                    this.infoFormat,
                    orderParser);
            this.fileFilter = nullOrAsOne(prefilterFileFilters(
                    filterParsers, availableFields));
        }
        else
        {
            this.infoGenerator = nonPrefilterInfoGenerator(
                    availableFields,
                    this.infoFormat,
                    orderParser,
                    filterParsers);
            this.fileFilter = nullOrAsOne(nonPrefilterFileFilters(
                    filterParsers, this.infoGenerator));
        }
    }
    
    public InfoGenerator infoGenerator()
    {
        return infoGenerator;
    }
    
    public InfoFormat infoFormat()
    {
        return infoFormat;
    }
    
    public FileFilter fileFilter()
    {
        return fileFilter;
    }
    
    public Comparator<Info> order()
    {
        return order;
    }
    
    private static InfoGenerator prefilterInfoGenerator(
            final AvailableFields availableFields,
            final TokenInfoFormat infoFormat,
            final OrderParser orderParser)
    {
        final String[] used1 = infoFormat.usedFieldNames(
                availableFields.names());
        final String[] used2 = orderParser.usedFieldNames();
        return infoGenerator(availableFields, concat(used1, used2));
    }
    
    private static InfoGenerator nonPrefilterInfoGenerator(
            final AvailableFields availableFields,
            final TokenInfoFormat infoFormat,
            final OrderParser orderParser,
            final Collection<FilterParser> filterParsers)
    {
        final String[] availableFieldNames = availableFields.names();
        final String[] used1 = infoFormat.usedFieldNames(
                availableFieldNames);
        final String[] used2 = orderParser.usedFieldNames();
        final String[] used3 = filterParsers.stream()
                .map(parser -> parser.usedFieldNames(availableFieldNames))
                .flatMap(Stream::of)
                .toArray(String[]::new);
        return infoGenerator(availableFields, concat(used1, used2, used3));
    }
    
    private static Collection<FileFilter> prefilterFileFilters(
            final Collection<FilterParser> filterParsers,
            final AvailableFields availableFields) throws IOException
    {
        final var fileFilters = new ArrayList<FileFilter>(filterParsers.size());
        for (final var filterParser : filterParsers)
        {
            fileFilters.add(
                    prefilterFileFilter(availableFields, filterParser));
        }
        return fileFilters;
    }
    
    private static Collection<FileFilter> nonPrefilterFileFilters(
            final Collection<FilterParser> filterParsers,
            final InfoGenerator infoGenerator) throws IOException
    {
        final var fileFilters = new ArrayList<FileFilter>(filterParsers.size());
        for (final var filterParser : filterParsers)
        {
            fileFilters.add(
                    filterParser.fileFilter(REGEX_FLAGS, infoGenerator));
        }
        return fileFilters;
    }
    
    private static FileFilter prefilterFileFilter(
            final AvailableFields availableFields,
            final FilterParser filterParser) throws IOException
    {
        final String[] usedFieldNames = filterParser.usedFieldNames(
                availableFields.names());
        return filterParser.fileFilter(
                REGEX_FLAGS, infoGenerator(availableFields, usedFieldNames));
    }
    
    private static InfoGenerator infoGenerator(
            final AvailableFields availableFields,
            final String[] usedFieldNames)
    {
        return availableFields.reader(usedFieldNames);
    }
    
    private static FileFilter nullOrAsOne(
            final Collection<FileFilter> fileFilters)
    {
        if (fileFilters.isEmpty())
        {
            return null;
        }
        return file -> !fileFilters.stream().anyMatch(ff -> !ff.accept(file));
    }
    
    private static String[] concat(
            final String[] array1, final String[] array2)
    {
        return Stream.concat(
                Stream.of(array1),
                Stream.of(array2))
                .toArray(String[]::new);
    }
    
    private static String[] concat(
            final String[] array1,
            final String[] array2,
            final String[] array3)
    {
        return Stream.concat(
                Stream.of(array1),
                Stream.concat(
                        Stream.of(array2),
                        Stream.of(array3)))
                .toArray(String[]::new);
    }

}
