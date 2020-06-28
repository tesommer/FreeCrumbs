package freecrumbs.finf.internal;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import freecrumbs.finf.InfoGenerator;

/**
 * <p>
 * Parses the filter config-setting.
 * Two setting styles are supported: regex and format pattern.
 * </p>
 * <p>
 * Format pattern:
 * </p>
 * <pre>
 * {@code
 * <format-pattern> ::= <format> (("++"|"--") <pattern>)+
 * }
 * </pre>
 * 
 * @see freecrumbs.finf.internal.PropertiesConfigLoader
 * 
 * @author Tone Sommerland
 */
public final class FilterParser
{
    private static final String
    DELIM_PATTERN = "(\\+\\+([^+]|$))|(\\-\\-([^-]|$))";
    
    private static final String INCLUDE_DELIM = "++";
    
    private static final int DELIM_LENGTH = 2;

    private final Collection<FormatPattern> formatPatterns = new ArrayList<>();
    private final String setting;
    private final TokenInfoFormat infoFormat;
    
    /**
     * Parses the filter setting.
     * @param setting the filter setting (nullable)
     */
    public FilterParser(final String setting) throws IOException
    {
        this.setting = setting;
        if (setting == null)
        {
            this.infoFormat = null;
        }
        else
        {
            final Matcher delimiter
                = Pattern.compile(DELIM_PATTERN).matcher(setting);
            final Part formatPart = formatPart(setting, delimiter);
            if (formatPart == null)
            {
                this.infoFormat = null;
            }
            else
            {
                this.infoFormat = new TokenInfoFormat(formatPart.payload);
                initFormatPatterns(setting, delimiter, formatPart);
            }
        }
    }

    private void initFormatPatterns(
            final String setting,
            final Matcher delimiter,
            final Part formatPart) throws IOException
    {
        Part part = formatPart;
        do
        {
            part = nextPatternPart(setting, delimiter, part);
            formatPatterns.add(formatPattern(part));
        }
        while (!part.last);
    }
    
    /**
     * Returns the field names used by the filter setting.
     */
    public String[] usedFieldNames(final String[] availableFieldNames)
    {
        return infoFormat == null
                ? new String[0]
                : infoFormat.usedFieldNames(availableFieldNames);
    }
    
    /**
     * Returns the file filter.
     * @return null if the setting is null
     */
    public FileFilter fileFilter(
            final int regexFlags,
            final InfoGenerator infoGenerator) throws IOException
    {
        if (setting == null)
        {
            return null;
        }
        else if (infoFormat == null)
        {
            return new RegexFileFilter(setting, regexFlags);
        }
        else
        {
            return new FormatPatternFileFilter(
                    infoGenerator,
                    infoFormat,
                    formatPatterns.stream().toArray(FormatPattern[]::new));
        }
    }
    
    private static FormatPattern formatPattern(final Part patternPart)
            throws IOException
    {
        try
        {
            final Pattern pattern = Pattern.compile(patternPart.payload);
            return new FormatPattern(pattern, isInclude(patternPart));
        }
        catch (final PatternSyntaxException ex)
        {
            throw new IOException(ex);
        }
    }
    
    /**
     * Returns null if the setting is not a format pattern.
     */
    private static Part formatPart(
            final String setting, final Matcher delimiter)
    {
        if (delimiter.find())
        {
            return new Part(
                    0, "", setting.substring(0, delimiter.start()), false);
        }
        return null;
    }
    
    private static Part nextPatternPart(
            final String setting,
            final Matcher delimiter,
            final Part previous)
    {
        final int start = startOfNextPart(previous);
        final int payloadStart = start + DELIM_LENGTH;
        final String prefix = setting.substring(start, payloadStart);
        final String payload;
        final boolean last;
        if (delimiter.find(payloadStart))
        {
            payload = setting.substring(payloadStart, delimiter.start());
            last = false;
        }
        else
        {
            payload = setting.substring(payloadStart);
            last = true;
        }
        return new Part(start, prefix, payload, last);
    }

    private static int startOfNextPart(final Part previous)
    {
        return
                previous.start
                + previous.prefix.length()
                + previous.payload.length();
    }
    
    private static boolean isInclude(final Part part)
    {
        return INCLUDE_DELIM.equals(part.prefix);
    }
    
    /**
     * A part of a setting in the format-pattern style.
     * The part may either be the format part,
     * or any of the pattern parts.
     * - start is the start index of the entire part.
     * - payload is the actual part:
     * an info format for the format part
     * and a regex pattern for a pattern part.
     * - prefix is the include/exclude prefix
     * preceding the payload of a pattern part.
     * The format part has an empty prefix.
     * - last is true for the last pattern part.
     */
    private static final class Part
    {
        final int start;
        final String prefix;
        final String payload;
        final boolean last;
        
        Part(
                final int start,
                final String prefix,
                final String payload,
                final boolean last)
        {
            assert prefix != null;
            assert payload != null;
            this.start = start;
            this.prefix = prefix;
            this.payload = payload;
            this.last = last;
        }
    }

}
