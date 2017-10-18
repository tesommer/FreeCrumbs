package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;

import freecrumbs.finf.InfoField;

public final class SizeField extends AbstractInfoField {
    
    public static final InfoField INSTANCE = new SizeField("size");

    private SizeField(final String name) {
        super(name);
    }

    @Override
    public String getValue(final File file) throws IOException {
        return String.valueOf(file.length());
    }

}
