package test.freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import freecrumbs.macro.Location;
import freecrumbs.macro.MacroException;

/**
 * A location that points to script content in memory.
 * 
 * @author Tone Sommerland
 */
public final class MockLocation implements Location {
    
    public static final MockLocation DUMMY = new MockLocation("");
    
    private final String scriptContent;

    public MockLocation(final String scriptContent) {
        this.scriptContent = requireNonNull(scriptContent, "scriptContent");
    }

    @Override
    public Location refer(final String target) throws MacroException {
        return new MockLocation(target);
    }

    @Override
    public InputStream open() throws MacroException {
        return new ByteArrayInputStream(
                scriptContent.getBytes(Charset.forName("UTF-8")));
    }

}
