package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * File info.
 * 
 * @author Tone Sommerland
 */
public final class Info
{
    private final Map<String, String> values;

    /**
     * Creates a file info object.
     * @param values field names and their associated values
     */
    public Info(final Map<String, String> values)
    {
        this.values = Map.copyOf(values);
    }
    
    /**
     * The names of the fields this info contains.
     */
    public Set<String> getFieldNames()
    {
        return values.keySet();
    }
    
    /**
     * Returns the value of the field with the given name.
     * @throws java.util.NoSuchElementException
     * if this info doesn't have the given field
     */
    public String getValue(final String fieldName)
    {
        requireNonNull(fieldName, "fieldName");
        if (!values.containsKey(fieldName))
        {
            throw new NoSuchElementException(fieldName);
        }
        return values.get(fieldName);
    }
    
    /**
     * Compares the value of a field in this info
     * with the value of the same field in another info.
     * @return empty if the field is nonexistent in any of the info objects
     */
    public Optional<Integer> compare(final String fieldName, final Info other)
    {
        try
        {
            final String value1 = getValue(fieldName);
            final String value2 = other.getValue(fieldName);
            return Optional.of(value1.compareTo(value2));
        }
        catch (final NoSuchElementException ex)
        {
            return Optional.empty();
        }
    }

}
