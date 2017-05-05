package freecrumbs.macro;

import org.junit.Assert;
import org.junit.Test;

public class MacrosTest {

    public MacrosTest() {
    }
    
    @Test
    public void testSplit() {
        assertSplit("");
        assertSplit(" ");
        assertSplit("ab", "ab");
        assertSplit(" cd", "cd");
        assertSplit("ef \t", "ef");
        assertSplit(" \tgh ", "gh");
        assertSplit("ij\t kl", "ij", "kl");
        assertSplit(" mn op  xyz\t", "mn", "op", "xyz");
        assertSplit(" \n \r\ndir ¤#\t\rx", "dir", "¤#", "\rx");
    }
    
    @Test
    public void testSplitWithLimit() {
        assertSplit("", -1);
        assertSplit("", 0);
        assertSplit(" ", 0);
        assertSplit(" ", 2);
        assertSplit("ab", 0, "ab");
        assertSplit("bc", 1, "bc");
        assertSplit(" cd", 0, "cd");
        assertSplit(" de", 1, "de");
        final String line = " ef fg \tgh  \"abcde fgh i jk $\" ";
        assertSplit(
                line, 0, "ef", "fg", "gh", "\"abcde", "fgh", "i", "jk", "$\"");
        assertSplit(line, 4, "ef", "fg", "gh", "\"abcde fgh i jk $\"");
    }
    
    private static void assertSplit(
            final String line, final int limit, final String... expecteds) {
        
        final String[] actuals = Macros.split(line, limit);
        Assert.assertArrayEquals(
                "split(" + line + ", " + limit + ")", expecteds, actuals);
    }
    
    private static void assertSplit(
            final String line, final String... expecteds) {
        
        final String[] actuals = Macros.split(line);
        Assert.assertArrayEquals("split(" + line + ")", expecteds, actuals);
    }

}
