package freecrumbs.finf;

/**
 * A dynamically created value.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface DynamicValue extends FieldValue
{
    /**
     * Returns an instance with a fixed value.
     * @param value the value
     */
    public static DynamicValue of(final String value)
    {
        return file -> value;
    }
    
    /**
     * Returns an instance that obtains its value
     * by applying the given info format
     * to info returned by the given info generator.
     */
    public static DynamicValue of(
            final InfoGenerator generator, final InfoFormatter formatter)
    {
        return file -> formatter.stringify(generator.infoAbout(file));
    }

}
