package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;

import freecrumbs.finf.InfoField;

public class MockField implements InfoField {
    private final String name;
    private final String value;
    private boolean valueRead;

    public MockField(final String name, final String value) {
        this.name = requireNonNull(name, "name");
        this.value = requireNonNull(value, "value");
    }

    public boolean isValueRead() {
        return valueRead;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue(final File file) throws IOException {
        valueRead = true;
        return value;
    }

}
