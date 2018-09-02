package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
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
    
    private final InfoFields fields;
    private final File file;

    /**
     * Creates a new file info object.
     * @param fields the fields of info to get
     * @param file the file to get info about
     */
    protected Info(final InfoFields fields, final File file) {
        this.fields = requireNonNull(fields, "fields");
        this.file = requireNonNull(file, "file");
    }
    
    /**
     * The names of the fields this info contains.
     */
    public final String[] getFieldNames() {
        return fields.getNames();
    }
    
    /**
     * Returns the value of the field with the given name.
     * @throws java.util.NoSuchElementException
     * if this info doesn't have the given field
     */
    public final String getValue(final String fieldName) throws IOException {
        return getValue(fields.getField(fieldName), file);
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
     * Retrieves the value of the given field from the given file.
     */
    protected abstract String getValue(InfoField field, File file)
            throws IOException;

}
