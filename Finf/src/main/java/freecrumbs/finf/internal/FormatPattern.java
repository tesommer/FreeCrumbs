package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import freecrumbs.finf.Finf;
import freecrumbs.finf.HashGenerator;
import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormat;

/**
 * Instances of this class are used by
 * {@link freecrumbs.finf.internal.FormatPatternFileFilter}
 * to include or exclude input files.
 * This class applies a regex pattern to formatted file info
 * and includes or excludes the file depending on whether it matches.
 * 
 * @author Tone Sommerland
 */
public class FormatPattern {
    private final HashGenerator hashGenerator;
    private final Pattern pattern;
    private final boolean include;
    
    /**
     * Creates a new format pattern.
     * @param hashGenerator the hash generator to use
     * @param pattern the regex pattern to match against the format
     * @param include whether a match includes or excludes the file
     */
    public FormatPattern(
            final HashGenerator hashGenerator,
            final Pattern pattern,
            final boolean include) {
        
        this.hashGenerator = requireNonNull(hashGenerator, "hashGenerator");
        this.pattern = requireNonNull(pattern, "pattern");
        this.include = include;
    }
    
    /**
     * Whether to include or exclude the given file.
     */
    public boolean includes(final File file, final InfoFormat infoFormat)
            throws IOException {
        
        final Info info = Finf.getInfo(file, hashGenerator);
        return pattern.matcher(infoFormat.toString(info)).matches() == include;
    }
    
}
