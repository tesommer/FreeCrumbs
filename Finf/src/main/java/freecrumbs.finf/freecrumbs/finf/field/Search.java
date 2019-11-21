package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

/**
 * <p>
 * Regex search of the file contents.
 * </p>
 * <p>
 * Each search makes the following fields available:
 * <ul>
 * <li>found: 0 or 1</li>
 * <li>groupcount: number of regex groups, excluding group zero</li>
 * <li>line: one-based line number</li>
 * <li>input: the matched input sequence</li>
 * <li>start: zero-based char-index of the start of the matched sequence</li>
 * <li>end: zero-based index of the first char after the matched sequence</li>
 * </ul>
 * </p>
 * 
 * @author Tone Sommerland
 */
public final class Search
{
    private static final String FOUND_FIELD_NAME = "found";
    private static final String GROUP_COUNT_FIELD_NAME = "groupcount";
    private static final String LINE_FIELD_NAME = "line";
    private static final String INPUT_FIELD_NAME = "input";
    private static final String START_FIELD_NAME = "start";
    private static final String END_FIELD_NAME = "end";
    
    private static final String SEPARATOR = "-";
    
    /**
     * Search parameters.
     * 
     * @author Tone Sommerland
     */
    public static final class Params
    {
        private final String fieldNamePrefix;
        private final int groups;
        private final Charset charset;
        private final int occurrence;
        private final int regexFlags;
        private final DynamicValue regex;
        
        private Params(
                final String fieldNamePrefix,
                final int groups,
                final Charset charset,
                final int occurrence,
                final int regexFlags,
                final DynamicValue regex)
        {
            if (groups < 0)
            {
                throw new IllegalArgumentException("groups < 0");
            }
            this.fieldNamePrefix
                = requireNonNull(fieldNamePrefix, "fieldNamePrefix");
            this.groups = groups;
            this.charset = requireNonNull(charset, "charset");
            this.occurrence = occurrence;
            this.regexFlags = regexFlags;
            this.regex = requireNonNull(regex, "regex");
        }
        
        /**
         * Creates parameters with the given regex.
         */
        public Params(final DynamicValue regex)
        {
            this("", 0, Charset.defaultCharset(), 1, 0, regex);
        }
        
        public Params withFieldNamePrefix(final String fieldNamePrefix)
        {
            return new Params(
                    fieldNamePrefix,
                    this.groups,
                    this.charset,
                    this.occurrence,
                    this.regexFlags,
                    this.regex);
        }
        
        /**
         * Sets the number of groups to include.
         * For each group, three fields will be added,
         * prefixed with the group number and a dash:
         * input, start and end.
         * If the groups exceed the group count,
         * excess groups will have empty/-1 as their values.
         * @throws IllegalArgumentException if groups is negative
         */
        public Params withGroups(final int groups)
        {
            return new Params(
                    this.fieldNamePrefix,
                    groups,
                    this.charset,
                    this.occurrence,
                    this.regexFlags,
                    this.regex);
        }
        
        public Params withCharset(final Charset charset)
        {
            return new Params(
                    this.fieldNamePrefix,
                    this.groups,
                    charset,
                    this.occurrence,
                    this.regexFlags,
                    this.regex);
        }
        
        /**
         * The occurrence to search for.
         * A negative occurrence searches from the bottom rather than the top.
         * An occurrence of zero results in not found.
         */
        public Params withOccurrence(final int occurrence)
        {
            return new Params(
                    this.fieldNamePrefix,
                    this.groups,
                    this.charset,
                    occurrence,
                    this.regexFlags,
                    this.regex);
        }
        
        public Params withRegexFlags(final int regexFlags)
        {
            return new Params(
                    this.fieldNamePrefix,
                    this.groups,
                    this.charset,
                    this.occurrence,
                    regexFlags,
                    this.regex);
        }
        
        public Params withRegex(final DynamicValue regex)
        {
            return new Params(
                    this.fieldNamePrefix,
                    this.groups,
                    this.charset,
                    this.occurrence,
                    this.regexFlags,
                    regex);
        }
    }

