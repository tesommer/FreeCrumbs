package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A script location referencing a file in the file system.
 * 
 * @author Tone Sommerland
 */
public class ScriptFile implements ScriptLocation {
    private final String base;

    public ScriptFile(final String base) {
        this.base = requireNonNull(base, "base");
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public ScriptLocation refer(final String relative) throws MacroException {
        final int index = base.lastIndexOf(File.separator);
        if (index >= 0) {
            final File relativeFile
                = new File(base.substring(0, index), relative);
            if (relativeFile.isFile()) {
                return new ScriptFile(relativeFile.getPath());
            }
        }
        return new ScriptFile(relative);
    }

    @Override
    public InputStream open() throws MacroException {
        try {
            return new FileInputStream(base);
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
    }

}
