package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A location that points to content in memory.
 * 
 * @author Tone Sommerland
 */
public class MockLocation implements Location {
    private final String content;

    public MockLocation(final String content) {
        this.content = requireNonNull(content, "content");
    }

    @Override
    public String getBase() {
        return content;
    }

    @Override
    public Location refer(final String target) throws MacroException {
        return new MockLocation(target);
    }

    @Override
    public InputStream open() throws MacroException {
        return new ByteArrayInputStream(
                content.getBytes(Charset.forName("UTF-8")));
    }

}
