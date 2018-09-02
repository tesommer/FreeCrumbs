package freecrumbs.finf;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * A group of file-info fields.
 * 
 * @author Tone Sommerland
 */
public final class InfoFields {
    private final InfoField[] fields;

    private InfoFields(final InfoField[] fields) {
        this.fields = fields.clone();
    }
    
    public static InfoFields of(final InfoField... fields) {
        return new InfoFields(fields);
    }
    
    /**
     * The names of the fields.
     */
    public String[] getNames() {
        return Stream.of(fields)
            .map(InfoField::getName)
            .toArray(String[]::new);
    }
    
    /**
     * Returns the field with the given name.
     * @param name the field name
     * @throws NoSuchElementException
     * if this instance doesn't have the field with the given name
     */
    public InfoField getField(final String name) {
        return Stream.of(fields)
                .filter(field -> field.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(name));
    }

}
