package test.freecrumbs.macro;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

public final class UtilTest
{
    public UtilTest()
    {
    }
    
    @Nested
    public static final class
    SPLIT_LINE
    {
        @Test
        public void
        input_is_split_at_space_and_tab
        ()
        {
            assertSplit("ab", "ab");
            assertSplit(" cd", "cd");
            assertSplit("ef \t", "ef");
            assertSplit(" \tgh ", "gh");
            assertSplit("ij\t kl", "ij", "kl");
            assertSplit(" mn op  xyz\t", "mn", "op", "xyz");
            assertSplit(" \n \r\ndir ab\t\rx", "dir", "ab", "\rx");
        }
        
        @Test
        public void
        returns_empty_array_if_given_empty_input
        ()
        {
            assertSplit("");
        }
        
        @Test
        public void
        returns_empty_array_if_given_only_space_and_tab
        ()
        {
            assertSplit(" ");
            assertSplit("\t");
            assertSplit("\t \t ");
        }
        
    }
    
    @Nested
    public static final class
    ARITHMETIC_AND_LOGICAL_EVALUATION
    {
        @Test
        public void
        arithmetic_supports_plus_minus_times_divided_by_and_modulo
        () throws MacroException
        {
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
        }
        
        @Test
        public void
        division_and_modulo_throws_up_if_denominator_is_zero
        ()
        {
            final Script script = dummyScript();
            script.variables().set("x", 2);
            script.variables().set("y", 3);
            assertThrows(
                    MacroException.class,
                    () -> Util.evaluateArithmetic(script, "27", "/", "0"),
                    "27 / 0");
            assertThrows(
                    MacroException.class,
                    () -> Util.evaluateArithmetic(script, "26", "%", "0"),
                    "26 % 0");
        }
        
        @Test
        public void
        logical_supports_standard_logical_operators_and_isset
        () throws MacroException
        {
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
            assertTrue(
                    Util.evaluateLogical(script, "x", "isset", "y"),
                    "x isset y");
        }
        
        @Test
        public void
        x_isset_0_is_true_only_if_x_is_not_a_script_variable
        () throws MacroException
        {
            final Script script = dummyScript();
            script.variables().set("x", 7);
            script.variables().set("y", 11);
            assertFalse(
                    Util.evaluateLogical(script, "x", "isset", "0"),
                    "x isset 0");
            assertTrue(
                    Util.evaluateLogical(script, "t", "isset", "0"),
                    "t isset 0");
        }
        
        @Test
        public void
        x_isset_1_is_true_only_if_x_is_a_script_variable
        () throws MacroException
        {
            final Script script = dummyScript();
            script.variables().set("x", 7);
            script.variables().set("y", 11);
            assertTrue(
                    Util.evaluateLogical(script, "x", "isset", "1"),
                    "x isset 1");
            assertFalse(
                    Util.evaluateLogical(script, "t", "isset", "1"),
                    "t isset 1");
        }
        
        @Test
        public void
        arithmetic_and_logical_throws_unknown_operators_back_at_you
        ()
        {
            final Script script = dummyScript();
            script.variables().set("x", 2);
            script.variables().set("y", 3);
            assertThrows(
                    MacroException.class,
                    () -> Util.evaluateArithmetic(script, "11", "@", "73"),
                    "arithmetic 11 @ 73");
            assertThrows(
                    MacroException.class,
                    () -> Util.evaluateLogical(script, "11", "#", "73"),
                    "logical 11 # 73");
        }
    }
    
    @Test
    public void
    adding_key_code_variables_puts_integer_variables_named_VK_A_etc_in_script
    ()
    {
        final Script script = dummyScript();
        Util.addKeyCodeVariables(script);
        Stream.of("VK_A", "VK_B", "VK_T")
            .forEach(s -> assertTrue(
                    script.variables().names().contains(s),
                    "Missing variable: " + s));
    }
    
    private static Script dummyScript()
    {
        try
        {
            return Script.load(MockLocation.DUMMY, MockLoader.DUMMY);
        }
        catch (final MacroException ex)
        {
            throw new AssertionError(ex);
        }
    }
    
    private static void assertSplit(
            final String line, final String... expecteds)
    {
        final String[] actuals = Util.split(line);
        assertArrayEquals(expecteds, actuals, "split(" + line + ")");
    }

}
