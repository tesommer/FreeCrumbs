package freecrumbs.finf.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
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
 * The fields will have distinct names.
 * 
 * @author Tone Sommerland
 */
public final class AvailableFields {
    private final Field[] fields;
    
    private AvailableFields(final Field[] initial, final Field... more) {
        this.fields = Stream.concat(Stream.of(initial), Stream.of(more))
                .filter(distinctByName())
                .toArray(Field[]::new);
    }
    
    private static Predicate<Field> distinctByName() {
        final var names = new HashSet<String>();
        return field -> names.add(field.name());
    }
    
    /**
     * Returns an instance containing initial fields:
     * path, filename, size and EOL-fields.
     */
    public static AvailableFields getInitial() {
        final var initial = new Field[] {
                Path.FIELD,
                Filename.FIELD,
                Size.FIELD,
        };
        return new AvailableFields(initial, Eol.getFields());
    }
    
    /**
     * If the given date format is empty, timestamp formatting will be off.
     */
    public AvailableFields plusTimeFields(
            final String dateFormat, final Locale locale) throws IOException {
        
        final Field modifiedField = dateFormat.isEmpty()
                ? Modified.getField()
                : Modified.getField(dateFormat, locale);
        return new AvailableFields(this.fields, modifiedField);
    }
    
    public AvailableFields plusClassification(
            final Classification.Heuristic heuristic) {
        
        return new AvailableFields(
                this.fields,
                Classification.getField(heuristic, String::valueOf));
    }
    
    public AvailableFields plusHashFields(final String... algorithms) {
        return new AvailableFields(
                this.fields,
                hashFields(algorithms).stream().toArray(Field[]::new));
    }
    
    private static Collection<Field> hashFields(final String[] algorithms) {
        final var hashFields = new ArrayList<Field>();
        for (final String algorithm : algorithms) {
            final String trimmed = algorithm.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            final String trimmedAndLowerCase = trimmed.toLowerCase();
            hashFields.add(Hash.getField(trimmedAndLowerCase, trimmed));
        }
        return hashFields;
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
