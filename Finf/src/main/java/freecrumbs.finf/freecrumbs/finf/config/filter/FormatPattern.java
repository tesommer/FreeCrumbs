package freecrumbs.finf.config.filter;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormatter;
import freecrumbs.finf.InfoGenerator;

/**
 * Instances of this class are used by
 * {@link freecrumbs.finf.config.filter.FormatPatternFilter}
 * to include or exclude input files.
 * This class applies a regex pattern to formatted file info
 * and includes or excludes the file depending on matches.
 * 
 * @author Tone Sommerland
 */
public final class FormatPattern
{
    private final Pattern pattern;
    private final boolean include;
    
    /**
     * Creates a new format pattern.
     * @param pattern the regex pattern to match against the format
     * @param include whether a match includes or excludes the file
     */
    public FormatPattern(final Pattern pattern, final boolean include)
    {
        this.pattern = requireNonNull(pattern, "pattern");
        this.include = include;
    }
    
    /**
     * Whether to include or exclude the given file.
     */
    public boolean includes(
            final File file,
            final InfoGenerator generator,
            final InfoFormatter formatter) throws IOException
    {
        final Info info = generator.infoAbout(file);
        return pattern.matcher(formatter.stringify(info)).matches() == include;
    }
    
}