    private Search()
    {
    }
    
    /**
     * Returns the fields of the search specified with the given parameters.
     */
    public static Field[] getFields(final Params params)
    {
        final var searcher = new Searcher();
        final var fields = new ArrayList<Field>(4 + params.groups);
        fields.add(Field.getInstance(
                params.fieldNamePrefix + FOUND_FIELD_NAME,
                new SearchComputation(params, searcher, Search::getFound)));
        fields.add(Field.getInstance(
                params.fieldNamePrefix + GROUP_COUNT_FIELD_NAME,
                new SearchComputation(params, searcher, Search::getGroupCount)));
        fields.add(Field.getInstance(
                params.fieldNamePrefix + LINE_FIELD_NAME,
                new SearchComputation(params, searcher, Search::getLine)));
        for (int groupNumber = 0; groupNumber <= params.groups; groupNumber++)
        {
            addGroupFields(
                    params,
                    searcher,
                    groupNumber,
                    fields);
        }
        return fields.stream().toArray(Field[]::new);
    }

    private static void addGroupFields(
            final Params params,
            final Searcher searcher,
            final int groupNumber,
            final Collection<? super Field> fields)
    {
        final String groupPrefix
            = groupNumber == 0 ? "" : groupNumber + SEPARATOR;
        fields.add(Field.getInstance(
                params.fieldNamePrefix + groupPrefix + INPUT_FIELD_NAME,
                new SearchComputation(
                        params, searcher, hit -> getInput(hit, groupNumber))));
        fields.add(Field.getInstance(
                params.fieldNamePrefix + groupPrefix + START_FIELD_NAME,
                new SearchComputation(
                        params, searcher, hit -> getStart(hit, groupNumber))));
        fields.add(Field.getInstance(
                params.fieldNamePrefix + groupPrefix + END_FIELD_NAME,
                new SearchComputation(
                        params, searcher, hit -> getEnd(hit, groupNumber))));
    }
    
    private static String getFound(final Hit hit)
    {
        return hit.isFound() ? "1" : "0";
    }
    
    private static String getGroupCount(final Hit hit)
    {
        return String.valueOf(hit.groupCount);
    }
    
    private static String getLine(final Hit hit)
    {
        return String.valueOf(hit.lineNumber);
    }
    
    private static String getInput(final Hit hit, final int groupNumber)
    {
        if (groupNumber >= hit.groupHits.length)
        {
            return "";
        }
        return hit.groupHits[groupNumber].input;
    }
    
    private static String getStart(final Hit hit, final int groupNumber)
    {
        if (groupNumber >= hit.groupHits.length)
        {
            return "-1";
        }
        return String.valueOf(hit.groupHits[groupNumber].start);
    }
    
    private static String getEnd(final Hit hit, final int groupNumber)
    {
        if (groupNumber >= hit.groupHits.length)
        {
            return "-1";
        }
        return String.valueOf(hit.groupHits[groupNumber].end);
    }
    
    private static final class SearchComputation implements FieldComputation
    {
        private final Params params;
        private final Searcher searcher;
        private final Function<? super Hit, String> value;
        
        private SearchComputation(
                final Params params,
                final Searcher searcher,
                final Function<? super Hit, String> value)
        {
            assert params != null;
            assert searcher != null;
            assert value != null;
            this.params = params;
            this.searcher = searcher;
            this.value = value;
        }

        @Override
        public void reset(final File file) throws IOException
        {
            searcher.reset(this, file, params.regexFlags, params.regex);
        }

        @Override
        public boolean update(
                final byte[] input,
                final int offset,
                final int length) throws IOException
        {
            return searcher.update(this, input, offset, length);
        }

        @Override
        public String get() throws IOException
        {
            return value.apply(
                    searcher.finish(params.charset, params.occurrence));
        }
    }
    
