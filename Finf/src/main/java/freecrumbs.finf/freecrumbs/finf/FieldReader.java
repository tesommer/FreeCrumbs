package freecrumbs.finf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Reads the values of files' info fields.
 * 
 * @author Tone Sommerland
 */
public final class FieldReader {
    private final byte[] buffer;
    private final Field[] fields;

    private FieldReader(final int bufferSize, final Field[] fields) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("bufferSize < 1: " + bufferSize);
        }
        this.buffer = new byte[bufferSize];
        this.fields = fields.clone();
    }
    
    /**
     * Returns an instance of this class.
     * @param bufferSize the buffer size used when reading a file's content
     * @param fields the fields to read
     * @throws IllegalArgumentException if the buffer size is zero or negative
     */
    public static FieldReader getInstance(
            final int bufferSize, final Field... fields) {
        
        return new FieldReader(bufferSize, fields);
    }
    
    /**
     * The names of the fields this reader contains.
     */
    public String[] getFieldNames() {
        return Field.namesOf(fields);
    }
    
    /**
     * Reads the field values of the given file.
     * @param file the file to get info about
     * @return the field names associated with their respective values
     */
    public Map<String, String> readFieldValues(final File file)
            throws IOException {
        
        final var values = new HashMap<String, String>();
        putValues(values, file);
        putComputations(values, file);
        return values;
    }
    
    private void putValues(
            final Map<? super String, ? super String> values,
            final File file) throws IOException {
        
        for (final Field field : fields) {
            if (!field.isComputed()) {
                values.put(field.name(), field.value().get(file));
            }
        }
    }
    
    private void putComputations(
            final Map<? super String, ? super String> values,
            final File file) throws IOException {
        
        final FieldComputation[] computations = getComputations();
        if (computations.length == 0) {
            return;
        }
        reset(computations);
        serve(computations, buffer, file);
        compute(fields, values);
    }
    
    private FieldComputation[] getComputations() {
        return Stream.of(fields)
                .filter(Field::isComputed)
                .map(Field::computation)
                .toArray(FieldComputation[]::new);
    }

    private static void reset(final FieldComputation[] computations)
            throws IOException {
        
        for (final var computation : computations) {
            computation.reset();
        }
    }

    private static void serve(
            final FieldComputation[] computations,
            final byte[] buffer,
            final File file) throws IOException {
        
        try (final var in = new FileInputStream(file)) {
            final var active = new ArrayList<FieldComputation>(
                    List.of(computations));
            for (
                    int bytesRead = in.read(buffer);
                    bytesRead > 0;
                    bytesRead = in.read(buffer)) {
                
                for (int i = 0; i < active.size(); i++) {
                    if (!active.get(i).update(buffer, 0, bytesRead)) {
                        active.remove(i--);
                        if (active.isEmpty()) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private static void compute(
            final Field[] fields,
            final Map<? super String, ? super String> values)
                    throws IOException {
        
        for (final var field : fields) {
            if (field.isComputed()) {
                values.put(field.name(), field.computation().get());
            }
        }
    }

}
