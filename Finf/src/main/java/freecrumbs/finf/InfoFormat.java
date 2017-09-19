package freecrumbs.finf;

/**
 * File info format.
 *
 * @author Tone Sommerland
 */
@FunctionalInterface
public interface InfoFormat {

    public abstract String toString(Info info);
}
