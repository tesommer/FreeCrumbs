package freecrumbs.finf.field;

import freecrumbs.finf.FieldValue;
import freecrumbs.finf.InfoFormat;
import freecrumbs.finf.InfoGenerator;

/**
 * A dynamically created value.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface DynamicValue extends FieldValue {
    
    /**
     * Returns an instance with a fixed value.
     * @param value the value
     */
    public static DynamicValue of(final String value) {
        return file -> value;
    }
    
    /**
     * Returns an instance that obtains its value
     * by applying the given info format
     * to info returned by the given info generator.
     */
    public static DynamicValue of(
            final InfoGenerator infoGenerator, final InfoFormat infoFormat) {
        
        return file -> infoFormat.toString(infoGenerator.getInfo(file));
    }

}
