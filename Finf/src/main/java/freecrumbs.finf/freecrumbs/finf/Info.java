package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * File info.
 * 
 * @author Tone Sommerland
 */
public abstract class Info {
    
    private static final Logger LOGGER = Logger.getLogger(Info.class.getName());
    
    private final File file;
    private final InfoField[] fields;

    /**
     * Creates a new file info object.
     * @param file the file to get info about
     * @param fields the fields of info to get
     */
    public Info(final File file, final InfoField... fields) {
        this.file = requireNonNull(file, "file");
        this.fields = fields.clone();
    }
    
    /**
     * The names of the fields this info contains.
     */
    public String[] getFieldNames() {
        return Stream.of(fields)
            .map(InfoField::getName)
            .toArray(String[]::new);
    }
    
    /**
     * Returns the value of the field with the given name.
     * @throws java.util.NoSuchElementException
     * if this info doesn't have the given field.
     */
    public String getValue(final String fieldName) throws IOException {
        return getValue(getField(fieldName), file);
    }
    
    /**
     * Compares the value of a field in this info
     * with the value of the same field in another info.
     * @return empty if the field is nonexistent in any of the info objects.
     */
    public Optional<Integer> compare(final String fieldName, final Info other) {
        try {
            final String value1 = getValue(fieldName);
            final String value2 = other.getValue(fieldName);
            return Optional.of(value1.compareTo(value2));
        } catch (final NoSuchElementException ex) {
            return Optional.empty();
        } catch (final IOException ex) {
            LOGGER.warning(ex.toString());
            return Optional.empty();
        }
    }
    
    /**
     * Retrieves the value of the given field from the given file.
     */
    protected abstract String getValue(InfoField field, File file)
            throws IOException;
    
    private InfoField getField(final String name) {
        return Stream.of(fields)
                .filter(field -> field.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(name));
    }

}
