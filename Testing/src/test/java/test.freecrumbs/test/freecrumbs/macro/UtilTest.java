package test.freecrumbs.macro;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

@DisplayName("Util")
public final class UtilTest {

    public UtilTest() {
    }
    
    @Test
    @DisplayName("split(String)")
    public void test1() {
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
    @DisplayName("evaluateArithmetic")
    public void test2() throws MacroException {
        final Script script = dummyScript();
        script.variables().set("x", 2);
        script.variables().set("y", 3);
        assertEquals(
                5,
                Util.evaluateArithmetic(script, "x", "+", "y"),
                "x + y");
        assertEquals(
                -19,
                Util.evaluateArithmetic(script, "x", "-", "21"),
                "x - 21");
        assertEquals(
                66,
                Util.evaluateArithmetic(script, "22", "*", "y"),
                "22 * y");
        assertEquals(
                3,
                Util.evaluateArithmetic(script, "23", "/", "7"),
                "23 / 7");
        assertEquals(
                2,
                Util.evaluateArithmetic(script, "23", "%", "7"),
                "23 % 7");
        assertThrows(
                MacroException.class,
                () -> Util.evaluateArithmetic(script, "27", "/", "0"),
                "27 / 0");
        assertThrows(
                MacroException.class,
                () -> Util.evaluateArithmetic(script, "26", "%", "0"),
                "26 % 0");
        assertThrows(
                MacroException.class,
                () -> Util.evaluateArithmetic(script, "11", "£", "73"),
                "11 £ 73");
    }
    
    @Test
    @DisplayName("evaluateLogical")
    public void test3() throws MacroException {
        final Script script = dummyScript();
        script.variables().set("x", 7);
        script.variables().set("y", 11);
        assertTrue(
                Util.evaluateLogical(script, "x", "==", "7"),
                "x == 7");
        assertTrue(
                Util.evaluateLogical(script, "x", "!=", "y"),
                "x != y");
        assertTrue(
                Util.evaluateLogical(script, "7", "<=", "x"),
                "7 <= x");
        assertFalse(
                Util.evaluateLogical(script, "11", "<", "y"),
                "11 < y");
        assertFalse(
                Util.evaluateLogical(script, "21", ">=", "23"),
                "21 >= 23");
        assertTrue(
                Util.evaluateLogical(script, "3", ">", "2"),
                "3 > 2");
        assertThrows(
                MacroException.class,
                () -> Util.evaluateLogical(script, "37", "¤", "73"),
                "37 ¤ 73");
        assertTrue(
                Util.evaluateLogical(script, "x", "isset", "y"),
                "x isset y");
        assertFalse(
                Util.evaluateLogical(script, "x", "isset", "0"),
                "x isset 0");
        assertFalse(
                Util.evaluateLogical(script, "t", "isset", "1"),
                "t isset 1");
        assertTrue(
                Util.evaluateLogical(script, "t", "isset", "0"),
                "t isset 0");
    }
    
    @Test
    @DisplayName("addKeyCodeVariables")
    public void test4() {
        final Script script = dummyScript();
        Util.addKeyCodeVariables(script);
        Stream.of("VK_A", "VK_B", "VK_T")
            .forEach(s -> assertTrue(
                    script.variables().getNames().contains(s),
                    "Missing variable: " + s));
    }
    
    private static Script dummyScript() {
        try {
            return new Script(MockLocation.DUMMY, MockLoader.DUMMY);
        } catch (final MacroException ex) {
            throw new AssertionError(ex);
        }
    }
    
    private static void assertSplit(
            final String line, final String... expecteds) {
        
        final String[] actuals = Util.split(line);
        assertArrayEquals(expecteds, actuals, "split(" + line + ")");
    }

}
