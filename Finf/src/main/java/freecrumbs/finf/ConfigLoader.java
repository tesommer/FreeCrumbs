package freecrumbs.finf;

import java.io.IOException;
import java.io.Reader;

/**
 * Configuration loader.
 *
 * @author Tone Sommerland
 */
public interface ConfigLoader {

    public abstract Config loadConfig(Reader reader) throws IOException;
}
