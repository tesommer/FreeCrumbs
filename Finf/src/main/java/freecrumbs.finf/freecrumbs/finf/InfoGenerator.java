package freecrumbs.finf;

import java.io.File;
import java.io.IOException;

/**
 * Generates file info.
 * 
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface InfoGenerator
{
    public abstract Info infoAbout(final File file) throws IOException;

}
