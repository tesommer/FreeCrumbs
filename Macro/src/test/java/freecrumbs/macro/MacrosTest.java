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
    
    @Test
    public void testEvaluateArithmetic() throws MacroException {
        final Script script = new Script("");
        script.setVariable("x", 2);
        script.setVariable("y", 3);
        Assert.assertEquals(
                "x + y",
                5,
                Macros.evaluateArithmetic(script, "x", "+", "y"));
        Assert.assertEquals(
                "x - 21",
                -19,
                Macros.evaluateArithmetic(script, "x", "-", "21"));
        Assert.assertEquals(
                "22 * y",
                66,
                Macros.evaluateArithmetic(script, "22", "*", "y"));
        Assert.assertEquals(
                "23 / 7",
                3,
                Macros.evaluateArithmetic(script, "23", "/", "7"));
        Assert.assertEquals(
                "23 % 7",
                2,
                Macros.evaluateArithmetic(script, "23", "%", "7"));
        try {
            Macros.evaluateArithmetic(script, "27", "/", "0");
            Assert.fail("27 / 0: Expected MacroException.");
        } catch (final MacroException ex) {
            Assert.assertTrue(
                    "Cause: 27 / 0",
                    ex.getCause() instanceof ArithmeticException);
        }
        try {
            Macros.evaluateArithmetic(script, "26", "%", "0");
            Assert.fail("26 % 0: Expected MacroException.");
        } catch (final MacroException ex) {
            Assert.assertTrue(
                    "Cause: 26 % 0",
                    ex.getCause() instanceof ArithmeticException);
        }
        try {
            Macros.evaluateArithmetic(script, "11", "£", "73");
            Assert.fail("11 £ 73: Expected MacroException.");
        } catch (final MacroException ex) {
            Assert.assertNull(
                    "Cause: 11 £ 73",
                    ex.getCause());
        }
    }
    
    @Test
    public void testEvaluateLogical() throws MacroException {
        final Script script = new Script("");
        script.setVariable("x", 7);
        script.setVariable("y", 11);
        Assert.assertTrue(
                "x == 7",
                Macros.evaluateLogical(script, "x", "==", "7"));
        Assert.assertTrue(
                "x != y",
                Macros.evaluateLogical(script, "x", "!=", "y"));
        Assert.assertTrue(
                "7 <= x",
                Macros.evaluateLogical(script, "7", "<=", "x"));
        Assert.assertFalse(
                "11 < y",
                Macros.evaluateLogical(script, "11", "<", "y"));
        Assert.assertFalse(
                "21 >= 23",
                Macros.evaluateLogical(script, "21", ">=", "23"));
        Assert.assertTrue(
                "3 > 2",
                Macros.evaluateLogical(script, "3", ">", "2"));
        try {
            Macros.evaluateLogical(script, "37", "¤", "73");
            Assert.fail("37 ¤ 73: Expected MacroException.");
        } catch (final MacroException ex) {
            Assert.assertNull(
                    "Cause: 37 ¤ 73",
                    ex.getCause());
        }
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
