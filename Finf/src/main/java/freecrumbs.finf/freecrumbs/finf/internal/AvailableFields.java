package freecrumbs.finf.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldReader;
import freecrumbs.finf.field.Classification;
import freecrumbs.finf.field.Eol;
import freecrumbs.finf.field.Filename;
import freecrumbs.finf.field.Hash;
import freecrumbs.finf.field.Modified;
import freecrumbs.finf.field.Path;
import freecrumbs.finf.field.Size;

/**
 * Contains available info fields.
 * 
 * @author Tone Sommerland
 */
public final class AvailableFields {
    private final Field[] fields;

    /**
     * Creates an instance containing available info fields.
     * If the given date format is empty, timestamp formatting will be off.
     */
    public AvailableFields(
            final String dateFormat,
            final Locale locale,
            final Classification.Heuristic classHeuristic,
            final String... hashAlgorithms) throws IOException {
        
        this.fields = concat(
                Stream.of(
                        Path.FIELD,
                        Filename.FIELD,
                        Size.FIELD,
                        modifiedField(dateFormat, locale),
                        Classification.getField(classHeuristic, String::valueOf)
                    ),
                hashFields(hashAlgorithms).stream(),
                Stream.of(Eol.getFields())
            )
            .toArray(Field[]::new);
    }
    
    private static Stream<Field> concat(
            Stream<Field> stream1,
            Stream<Field> stream2,
            Stream<Field> stream3) {
        
        return Stream.concat(
                stream1,
                Stream.concat(
                        stream2,
                        stream3));
    }
    
    private static Field modifiedField(
            final String dateFormat, final Locale locale) throws IOException {
        
        if (dateFormat.isEmpty()) {
            return Modified.getField();
        }
        return Modified.getField(dateFormat, locale);
    }
    
    private static Collection<Field> hashFields(final String[] hashAlgorithms) {
        final var hashFields = new HashMap<String, Field>();
        for (final String algorithm : hashAlgorithms) {
            final String trimmed = algorithm.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            final String trimmedAndLowerCase = trimmed.toLowerCase();
            hashFields.put(
                    trimmedAndLowerCase,
                    Hash.getField(trimmedAndLowerCase, trimmed));
        }
        return hashFields.values();
    }
    
    /**
     * The names of all available fields.
     */
    public String[] getNames() {
        return Field.namesOf(fields);
    }
    
    /**
     * Returns a reader of only the fields that are actually used.
     * @throws NoSuchElementException
     * if any of the specified fields are unavailable
     */
    public FieldReader getReader(
            final int bufferSize, final String... usedFieldNames) {
        
        return FieldReader.getInstance(
                bufferSize,
                Stream.of(usedFieldNames)
                    .map(this::getField)
                    .toArray(Field[]::new));
    }
    
    private Field getField(final String name) {
        return Stream.of(fields)
            .filter(field -> field.name().equals(name))
            .findAny()
            .orElseThrow(() -> new NoSuchElementException(name));
    }

}
