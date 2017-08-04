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
    private final String file;

    public ScriptFile(final String file) {
        this.file = requireNonNull(file, "file");
    }

    @Override
    public String getBase() {
        return file;
    }

    @Override
    public ScriptLocation refer(final String relative) throws MacroException {
        final int index = file.lastIndexOf(File.separator);
        if (index >= 0) {
            final File relativeFile
                = new File(file.substring(0, index), relative);
            if (relativeFile.isFile()) {
                return new ScriptFile(relativeFile.getPath());
            }
        }
        return new ScriptFile(relative);
    }

    @Override
    public Script open(final MacroLoader loader) throws MacroException {
        try (final InputStream in = new FileInputStream(file)) {
            return new Script(loader, this, loader.load(in));
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
    }

}
