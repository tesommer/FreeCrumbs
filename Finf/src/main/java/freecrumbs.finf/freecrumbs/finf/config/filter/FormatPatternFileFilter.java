package freecrumbs.finf.config.filter;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Logger;

import freecrumbs.finf.InfoFormat;
import freecrumbs.finf.InfoGenerator;

/**
 * This file filter includes or excludes files based on
 * regex patterns applied to formatted info for each file.
 * 
 * @author Tone Sommerland
 */
public final class FormatPatternFileFilter implements FileFilter
{
    private static final Logger
    LOGGER = Logger.getLogger(FormatPatternFileFilter.class.getName());

    private final InfoGenerator infoGenerator;
    private final InfoFormat infoFormat;
    private final FormatPattern[] formatPatterns;

    /**
     * Creates a new format pattern file filter.
     * @param infoGenerator the info generator to use
     * @param infoFormat the format to apply to each file's info
     * @param formatPatterns the inclusion or exclusion patterns
     */
    public FormatPatternFileFilter(
            final InfoGenerator infoGenerator,
            final InfoFormat infoFormat,
            final FormatPattern... formatPatterns)
    {
        this.infoGenerator = requireNonNull(infoGenerator, "infoGenerator");
        this.infoFormat = requireNonNull(infoFormat, "infoFormat");
        this.formatPatterns = formatPatterns.clone();
    }

    @Override
    public boolean accept(final File pathname)
    {
        try
        {
            return includes(pathname);
        }
        catch (final IOException ex)
        {
            LOGGER.warning(ex.toString());
            return false;
        }
    }

    private boolean includes(final File file) throws IOException
    {
        for (final FormatPattern formatPattern : formatPatterns)
        {
            if (!formatPattern.includes(file, infoGenerator, infoFormat))
            {
                return false;
            }
        }
        return true;
    }

}
