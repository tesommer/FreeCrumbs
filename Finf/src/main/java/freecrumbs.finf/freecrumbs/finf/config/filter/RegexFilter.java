package freecrumbs.finf.config.filter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A file filter that accepts files with names matching a regular expression.
 * 
 * @author Tone Sommerland
 */
public final class RegexFilter implements FileFilter
{
    private final Pattern pattern;
    
    public RegexFilter(final String regex, final int flags)
        throws IOException
    {
        try
        {
            this.pattern = Pattern.compile(regex, flags);
        }
        catch (final PatternSyntaxException ex)
        {
            throw new IOException(ex);
        }
    }
    
    @Override
    public boolean accept(final File file)
    {
        return pattern.matcher(file.getName()).matches();
    }
}
