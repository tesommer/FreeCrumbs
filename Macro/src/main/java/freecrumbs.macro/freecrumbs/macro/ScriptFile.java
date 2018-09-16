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

    private ScriptFile(final String base) {
        this.base = requireNonNull(base, "base");
    }
    
    /**
     * Returns a script file referencing a file.
     * @param file platform dependent path to the file.
     */
    public static ScriptFile fromFilePath(final String file) {
        return new ScriptFile(neutral(file));
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
                    dependent(base.substring(0, index)),
                    dependent(target));
            if (relative.isFile()) {
                return new ScriptFile(neutral(relative.getPath()));
            }
        }
        return new ScriptFile(target);
    }

    @Override
    public InputStream open() throws MacroException {
        try {
            return new FileInputStream(dependent(base));
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
    }
    
    private static String dependent(final String file) {
        return file.replace(SEPARATOR, File.separatorChar);
    }
    
    private static String neutral(final String file) {
        return file.replace(File.separatorChar, SEPARATOR);
    }

}
