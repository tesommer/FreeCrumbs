package freecrumbs.finf.config.filter;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Logger;

import freecrumbs.finf.InfoFormatter;
import freecrumbs.finf.InfoGenerator;

/**
 * This file filter includes or excludes files based on
 * regex patterns applied to formatted info for each file.
 * 
 * @author Tone Sommerland
 */
public final class FormatPatternFilter implements FileFilter
{
    private static final Logger
    LOGGER = Logger.getLogger(FormatPatternFilter.class.getName());

    private final InfoGenerator generator;
    private final InfoFormatter formatter;
    private final FormatPattern[] formatPatterns;

    /**
     * Creates a new format pattern file filter.
     * @param generator the info generator to use
     * @param formatter the format to apply to each file's info
     * @param formatPatterns the inclusion or exclusion patterns
     */
    public FormatPatternFilter(
            final InfoGenerator generator,
            final InfoFormatter formatter,
            final FormatPattern... formatPatterns)
    {
        this.generator = requireNonNull(generator, "generator");
        this.formatter = requireNonNull(formatter, "formatter");
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
            if (!formatPattern.includes(file, generator, formatter))
            {
                return false;
            }
        }
        return true;
    }

}
