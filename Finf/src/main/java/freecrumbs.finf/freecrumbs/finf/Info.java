package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * File info.
 * 
 * @author Tone Sommerland
 */
public abstract class Info {
    
    private static final Logger LOGGER = Logger.getLogger(Info.class.getName());
    
    private final FieldReader reader;
    private final File file;

    /**
     * Creates a new file info object.
     * @param fields the fields of info to acquire
     * @param file the file to get info about
     */
    protected Info(final FieldReader reader, final File file) {
        this.reader = requireNonNull(reader, "reader");
        this.file = requireNonNull(file, "file");
    }
    
    /**
     * The names of the fields this info contains.
     */
    public final String[] getFieldNames() {
        return reader.getFieldNames();
    }
    
    /**
     * Returns the value of the field with the given name.
     * @throws java.util.NoSuchElementException
     * if this info doesn't have the given field
     */
    public final String getValue(final String fieldName) throws IOException {
        requireNonNull(fieldName, "fieldName");
        final Map<String, String> values = getValues(reader, file);
        if (!values.containsKey(fieldName)) {
            new NoSuchElementException(fieldName);
        }
        return values.get(fieldName);
    }
    
    /**
     * Compares the value of a field in this info
     * with the value of the same field in another info.
     * @return empty if the field is nonexistent in any of the info objects
     */
    public final Optional<Integer> compare(
            final String fieldName, final Info other) {
        
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
     * Uses the given field reader to get the field values of the given file.
     */
    protected abstract Map<String, String> getValues(
            FieldReader reader, File file) throws IOException;

}
