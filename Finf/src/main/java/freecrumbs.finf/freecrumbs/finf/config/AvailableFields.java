package freecrumbs.finf.config;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldReader;
import freecrumbs.finf.field.BinaryToText;
import freecrumbs.finf.field.Classification;
import freecrumbs.finf.field.Command;
import freecrumbs.finf.field.Eol;
import freecrumbs.finf.field.Filename;
import freecrumbs.finf.field.Hash;
import freecrumbs.finf.field.Modified;
import freecrumbs.finf.field.Path;
import freecrumbs.finf.field.Search;
import freecrumbs.finf.field.Size;
import freecrumbs.finf.field.Whitespace;

/**
 * Contains available info fields of which
 * {@link #readerOf(String...) readers} can be created.
 * No two fields in an instance of this class have the same name.
 * Each instance of this class has its own instances of the computed fields.
 * This is so that readers from instance A
 * will not interfere with readers from instance B.
 * 
 * @author Tone Sommerland
 */
public final class AvailableFields
{
    private static final int BUFFER_SIZE = 2048;
    
    /**
     * Field parameters.
     * 
     * @author Tone Sommerland
     */
    public static final class Params
    {
        private final String dateFormat;
        private final Locale locale;
        private final Classification.Heuristic classHeuristic;
        private final String[] hashAlgorithms;
        private final Search.Params[] searchParams;
        private final Command.Params[] commandParams;
        
        private Params(
                final String dateFormat,
                final Locale locale,
                final Classification.Heuristic classHeuristic,
                final String[] hashAlgorithms,
                final Search.Params[] searchParams,
                final Command.Params[] commandParams)
        {
            this.dateFormat = dateFormat;
            this.locale = locale;
            this.classHeuristic = classHeuristic;
            this.hashAlgorithms
                = hashAlgorithms == null ? new String[0] : hashAlgorithms;
            this.searchParams
                = searchParams == null ? new Search.Params[0] : searchParams;
            this.commandParams
                = commandParams == null ? new Command.Params[0] : commandParams;
        }
        
        public Params()
        {
            this(null, null, null, null, null, null);
        }
        
        /**
         * If the given date format is empty, timestamp formatting will be off.
         */
        public Params withTime(final String dateFormat, final Locale locale)
        {
            return new Params(
                    requireNonNull(dateFormat, "dateFormat"),
                    requireNonNull(locale, "locale"),
                    this.classHeuristic,
                    this.hashAlgorithms,
                    this.searchParams,
                    this.commandParams);
        }
        
        public Params withClassification(
                final Classification.Heuristic heuristic)
        {
            return new Params(
                    this.dateFormat,
                    this.locale,
                    requireNonNull(heuristic, "heuristic"),
                    this.hashAlgorithms,
                    this.searchParams,
                    this.commandParams);
        }
        
        /**
         * Algorithms will be trimmed.
         * Empty algorithms and duplicates will be ignored.
         * Field names will be the algorithms in lowercase.
         */
        public Params withHash(final String... algorithms)
        {
            return new Params(
                    this.dateFormat,
                    this.locale,
                    this.classHeuristic,
                    algorithms.clone(),
                    this.searchParams,
                    this.commandParams);
        }
        
        public Params withAnotherSearch(final Search.Params params)
        {
            return new Params(
                    this.dateFormat,
                    this.locale,
                    this.classHeuristic,
                    this.hashAlgorithms,
                    Stream.concat(
                            Stream.of(this.searchParams), Stream.of(params))
                        .toArray(Search.Params[]::new),
                    this.commandParams);
        }
        
        public Params withAnotherCommand(final Command.Params params)
        {
            return new Params(
                    this.dateFormat,
                    this.locale,
                    this.classHeuristic,
                    this.hashAlgorithms,
                    this.searchParams,
                    Stream.concat(
                            Stream.of(this.commandParams), Stream.of(params))
                        .toArray(Command.Params[]::new));
        }
        
