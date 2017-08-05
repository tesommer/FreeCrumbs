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
public class ScriptFile implements Location {
    private final String base;

    public ScriptFile(final String base) {
        this.base = requireNonNull(base, "base");
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public Location refer(final String target) throws MacroException {
        final int index = base.lastIndexOf(File.separator);
        if (index >= 0) {
            final File relative = new File(base.substring(0, index), target);
            if (relative.isFile()) {
                return new ScriptFile(relative.getPath());
            }
        }
        return new ScriptFile(target);
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
