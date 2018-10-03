package test.freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldValue;

public final class MockFieldValue implements FieldValue {
    private final String value;
    private boolean valueRead;

    public MockFieldValue(final String value) {
        this.value = requireNonNull(value, "value");
    }
    
    public static boolean isValueRead(final Field field) {
        if (field.isComputed() || !(field.value() instanceof MockFieldValue)) {
            return false;
        }
        return ((MockFieldValue)field.value()).valueRead;
    }

    @Override
    public String get(final File file) throws IOException {
        valueRead = true;
        return value;
    }

}
