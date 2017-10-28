package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;

import freecrumbs.finf.InfoField;

/**
 * The filename.
 * 
 * @author Tone Sommerland
 */
public final class FilenameField extends AbstractInfoField {
    
    public static final InfoField INSTANCE = new FilenameField("filename");

    private FilenameField(final String name) {
        super(name);
    }

    @Override
    public String getValue(final File file) throws IOException {
        return file.getName();
    }

}
