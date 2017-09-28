package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freecrumbs.finf.HashGenerator;

public final class FileFilterParser {
    
    private static final String DELIM_PATTERN = "(\\+\\+|\\-\\-)([^+-]|$)";
    private static final String INCLUDE_DELIM = "++";
    private static final int DELIM_LENGTH = 2;
    
    private final String dateFormat;
    private final Locale locale;
    private final String hashAlgorithm;
    private final int regexFlags;
    
    public FileFilterParser(
            final String dateFormat,
            final Locale locale,
            final String hashAlgorithm,
            final int regexFlags) {
        
        this.dateFormat = requireNonNull(dateFormat, "dateFormat");
        this.locale = requireNonNull(locale, "locale");
        this.hashAlgorithm = requireNonNull(hashAlgorithm, "hashAlgorithm");
        this.regexFlags = regexFlags;
    }

    public FileFilter parse(final String setting) throws IOException {
        final Collection<FormatPattern> formatPatterns = new ArrayList<>();
        TokenInfoFormat infoFormat = null;
        HashGenerator hashGenerator = null;
        int start = 0;
        final Matcher matcher = Pattern.compile(DELIM_PATTERN).matcher(setting);
        while (matcher.find(start)) {
            final String segment;
            if (infoFormat == null) {
                segment = setting.substring(start, matcher.start());
                infoFormat = new TokenInfoFormat(segment, dateFormat, locale);
                hashGenerator = getHashGenerator(infoFormat);
            } else {
                segment = setting.substring(
                        start - DELIM_LENGTH, matcher.start());
                formatPatterns.add(getFormatPattern(hashGenerator, segment));
            }
            start = matcher.start() + DELIM_LENGTH;
        }
        if (start == 0) {
            return new RegexFileFilter(setting, regexFlags);
        }
        formatPatterns.add(getFormatPattern(
                hashGenerator, setting.substring(start -  DELIM_LENGTH)));
        return new FormatPatternFileFilter(
                infoFormat,
                formatPatterns.stream().toArray(FormatPattern[]::new));
    }
    
    private FormatPattern getFormatPattern(
            final HashGenerator hashGenerator, final String patternSegment) {
        
        final boolean include = patternSegment.startsWith(INCLUDE_DELIM);
        final String regex = patternSegment.substring(DELIM_LENGTH);
        final Pattern pattern = Pattern.compile(regex);
        return new FormatPattern(
                hashGenerator, pattern, include);
    }
    
    private HashGenerator getHashGenerator(final TokenInfoFormat infoFormat) {
        if (infoFormat.containsHash()) {
            return new MessageDigestHashGenerator(hashAlgorithm);
        }
        return HashGenerator.DUMMY;
    }

}
