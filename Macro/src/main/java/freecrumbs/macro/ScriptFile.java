package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A script location referencing a file in the file system.
 * This class uses forward slash (/) as file separator.
 * 
 * @author Tone Sommerland
 */
public class ScriptFile implements Location {
    
    private static final char SEPARATOR = '/';
    
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
        final int index = base.lastIndexOf(SEPARATOR);
        if (index >= 0) {
            final File relative = new File(
                    systemDependent(base.substring(0, index)),
                    systemDependent(target));
            if (relative.isFile()) {
                return new ScriptFile(
                        systemIndependent(relative.getPath()));
            }
        }
        return new ScriptFile(target);
    }

    @Override
    public InputStream open() throws MacroException {
        try {
            return new FileInputStream(systemDependent(base));
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
    }
    
    private static String systemDependent(final String file) {
        return file.replace(SEPARATOR, File.separatorChar);
    }
    
    private static String systemIndependent(final String file) {
        return file.replace(File.separatorChar, SEPARATOR);
    }

}
