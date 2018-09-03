package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;

import freecrumbs.finf.InfoField;

/**
 * The path without the filename.
 * The path is empty if the file is in the root directory.
 * Otherwise it ends with a file separator.
 * The name of this field is {@code "path"}.
 * 
 * @author Tone Sommerland
 */
public final class PathField extends AbstractInfoField {
    
    public static final InfoField INSTANCE = new PathField("path");

    private PathField(final String name) {
        super(name);
    }

    @Override
    public String getValue(final File file) throws IOException {
        final int index = file.getPath().lastIndexOf(File.separatorChar);
        if (index < 0) {
            return "";
        }
        return file.getPath().substring(0, index + 1);
    }

}
