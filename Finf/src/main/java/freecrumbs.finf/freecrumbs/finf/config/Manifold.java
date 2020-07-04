package freecrumbs.finf.config;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormatter;
import freecrumbs.finf.InfoGenerator;
import freecrumbs.finf.config.filter.FilterParser;
import freecrumbs.finf.config.order.OrderParser;

/**
 * This class extracts, from the properties file, four parts for the config:
 * {@code generator},
 * {@code formatter},
 * {@code filter} and
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

    private final InfoGenerator generator;
    private final TokenInfoFormatter formatter;
    private final FileFilter filter;
    private final Comparator<Info> order;

    public Manifold(final Properties props, final Locale locale)
            throws IOException
    {
        this.formatter = Settings.output(props);
        final AvailableFields availableFields = Settings.availableFields(
                props, locale);
        final OrderParser orderParser = Settings.orderParser(
                props, availableFields.names());
        this.order = orderParser.orderOrNull();
        final Collection<FilterParser> filterParsers = Settings.filterParsers(
                props);
        if (Settings.isPrefilter(props))
        {
            this.generator = prefilterGenerator(
                    availableFields,
                    this.formatter,
                    orderParser);
            this.filter = nullOrAsOne(prefilterFilters(
                    filterParsers, availableFields));
        }
        else
        {
            this.generator = nonPrefilterGenerator(
                    availableFields,
                    this.formatter,
                    orderParser,
                    filterParsers);
            this.filter = nullOrAsOne(nonPrefilterFilters(
                    filterParsers, this.generator));
        }
    }
    
    public InfoGenerator generator()
    {
        return generator;
    }
    
    public InfoFormatter formatter()
    {
        return formatter;
    }
    
    public FileFilter filterOrNull()
    {
        return filter;
    }
    
    public Comparator<Info> orderOrNull()
    {
        return order;
    }
    
    private static InfoGenerator prefilterGenerator(
            final AvailableFields availableFields,
            final TokenInfoFormatter formatter,
            final OrderParser orderParser)
    {
        final String[] used1 = formatter.usedFieldNames(
                availableFields.names());
        final String[] used2 = orderParser.usedFieldNames();
        return generator(availableFields, concat(used1, used2));
    }
    
    private static InfoGenerator nonPrefilterGenerator(
            final AvailableFields availableFields,
            final TokenInfoFormatter formatter,
            final OrderParser orderParser,
            final Collection<FilterParser> filterParsers)
    {
        final String[] availableFieldNames = availableFields.names();
        final String[] used1 = formatter.usedFieldNames(
                availableFieldNames);
        final String[] used2 = orderParser.usedFieldNames();
        final String[] used3 = filterParsers.stream()
                .map(parser -> parser.usedFieldNames(availableFieldNames))
                .flatMap(Stream::of)
                .toArray(String[]::new);
        return generator(availableFields, concat(used1, used2, used3));
    }
    
    private static Collection<FileFilter> prefilterFilters(
            final Collection<FilterParser> filterParsers,
            final AvailableFields availableFields) throws IOException
    {
        final var filters = new ArrayList<FileFilter>(filterParsers.size());
        for (final var filterParser : filterParsers)
        {
            filters.add(
                    prefilterFilter(filterParser, availableFields));
        }
        return filters;
    }
    
    private static Collection<FileFilter> nonPrefilterFilters(
            final Collection<FilterParser> filterParsers,
            final InfoGenerator generator) throws IOException
    {
        final var filters = new ArrayList<FileFilter>(filterParsers.size());
        for (final var filterParser : filterParsers)
        {
            filters.add(
                    filterParser.filterOrNull(REGEX_FLAGS, generator));
        }
        return filters;
    }
    
    private static FileFilter prefilterFilter(
            final FilterParser filterParser,
            final AvailableFields availableFields) throws IOException
    {
        final String[] usedFieldNames = filterParser.usedFieldNames(
                availableFields.names());
        return filterParser.filterOrNull(
                REGEX_FLAGS, generator(availableFields, usedFieldNames));
    }
    
    private static InfoGenerator generator(
            final AvailableFields availableFields,
            final String[] usedFieldNames)
    {
        return availableFields.reader(usedFieldNames);
    }
    
    private static FileFilter nullOrAsOne(
            final Collection<FileFilter> filters)
    {
        if (filters.isEmpty())
        {
            return null;
        }
        return file -> !filters.stream().anyMatch(ff -> !ff.accept(file));
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
