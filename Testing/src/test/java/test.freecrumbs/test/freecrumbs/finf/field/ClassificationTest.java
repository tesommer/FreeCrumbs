package test.freecrumbs.finf.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freecrumbs.finf.FieldComputation;
import freecrumbs.finf.field.Classification;
import freecrumbs.finf.field.Classification.Heuristic;
import test.freecrumbs.finf.FieldTesting;

@DisplayName("Classification")
public final class ClassificationTest
{
    private static final byte T = 't';
    private static final byte B = 'b';
    
    private static final String
    EMPTY = Classification.Category.EMPTY.toString();
    
    private static final String
    TEXT = Classification.Category.TEXT.toString();
    
    private static final String
    BINARY = Classification.Category.BINARY.toString();

    public ClassificationTest()
    {
    }
    
    @Test
    @DisplayName("Empty")
    public void test1() throws IOException
    {
        final FieldComputation comp = getInstance(512, .3);
        comp.reset(FieldTesting.DUMMY_FILE);
        assertEquals(EMPTY, comp.get(), "emtpy");
        final FieldComputation comp2 = getInstance(512, .3);
        final var ups = new byte[][]
        {
            new byte[0],
        };
        assertClassification(comp2, ups, 1, EMPTY);
    }
    
    @Test
    @DisplayName("Containing null char")
    public void test2() throws IOException
    {
        final var ups = new byte[][]
        {
            new byte[] {T, T, T},
            new byte[] {T, 0, T},
            new byte[] {T, T, T},
        };
        assertClassification(getInstance(512, .3), ups, 2, BINARY);
    }
    
    @Test
    @DisplayName("Exceeding limit")
    public void test3() throws IOException
    {
        final var ups = new byte[][]
        {
            new byte[] {T, T, T},
            new byte[] {T, B, B},
            new byte[] {B, B, B},
        };
        assertClassification(getInstance(4, .3), ups, 2, TEXT);
    }
    
    @Test
    @DisplayName("Zero limit")
    public void test4() throws IOException
    {
        final var ups = new byte[][]
        {
            new byte[] {B, B, B, B, B, B, B, B, B, B, B},
            new byte[] {B, B, B, B, B, B, B, B, B, B, B},
            new byte[] {B, B, B, B, B, B, B, B, B, B, B},
            new byte[] {B, B, B, B, B, B, B, B, B, B, B},
        };
        assertClassification(getInstance(0, .3), ups, 4, BINARY);
    }
    
    @Test
    @DisplayName("Negative limit")
    public void test5() throws IOException
    {
        final var ups = new byte[][]
        {
            new byte[] {T, T, T, T, T, T, T, T, T, T, T},
            new byte[] {T, T, T, T, T, T, T, T, T, T, T},
            new byte[] {T, T, T, T, T, T, T, T, T, T, T},
            new byte[] {T, T, T, T, T, T, T, T, T, T, T},
        };
        assertClassification(getInstance(-1, .3), ups, 4, TEXT);
    }
    
    @Test
    @DisplayName("Below threshold")
    public void test6() throws IOException
    {
        final var ups = new byte[][]
        {
            new byte[] {T, B, B, T, T, T, B, T, T, T, B},
            new byte[] {T, B, B, T, T, T, T, T, T, B, B},
            new byte[] {B, T, T, B, T, T, T, T, T, B, B},
            new byte[] {B, T, T, T, T, T, T, T, T, T, T},
        };
        assertClassification(getInstance(1024, .3), ups, 4, TEXT);
    }
    
    @Test
    @DisplayName("Exceeding threshold")
    public void test7() throws IOException
    {
        final var ups = new byte[][]
        {
            new byte[] {T, B, B, T, T, T, B, T, T, T, B},
            new byte[] {T, B, B, T, T, T, T, T, T, B, B},
            new byte[] {B, T, T, B, T, T, T, T, T, B, B},
            new byte[] {B, T, T, B, T, T, T, T, T, T, T},
        };
        assertClassification(getInstance(1024, .3), ups, 4, BINARY);
    }
    
    @Test
    @DisplayName("Zero threshold")
    public void test8() throws IOException
    {
        assertClassification(
                getInstance(512, 0),
                new byte[][] {new byte[] {T, T, T}},
                1,
                TEXT);
        assertClassification(
                getInstance(512, 0),
                new byte[][] {new byte[] {T, B, T}},
                1,
                BINARY);
    }
    
    @Test
    @DisplayName("Negative threshold")
    public void test9() throws IOException
    {
        assertClassification(
                getInstance(512, -1),
                new byte[][] {new byte[] {T, T, T}},
                1,
                BINARY);
        assertClassification(
                getInstance(512, -1),
                new byte[][] {new byte[] {T, B, T}},
                1,
                BINARY);
        assertClassification(
                getInstance(1, -1),
                new byte[][] {new byte[] {T, T, T}, new byte[] {T, T, T}},
                1,
                BINARY);
    }
    
    private static void assertClassification(
            final FieldComputation comp,
            final byte[][] updates,
            final int expectedEnough,
            final String expectedValue) throws IOException
    {
        comp.reset(FieldTesting.DUMMY_FILE);
        int actualEnough = updates.length;
        for (int i = 0; i < updates.length; i++)
        {
            if (!comp.update(updates[i], 0, updates[i].length))
            {
                actualEnough = i + 1;
                break;
            }
        }
        assertEquals(expectedEnough, actualEnough, "Enough updates");
        assertEquals(expectedValue, comp.get(), "Value");
    }
    
    private static FieldComputation getInstance(
            final int limit, final double threshold)
    {
        final Heuristic heuristic = Heuristic.DEFAULT
                .withLimit(limit)
                .withThreshold(threshold)
                .withIsTextChar(ch -> ch == T);
        return Classification.field(heuristic, String::valueOf)
                .computation();
    }

}
