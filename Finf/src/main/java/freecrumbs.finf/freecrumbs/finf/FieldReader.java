package freecrumbs.finf;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Reads the values of files' info fields.
 * A file's field values are cached after first retrieval.
 * The info obtained by this reader will contain
 * the values of this reader's fields
 * plus any values obtained by co-caching readers.
 * 
 * @author Tone Sommerland
 */
public final class FieldReader implements InfoGenerator
{
    private final Map<File, Map<String, String>> cache;
    private final byte[] buffer;
    private final Field[] fields;

    private FieldReader(
            final Map<File, Map<String, String>> cache,
            final int bufferSize,
            final Field[] fields)
    {
        assert cache != null;
        if (bufferSize < 1)
        {
            throw new IllegalArgumentException("bufferSize < 1: " + bufferSize);
        }
        this.cache = cache;
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
            final int bufferSize, final Field... fields)
    {
        return new FieldReader(new HashMap<>(), bufferSize, fields);
    }
    
    /**
     * Returns an instance that co-caches values with this instance.
     * @param bufferSize the buffer size used when reading a file's content
     * @param fields the fields to read
     * @throws IllegalArgumentException if the buffer size is zero or negative
     */
    public FieldReader coCaching(final int bufferSize, final Field... fields)
    {
        return new FieldReader(this.cache, bufferSize, fields);
    }
    
    /**
     * The names of this reader's fields.
     */
    public String[] getFieldNames()
    {
        return Field.namesOf(fields);
    }
    
    @Override
    public Info getInfo(final File file) throws IOException
    {
        final Map<String, String> values
            = cache.computeIfAbsent(file, key -> new HashMap<>());
        putValues(values, file);
        putComputations(values, file);
        return new Info(values);
    }
    
    private void putValues(
            final Map<? super String, ? super String> values,
            final File file) throws IOException
    {
        for (final Field field : nonCached(values))
        {
            if (!field.isComputed())
            {
                values.put(field.name(), field.value().get(file));
            }
        }
    }
    
    private void putComputations(
            final Map<? super String, ? super String> values,
            final File file) throws IOException
    {
        final Collection<Field> notAborted = resetAbort(values, file);
        final Collection<FieldComputation> compsToUpdate = computationsIn(
                notAborted);
        if (compsToUpdate.isEmpty())
        {
            return;
        }
        update(compsToUpdate, buffer, file);
        compute(notAborted, values);
    }
    
    private Collection<Field> resetAbort(
            final Map<? super String, ? super String> values,
            final File file) throws IOException
    {
        final Collection<Field> notCachedBeforeReset = nonCached(values);
        reset(computationsIn(notCachedBeforeReset), file);
        final Collection<Field> notCachedAfterReset = nonCached(values);
        abort(notCachedBeforeReset, notCachedAfterReset, file);
        return notCachedAfterReset;
    }
    
    private Collection<Field> nonCached(
            final Map<? super String, ? super String> values)
    {
        return Stream.of(fields)
                .filter(field -> !values.containsKey(field.name()))
                .collect(toList());
    }
    
    private static Collection<FieldComputation> computationsIn(
            final Collection<Field> fields)
    {
        return fields.stream()
                .filter(Field::isComputed)
                .map(Field::computation)
                .collect(toList());
    }

    private static void reset(
            final Collection<? extends FieldComputation> computations,
            final File file) throws IOException
    {
        for (final var computation : computations)
        {
            computation.reset(file);
        }
    }
    
    private static void abort(
            final Collection<Field> notCachedBeforeReset,
            final Collection<Field> notCachedAfterReset,
            final File file)
    {
        notCachedBeforeReset.stream()
                .filter(field -> !notCachedAfterReset.contains(field))
                .map(Field::computation)
                .forEach(computation -> computation.abort(file));
    }

    private static void update(
            final Collection<? extends FieldComputation> computations,
            final byte[] buffer,
            final File file) throws IOException
    {
        try (final var in = new FileInputStream(file))
        {
            final var active = new ArrayList<FieldComputation>(computations);
            for (
                    int bytesRead = in.read(buffer);
                    bytesRead > 0;
                    bytesRead = in.read(buffer))
            {
                for (int i = 0; i < active.size(); i++)
                {
                    if (!active.get(i).update(buffer, 0, bytesRead))
                    {
                        active.remove(i--);
                        if (active.isEmpty())
                        {
                            return;
                        }
                    }
                }
            }
        }
    }

    private static void compute(
            final Collection<Field> fields,
            final Map<? super String, ? super String> values)
                    throws IOException
    {
        for (final var field : fields)
        {
            if (field.isComputed())
            {
                values.put(field.name(), field.computation().get());
            }
        }
    }

}
