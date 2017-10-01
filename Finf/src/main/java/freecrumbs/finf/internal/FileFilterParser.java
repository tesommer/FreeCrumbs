package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import freecrumbs.finf.InfoFormat;
import freecrumbs.finf.InfoGenerator;

/**
 * <p>
 * Parses the file filter config setting.
 * Two setting styles are supported: regex and format pattern.
 * </p>
 * <p>
 * Format pattern:
 * </p>
 * <pre>
 * {@code
 * &lt;format-pattern&gt; ::= &lt;format&gt; (("++"|"--") &lt;pattern&gt;)+}
 * }
 * </pre>
 * 
 * @see freecrumbs.finf.internal.PropertiesConfigLoader
 * 
 * @author Tone Sommerland
 */
public class FileFilterParser {
    
    private static final String
    DELIM_PATTERN = "(\\+\\+([^+]|$))|(\\-\\-([^-]|$))";
    
    private static final String INCLUDE_DELIM = "++";
    
    private static final int DELIM_LENGTH = 2;

    private final String hashAlgorithm;
    private final String dateFormat;
    private final Locale locale;
    private final int regexFlags;
    private final InfoGenerator infoGenerator;
    
    public FileFilterParser(
            final String hashAlgorithm,
            final String dateFormat,
            final Locale locale,
            final int regexFlags,
            final InfoGenerator infoGenerator) {

        this.hashAlgorithm = requireNonNull(hashAlgorithm, "hashAlgorithm");
        this.dateFormat = requireNonNull(dateFormat, "dateFormat");
        this.locale = requireNonNull(locale, "locale");
        this.regexFlags = regexFlags;
        this.infoGenerator = requireNonNull(infoGenerator, "infoGenerator");
    }

    public FileFilter parse(final String setting) throws IOException {
        final Matcher delimiter
            = Pattern.compile(DELIM_PATTERN).matcher(setting);
        final Part formatPart = getFormatPart(setting, delimiter);
        if (formatPart == null) {
            return new RegexFileFilter(setting, regexFlags);
        }
        return parseFormatPattern(setting, delimiter, formatPart);
    }

    private FileFilter parseFormatPattern(
            final String setting,
            final Matcher delimiter,
            final Part formatPart) throws IOException {
        
        final InfoFormat infoFormat = getInfoFormat(formatPart);
        final InfoGenerator optimizedInfoGenerator
            = optimizeInfoGenerator(infoFormat);
        final Collection<FormatPattern> formatPatterns = new ArrayList<>();
        Part part = formatPart;
        do {
            part = getNextPatternPart(setting, delimiter, part);
            formatPatterns.add(getFormatPattern(part, optimizedInfoGenerator));
        } while (!part.last);
        return new FormatPatternFileFilter(
                infoFormat,
                formatPatterns.stream().toArray(FormatPattern[]::new));
    }

    private InfoFormat getInfoFormat(final Part formatPart) throws IOException {
        return new TokenInfoFormat(formatPart.payload, dateFormat, locale);
    }

    private InfoGenerator optimizeInfoGenerator(final InfoFormat infoFormat) {
        return MessageDigestHashGenerator.with(
                hashAlgorithm, infoGenerator, infoFormat);
    }
    
    private static FormatPattern getFormatPattern(
            final Part patternPart,
            final InfoGenerator infoGenerator) throws IOException {
        
        try {
            final Pattern pattern = Pattern.compile(patternPart.payload);
            return new FormatPattern(
                    pattern, isInclude(patternPart), infoGenerator);
        } catch (final PatternSyntaxException ex) {
            throw new IOException(ex);
        }
    }
    
    /**
     * Returns null if the setting is not a format pattern.
     */
    private static Part getFormatPart(
            final String setting, final Matcher delimiter) {
        
        if (delimiter.find()) {
            return new Part(
                    0, "", setting.substring(0, delimiter.start()), false);
        }
        return null;
    }
    
    private static Part getNextPatternPart(
            final String setting,
            final Matcher delimiter,
            final Part previous) {
        
        final int start = startOfNextPart(previous);
        final int payloadStart = start + DELIM_LENGTH;
        final String prefix = setting.substring(start, payloadStart);
        final String payload;
        final boolean last;
        if (delimiter.find(payloadStart)) {
            payload = setting.substring(payloadStart, delimiter.start());
            last = false;
        } else {
            payload = setting.substring(payloadStart);
            last = true;
        }
        return new Part(start, prefix, payload, last);
    }

    private static int startOfNextPart(final Part previous) {
        return
                previous.start
                + previous.prefix.length()
                + previous.payload.length();
    }
    
    private static boolean isInclude(final Part part) {
        return INCLUDE_DELIM.equals(part.prefix);
    }
    
    /**
     * A part of a setting in the format pattern style.
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
    private static final class Part {
        final int start;
        final String prefix;
        final String payload;
        final boolean last;
        
        public Part(
                final int start,
                final String prefix,
                final String payload,
                final boolean last) {
            
            this.start = start;
            this.prefix = prefix;
            this.payload = payload;
            this.last = last;
        }
    }

}
