package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * A file-info field.
 * 
 * @author Tone Sommerland
 */
public final class Field
{
    private final String name;
    private final FieldValue value;
    private final FieldComputation computation;
    
    private Field(
            final String name,
            final FieldValue value,
            final FieldComputation computation)
    {
        this.name = requireNonNull(name, "name");
        if (value == null)
        {
            this.value = null;
            this.computation = requireNonNull(computation, "computation");
        }
        else
        {
            this.value = value;
            this.computation = null;
        }
    }
    
    /**
     * Returns an instance with the given name and value.
     * @param name the name
     * @param value the value
     */
    public static Field simple(final String name, final FieldValue value)
    {
        return new Field(name, value, null);
    }
    
    /**
     * Returns an instance with a computed value.
     * @param name the field name
     * @param computation the computation
     */
    public static Field computed(
            final String name, final FieldComputation computation)
    {
        return new Field(name, null, computation);
    }
    
    /**
     * Returns the names of the given fields.
     */
    public static String[] namesOf(final Field... fields)
    {
        return Stream.of(fields).map(Field::name).toArray(String[]::new);
    }
    
    /**
     * The name of this field.
     */
    public String name()
    {
        return name;
    }
    
    /**
     * Whether or not this field contains a simple value or a computation.
     */
    public boolean isComputed()
    {
        return computation != null;
    }
    
    /**
     * This field's value.
     * @throws NoSuchElementException
     * if {@link #isComputed()} returns {@code true}
     */
    public FieldValue value()
    {
        if (value == null)
        {
            throw new NoSuchElementException("value");
        }
        return value;
    }
    
    /**
     * This field's value computation.
     * @throws NoSuchElementException
     * if {@link #isComputed()} returns {@code false}
     */
    public FieldComputation computation()
    {
        if (computation == null)
        {
            throw new NoSuchElementException("computation");
        }
        return computation;
    }
}
