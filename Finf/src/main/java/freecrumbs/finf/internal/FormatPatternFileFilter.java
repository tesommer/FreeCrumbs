package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Logger;

import freecrumbs.finf.InfoFormat;

/**
 * This file filter includes or excludes files based on
 * regex patterns applied to formatted info for each file.
 * 
 * @author Tone Sommerland
 */
public class FormatPatternFileFilter implements FileFilter {
    
    private static final Logger
    LOGGER = Logger.getLogger(FormatPatternFileFilter.class.getName());
    
    private final InfoFormat infoFormat;
    private final FormatPattern[] formatPatterns;

    /**
     * Creates a new format pattern file filter.
     * @param infoFormat the format to apply to each file's info
     * @param formatPatterns the inclusion or exclusion patterns
     */
    public FormatPatternFileFilter(
            final InfoFormat infoFormat,
            final FormatPattern... formatPatterns) {
        
        this.infoFormat = requireNonNull(infoFormat, "infoFormat");
        this.formatPatterns = formatPatterns.clone();
    }

    @Override
    public boolean accept(final File pathname) {
        try {
            return includes(pathname);
        } catch (final IOException ex) {
            LOGGER.warning(ex.toString());
            return false;
        }
    }

    private boolean includes(final File file) throws IOException {
        for (final FormatPattern formatPattern : formatPatterns) {
            if (!formatPattern.includes(file, infoFormat)) {
                return false;
            }
        }
        return true;
    }

}
