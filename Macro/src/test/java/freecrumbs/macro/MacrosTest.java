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
    
    private static void assertSplit(
            final String line, final String... expecteds) {
        
        final String[] actuals = Macros.split(line);
        Assert.assertArrayEquals("split(" + line + ")", expecteds, actuals);
    }

}
