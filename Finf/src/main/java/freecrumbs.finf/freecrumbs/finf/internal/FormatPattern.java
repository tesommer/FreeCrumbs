package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Pattern;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormat;

/**
 * Instances of this class are used by
 * {@link freecrumbs.finf.internal.FormatPatternFileFilter}
 * to include or exclude input files.
 * This class applies a regex pattern to formatted file info
 * and includes or excludes the file depending on matches.
 * 
 * @author Tone Sommerland
 */
public final class FormatPattern {
    private final Pattern pattern;
    private final boolean include;
    
    /**
     * Creates a new format pattern.
     * @param pattern the regex pattern to match against the format
     * @param include whether a match includes or excludes the file
     */
    public FormatPattern(final Pattern pattern, final boolean include) {
        this.pattern = requireNonNull(pattern, "pattern");
        this.include = include;
    }
    
    /**
     * Whether to include or exclude the given file.
     */
    public boolean includes(
            final File file,
            final Function<? super File, ? extends Info> infoGenerator,
            final InfoFormat infoFormat) throws IOException {
        
        final Info info = infoGenerator.apply(file);
        return pattern.matcher(infoFormat.toString(info)).matches() == include;
    }
    
}