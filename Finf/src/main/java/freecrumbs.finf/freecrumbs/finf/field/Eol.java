package freecrumbs.finf.field;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

/**
 * End-of-line-related fields.
 * Theses are the fields:
 * <ul>
 * <li>eol: the system-dependent line terminator</li>
 * <li>cr: carriage return</li>
 * <li>lf: line feed</li>
 * <li>crlf: carriage return followed by line feed</li>
 * <li>eolcount: total line count</li>
 * <li>crcount: carriage-return count</li>
 * <li>lfcount: line-feed count</li>
 * <li>crlfcount: carriage-return-line-feed count</li>
 * </ul>
 * 
 * @author Tone Sommerland
 */
public final class Eol
{
    private static final String EOL_COUNT_FIELD_NAME = "eolcount";
    private static final String CR_COUNT_FIELD_NAME = "crcount";
    private static final String LF_COUNT_FIELD_NAME = "lfcount";
    private static final String CRLF_COUNT_FIELD_NAME = "crlfcount";
    private static final String EOL_FIELD_NAME = "eol";
    private static final String CR_FIELD_NAME = "cr";
    private static final String LF_FIELD_NAME = "lf";
    private static final String CRLF_FIELD_NAME = "crlf";
    
    private static final String EOL = System.getProperty("line.separator");

    private Eol()
    {
    }
    
    /**
     * Returns instances of EOL-related fields.
     * Some of the returned instances are collaborating.
     */
    public static Field[] fields()
    {
        final var counter = new EolCounter();
        return new Field[]
        {
                Field.computed(
                        EOL_COUNT_FIELD_NAME,
                        new EolCountComputation(counter, Eol::getEolCount)),
                Field.computed(
                        CR_COUNT_FIELD_NAME,
                        new EolCountComputation(counter, Eol::getCrCount)),
                Field.computed(
                        LF_COUNT_FIELD_NAME,
                        new EolCountComputation(counter, Eol::getLfCount)),
                Field.computed(
                        CRLF_COUNT_FIELD_NAME,
                        new EolCountComputation(counter, Eol::getCrlfCount)),
                Field.simple(EOL_FIELD_NAME,  file -> EOL),
                Field.simple(CR_FIELD_NAME,   file -> "\r"),
                Field.simple(LF_FIELD_NAME,   file -> "\n"),
                Field.simple(CRLF_FIELD_NAME, file -> "\r\n"),
        };
    }
    
    private static final class EolCountComputation implements FieldComputation
    {
        private final EolCounter counter;
        private final Function<? super EolCounter, String> value;

        private EolCountComputation(
                final EolCounter counter,
                final Function<? super EolCounter, String> value)
        {
            assert counter != null;
            assert value != null;
            this.counter = counter;
            this.value = value;
        }

        @Override
        public void reset(final File file) throws IOException
        {
            counter.reset(this);
        }

        @Override
        public boolean update(
                final byte[] input,
                final int offset,
                final int length) throws IOException
        {
            final int offsetPlusLength = offset + length;
            for (int i = offset; i < offsetPlusLength; i++)
            {
                if (!counter.update(this, input[i]))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String get() throws IOException
        {
            counter.finish();
            return value.apply(counter);
        }
        
    }
    
    private static String getEolCount(final EolCounter counter)
    {
        return String.valueOf(
                counter.crCount + counter.lfCount + counter.crlfCount);
    }
    
    private static String getCrCount(final EolCounter counter)
    {
        return String.valueOf(counter.crCount);
    }
    
    private static String getLfCount(final EolCounter counter)
    {
        return String.valueOf(counter.lfCount);
    }
    
    private static String getCrlfCount(final EolCounter counter)
    {
        return String.valueOf(counter.crlfCount);
    }
    
    private static final class EolCounter
    {
        private Object master;
        private int crCount;
        private int lfCount;
        private int crlfCount;
        private boolean lastWasCr;
        
        private EolCounter()
        {
        }
        
        private void reset(final Object caller)
        {
            if (master == null)
            {
                master = caller;
                crCount = 0;
                lfCount = 0;
                crlfCount = 0;
                lastWasCr = false;
            }
        }

        private boolean update(final Object caller, final byte b)
        {
            if (master != caller)
            {
                return false;
            }
            if (b == '\n')
            {
                if (lastWasCr)
                {
                    crlfCount++;
                    lastWasCr = false;
                }
                else
                {
                    lfCount++;
                }
            }
            else
            {
                if (lastWasCr)
                {
                    crCount++;
                }
                lastWasCr = b == '\r';
            }
            return true;
        }
        
        private void finish()
        {
            master = null;
            if (lastWasCr)
            {
                crCount++;
                lastWasCr = false;
            }
        }
    }

}
