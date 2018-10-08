package freecrumbs.finf.internal;

import java.io.IOException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldReader;
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
     */
    public AvailableFields(
            final Locale locale,
            final String dateFormat,
            final String... hashAlgorithms) throws IOException {
        
        this.fields = Stream.concat(
                Stream.of(
                        Path.FIELD,
                        Filename.FIELD,
                        Size.FIELD,
                        Modified.getField(dateFormat, locale)),
                Stream.of(hashAlgorithms)
                    .map(algorithm -> Hash.getField(algorithm, algorithm)))
                .toArray(Field[]::new);
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
