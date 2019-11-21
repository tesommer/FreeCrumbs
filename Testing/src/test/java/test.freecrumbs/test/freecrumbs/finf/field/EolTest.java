package test.freecrumbs.finf.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freecrumbs.finf.Field;
import freecrumbs.finf.field.Eol;
import test.freecrumbs.finf.FieldTesting;

@DisplayName("Eol")
public final class EolTest
{
    private static final String EOL_COUNT_FIELD_NAME = "eolcount";
    private static final String CR_COUNT_FIELD_NAME = "crcount";
    private static final String LF_COUNT_FIELD_NAME = "lfcount";
    private static final String CRLF_COUNT_FIELD_NAME = "crlfcount";

    public EolTest()
    {
    }
    
    @Test
    @DisplayName("EOL-counting: Mixed-EOL content, varying buffer size")
    public void test1() throws IOException
    {
        final String content = "ab\nc\r\nde\n\rf\rg";
        assertEolCounts(
                "5",
                "2",
                "2",
                "1",
                getEolCounts(content, 1024, Eol.getFields()));
        assertEolCounts(
                "5",
                "2",
                "2",
                "1",
                getEolCounts(content, 2, Eol.getFields()));
        assertEolCounts(
                "5",
                "2",
                "2",
                "1",
                getEolCounts(content, 1, Eol.getFields()));
    }
    
    @Test
    @DisplayName("EOL-counting: Serve two files to same field instances")
    public void test2() throws IOException
    {
        final String content1 = "a\nb\nc\nd\n";
        final String content2 = "z\r\ny\r\nx\r\n";
        final Field[] fields = Eol.getFields();
        assertEolCounts(
                "4",
                "0",
                "4",
                "0",
                getEolCounts(content1, 16, fields));
        assertEolCounts(
                "3",
                "0",
                "0",
                "3",
                getEolCounts(content2, 32, fields));
    }
    
    @Test
    @DisplayName("EOL-counting: Empty file")
    public void test3() throws IOException
    {
        final String content = "";
        assertEolCounts(
                "0",
                "0",
                "0",
                "0",
                getEolCounts(content, 2, Eol.getFields()));
    }
    
    @Test
    @DisplayName("EOL-counting: Buffer-split CRLF")
    public void test4() throws IOException
    {
        final String content = "abc\r\nxyz";
        final int bufferSize = 4;
        assertEolCounts(
                "1",
                "0",
                "0",
                "1",
                getEolCounts(content, bufferSize, Eol.getFields()));
    }
    
    @Test
    @DisplayName("EOL-counting: Trailing CR")
    public void test5() throws IOException
    {
        final String content = "abc\r";
        assertEolCounts(
                "1",
                "1",
                "0",
                "0",
                getEolCounts(content, 512, Eol.getFields()));
    }
    
    @Test
    @DisplayName("EOL-counting: Test master-slave behaviour")
    public void test6() throws IOException
    {
        final String content = "abc\r\nx\ny\rz";
        final Field[] fields = Eol.getFields();
        final Field eol = FieldTesting.getField(EOL_COUNT_FIELD_NAME, fields);
        final Field cr = FieldTesting.getField(CR_COUNT_FIELD_NAME, fields);
        final Field lf = FieldTesting.getField(LF_COUNT_FIELD_NAME, fields);
        final Field crlf = FieldTesting.getField(CRLF_COUNT_FIELD_NAME, fields);
        reset(lf, eol, crlf, cr);
        serve(content, 3, lf);
        final EolCounts counts = finish(eol, cr, lf, crlf);
        assertEolCounts("3", "1", "1", "1", counts);
    }
    
    private static EolCounts getEolCounts(
            final String content,
            final int bufferSize,
            final Field... fields) throws IOException
    {
        reset(fields);
        serve(content, bufferSize, fields);
        return finish(fields);
    }

    private static void reset(final Field... fields) throws IOException
    {
        FieldTesting.resetComputations(fields);
    }

    private static void serve(
            final String content,
            final int bufferSize,
            final Field... fields) throws IOException
    {
        FieldTesting.updateComputations(
                content.getBytes("UTF-8"), bufferSize, fields);
    }
    
    private static EolCounts finish(final Field... fields) throws IOException
    {
        final String eolCount = FieldTesting.getField(
                EOL_COUNT_FIELD_NAME, fields).computation().get();
        final String crCount = FieldTesting.getField(
                CR_COUNT_FIELD_NAME, fields).computation().get();
        final String lfCount = FieldTesting.getField(
                LF_COUNT_FIELD_NAME, fields).computation().get();
        final String crlfCount = FieldTesting.getField(
                CRLF_COUNT_FIELD_NAME, fields).computation().get();
        return new EolCounts(eolCount, crCount, lfCount, crlfCount);
    }
    
    private static void assertEolCounts(
            final String expectedEolCount,
            final String expectedCrCount,
            final String expectedLfCount,
            final String expectedCrlfCount,
            final EolCounts actualCounts)
    {
        assertEquals(
                expectedEolCount,
                actualCounts.eolCount,
                EOL_COUNT_FIELD_NAME);
        assertEquals(
                expectedCrCount,
                actualCounts.crCount,
                CR_COUNT_FIELD_NAME);
        assertEquals(
                expectedLfCount,
                actualCounts.lfCount,
                LF_COUNT_FIELD_NAME);
        assertEquals(
                expectedCrlfCount,
                actualCounts.crlfCount,
                CRLF_COUNT_FIELD_NAME);
    }
    
    private static final class EolCounts
    {
        private final String eolCount;
        private final String crCount;
        private final String lfCount;
        private final String crlfCount;
        
        private EolCounts(
                final String eolCount,
                final String crCount,
                final String lfCount,
                final String crlfCount)
        {
            assert eolCount != null;
            assert crCount != null;
            assert lfCount != null;
            assert crlfCount != null;
            this.eolCount = eolCount;
            this.crCount = crCount;
            this.lfCount = lfCount;
            this.crlfCount = crlfCount;
        }
    }

}
