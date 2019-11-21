package test.freecrumbs.finf;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import freecrumbs.finf.Field;

/**
 * Utility class for field testing.
 * 
 * @author Tone Sommerland
 */
public final class FieldTesting
{
    public static final File DUMMY_FILE = new File("dummy");

    private FieldTesting()
    {
    }
    
    /**
     * Returns the field with the specified name.
     * @throws NoSuchElementException
     * if the fields array doesn't contain the field
     */
    public static Field getField(final String name, final Field... fields)
    {
        return Stream.of(fields)
                .filter(field -> field.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(name));
    }

    /**
     * Resets field computations.
     */
    public static void resetComputations(final Field... fields)
            throws IOException
    {
        for (final Field field : fields)
        {
            if (field.isComputed())
            {
                field.computation().reset(DUMMY_FILE);
            }
        }
    }

    /**
     * Updates field computations with input from the given content.
     */
    public static void updateComputations(
            final byte[] content,
            final int bufferSize,
            final Field... fields) throws IOException
    {
        final var active = new ArrayList<Field>(List.of(fields));
        final var buffer = new byte[bufferSize];
        for (int offset = 0; ;)
        {
            final int length = Math.min(content.length - offset, bufferSize);
            if (length <= 0)
            {
                break;
            }
            System.arraycopy(content, offset, buffer, 0, length);
            for (int i = 0; i < active.size(); i++)
            {
                final Field field = active.get(i);
                if (field.isComputed()
                        && !field.computation().update(buffer, 0, length))
                {
                    active.remove(i--);
                }
            }
            offset += length;
        }
    }
    
    public static void assertFieldValues(
            final Map<String, String> expecteds,
            final Field[] actuals) throws IOException
    {
        assertEquals(
                expecteds.keySet().stream()
                    .sorted()
                    .collect(toList()),
                Stream.of(actuals)
                    .map(Field::name)
                    .sorted()
                    .collect(toList()),
                "Field names");
        for (final String key : expecteds.keySet())
        {
            final Field field = getField(key, actuals);
            assertEquals(
                    expecteds.get(key),
                    field.isComputed()
                        ? field.computation().get()
                        : field.value().get(DUMMY_FILE),
                    "Field '" + key + "'");
        }
    }

}