    private static final class Searcher
    {
        private Object master;
        private Pattern pattern;
        private ByteArrayOutputStream buffer;
        private Hit hit;
        
        private Searcher()
        {
        }
        
        private void reset(
                final Object caller,
                final File file,
                final int regexFlags,
                final DynamicValue regex) throws IOException
        {
            if (master != null)
            {
                return;
            }
            master = caller;
            try
            {
                pattern = Pattern.compile(regex.get(file), regexFlags);
                buffer = new ByteArrayOutputStream();
                hit = Hit.getNotFound(pattern);
            }
            catch (final PatternSyntaxException ex)
            {
                throw new IOException(ex);
            }
        }
        
        private boolean update(
                final Object caller,
                final byte[] input,
                final int offset,
                final int length)
        {
            if (master != caller)
            {
                return false;
            }
            buffer.write(input, offset, length);
            return true;
        }
        
        private Hit finish(final Charset charset, final int occurrence)
        {
            if (master == null)
            {
                return hit;
            }
            master = null;
            try (
                    final var reader = new BufferedReader(
                            new InputStreamReader(
                                    new ByteArrayInputStream(
                                            buffer.toByteArray()),
                                    charset));
            )
            {
                return searchBuffer(reader, occurrence);
            }
            catch (final IOException ex)
            {
                throw new AssertionError(ex);
            }
        }

        private Hit searchBuffer(
                final BufferedReader reader,
                final int occurrence) throws IOException
        {
            final var hits = new ArrayList<Hit>();
            int lineNumber = 0;
            for (
                    String line = reader.readLine();
                    line != null;
                    line = reader.readLine())
            {
                lineNumber++;
                final Matcher matcher = pattern.matcher(line);
                while (matcher.find())
                {
                    hits.add(Hit.getFound(lineNumber, matcher));
                    if (completionShortCircuited(hits, occurrence))
                    {
                        return hit;
                    }
                }
            }
            complete(hits, occurrence);
            return hit;
        }
        
        private boolean completionShortCircuited(
                final List<Hit> hits, final int occurrence)
        {
            if (occurrence > 0 && hits.size() >= occurrence)
            {
                pattern = null;
                buffer = null;
                hit = hits.get(occurrence - 1);
                return true;
            }
            return false;
        }
        
        private void complete(final List<Hit> hits, final int occurrence)
        {
            pattern = null;
            buffer = null;
            if (occurrence < 0 && hits.size() >= -occurrence)
            {
                hit = hits.get(hits.size() + occurrence);
            }
        }
    }
    
    private static final class Hit
    {
        private final int lineNumber;
        private final int groupCount;
        private final GroupHit[] groupHits;
        
        private Hit(final int lineNumber, final Matcher matcher)
        {
            assert lineNumber >= 1;
            this.lineNumber = lineNumber;
            this.groupCount = matcher.groupCount();
            this.groupHits = new GroupHit[this.groupCount + 1];
            for (int i = 0; i < this.groupHits.length; i++)
            {
                this.groupHits[i] = new GroupHit(matcher, i);
            }
        }
        
        private Hit(final Pattern pattern)
        {
            this.lineNumber = -1;
            this.groupCount = pattern.matcher("").groupCount();
            this.groupHits = new GroupHit[0];
        }
        
        private static Hit getFound(
                final int lineNumber, final Matcher matcher)
        {
            return new Hit(lineNumber, matcher);
        }
        
        private static Hit getNotFound(final Pattern pattern)
        {
            return new Hit(pattern);
        }
        
        private boolean isFound()
        {
            return lineNumber >= 0;
        }
    }
    
    private static final class GroupHit
    {
        private final String input;
        private final int start;
        private final int end;
        
        private GroupHit(final Matcher matcher, final int groupNumber)
        {
            assert     matcher != null
                    && groupNumber >= 0
                    && groupNumber <= matcher.groupCount();
            this.input = matcher.group(groupNumber);
            this.start = matcher.start(groupNumber);
            this.end = matcher.end(groupNumber);
        }
    }

}
