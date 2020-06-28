package test.freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoGenerator;

/**
 * A mock info generator.
 * 
 * @author Tone Sommerland
 */
public class MockInfoGenerator implements InfoGenerator
{
    private static final String PATH_FIELD_NAME = "path";
    private static final String FILENAME_FIELD_NAME = "filename";
    private static final String SIZE_FIELD_NAME = "size";
    private static final String MODIFIED_FIELD_NAME = "modified";
    private static final String MD5_FIELD_NAME = "md5";
    
    private final Info info;

    /**
     * Creates an info generator that "generates" the given info.
     */
    public MockInfoGenerator(final Info info)
    {
        this.info = requireNonNull(info, "info");
    }
    
    /**
     * Returns an info with the given field values.
     */
    public static Info getInfo(
            final String path,
            final String filename,
            final String size,
            final String modified,
            final String md5)
    {
        return new Info(Map.of(
                PATH_FIELD_NAME,     path,
                FILENAME_FIELD_NAME, filename,
                SIZE_FIELD_NAME,     size,
                MODIFIED_FIELD_NAME, modified,
                MD5_FIELD_NAME,      md5));
    }

    @Override
    public Info infoAbout(final File file) throws IOException
    {
        return info;
    }

}
