package freecrumbs.finf;

import java.io.IOException;

/**
 * File info format.
 *
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface InfoFormat {

    public abstract String toString(Info info) throws IOException;
}
