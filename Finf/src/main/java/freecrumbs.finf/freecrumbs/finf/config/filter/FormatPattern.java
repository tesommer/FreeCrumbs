package freecrumbs.finf.config.filter;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import freecrumbs.finf.Info;
import freecrumbs.finf.InfoFormat;
import freecrumbs.finf.InfoGenerator;

/**
 * Instances of this class are used by
 * {@link freecrumbs.finf.config.filter.FormatPatternFileFilter}
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
            final InfoGenerator infoGenerator,
            final InfoFormat infoFormat) throws IOException
    {
        final Info info = infoGenerator.infoAbout(file);
        return pattern.matcher(infoFormat.stringify(info)).matches() == include;
    }
    
}