        private Field[] freshFields() throws IOException
        {
            final var freshFields = new ArrayList<Field>(
                    List.of(Path.FIELD, Filename.FIELD, Size.FIELD));
            freshFields.addAll(List.of(Eol.fields()));
            freshFields.addAll(List.of(Whitespace.fields()));
            freshFields.addAll(List.of(BinaryToText.fields()));
            if (dateFormat != null)
            {
                freshFields.addAll(timeFields(dateFormat, locale));
            }
            if (classHeuristic != null)
            {
                freshFields.add(classificationField(classHeuristic));
            }
            if (hashAlgorithms.length > 0)
            {
                freshFields.addAll(hashFields(hashAlgorithms));
            }
            if (searchParams.length > 0)
            {
                freshFields.addAll(searchFields(searchParams));
            }
            if (commandParams.length > 0)
            {
                freshFields.addAll(commandFields(commandParams));
            }
            return freshFields.stream()
                    .filter(distinctByName())
                    .toArray(Field[]::new);
        }
        
        private static Predicate<Field> distinctByName()
        {
            final var names = new HashSet<String>();
            return field -> names.add(field.name());
        }
        
        private static Collection<Field> timeFields(
                final String dateFormat,
                final Locale locale) throws IOException
        {
            final Field modifiedField = dateFormat.isEmpty()
                    ? Modified.field()
                    : Modified.field(dateFormat, locale);
            return List.of(modifiedField);
        }
        
        private static Field classificationField(
                final Classification.Heuristic heuristic)
        {
            return Classification.field(heuristic, String::valueOf);
        }
        
        private static Collection<Field> hashFields(final String[] algorithms)
        {
            final var hashFields = new ArrayList<Field>();
            for (final String algorithm : algorithms)
            {
                final String trimmed = algorithm.trim();
                if (trimmed.isEmpty())
                {
                    continue;
                }
                final String trimmedAndLowerCase = trimmed.toLowerCase();
                hashFields.add(Hash.field(trimmedAndLowerCase, trimmed));
            }
            return hashFields;
        }
        
        private static Collection<Field> searchFields(
                final Search.Params[] params)
        {
            return Stream.of(params)
                    .map(Search::fields)
                    .flatMap(Stream::of)
                    .collect(toList());
        }
        
        private static Collection<Field> commandFields(
                final Command.Params[] params)
        {
            return Stream.of(params)
                    .map(Command::fields)
                    .flatMap(Stream::of)
                    .collect(toList());
        }
    }
    
    private final FieldReader mother;
    private final Params params;
    private final Field[] fields;
    
    private AvailableFields(
            final FieldReader mother,
            final Params params) throws IOException
    {
        assert mother != null;
        this.mother = mother;
        this.params = params;
        this.fields = params.freshFields();
    }
    
    /**
     * Creates an instance with its own cache.
     * @param params field parameters
     */
    public AvailableFields(final Params params) throws IOException
    {
        this(FieldReader.of(2), params);
    }
    
    /**
     * The parameters for this instance's fields.
     */
    public Params params()
    {
        return params;
    }
    
    /**
     * Returns an instance that co-caches with this.
     * @param params field parameters
     */
    public AvailableFields coCaching(final Params params)
            throws IOException
    {
        return new AvailableFields(this.mother, params);
    }

    /**
     * The names of all available fields.
     */
    public String[] names()
    {
        return Field.namesOf(fields);
    }
    
    /**
     * Returns a reader of only the fields that are actually used.
     * Duplicate field names are ignored.
     * @throws NoSuchElementException
     * if any of the specified fields are unavailable
     */
    public FieldReader readerOf(final String... usedFieldNames)
    {
        return mother.coCaching(
                BUFFER_SIZE,
                Stream.of(usedFieldNames)
                    .distinct()
                    .map(this::field)
                    .toArray(Field[]::new));
    }
    
    private Field field(final String name)
    {
        return Stream.of(fields)
            .filter(field -> field.name().equals(name))
            .findAny()
            .orElseThrow(() -> new NoSuchElementException(name));
    }

}
