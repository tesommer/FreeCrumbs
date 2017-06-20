package freecrumbs.macro;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utility methods.
 * 
 * @author Tone Sommerland
 */
public final class Macros {

    private Macros() {
    }
    
    /**
     * Splits a line into words separated by space or tab.
     * @param limit the maximum number of parts to split the line in,
     * starting left.
     * The returned array will not be longer than the limit.
     * If the line contains more words than the limit,
     * the last element will contain a trimmed substring of the input line
     * after the first {@code limit - 1} words.
     */
    public static String[] split(final String line, final int limit) {
        final String[] words = line.trim().split("[ \\t]+", limit);
        if (words[0].isEmpty()) {
            return new String[0];
        }
        return words;
    }
    
    /**
     * Splits a line into words separated by space or tab.
     */
    public static String[] split(final String line) {
        return split(line, 0);
    }
    
    /**
     * Whether or not the given prefix is the first word of a line
     * with words separated by space or tab.
     */
    public static boolean isFirstWord(final String prefix, final String line) {
        final String[] words = split(line);
        return words.length >= 1 && words[0].equals(prefix);
    }
    
    /**
     * Creates script variables containing key codes.
     * The variables are named {@code VK_A}, {@code VK_ALT}, etc.
     * @see java.awt.event.KeyEvent
     */
    public static void addKeyCodeVariables(final Script script) {
        for (final Field field : KeyEvent.class.getDeclaredFields()) {
            if (
                    field.getName().startsWith("VK_")
                    && (field.getModifiers() & Modifier.PUBLIC) != 0
                    && (field.getModifiers() & Modifier.STATIC) != 0) {
                try {
                    script.setVariable(field.getName(), field.getInt(null));
                } catch (final IllegalAccessException ex) {
                    throw new AssertionError(ex);
                }
            }
        }
    }
    
    /**
     * Evaluates a three-word arithmetic expression.
     * @param script the script
     * @param left left operand (integer or variable)
     * @param operator +, -, *, / or %
     * @param right right operand (integer or variable)
     */
    public static int evaluateArithmetic(
            final Script script,
            final String left,
            final String operator,
            final String right) throws MacroException {
        
        if ("+".equals(operator)) {
            return script.getValue(left) + script.getValue(right);
        } else if ("-".equals(operator)) {
            return script.getValue(left) - script.getValue(right);
        } else if ("*".equals(operator)) {
            return script.getValue(left) * script.getValue(right);
        } else if ("/".equals(operator)) {
            try {
                return script.getValue(left) / script.getValue(right);
            } catch (final ArithmeticException ex) {
                throw new MacroException(ex);
            }
        } else if ("%".equals(operator)) {
            try {
                return script.getValue(left) % script.getValue(right);
            } catch (final ArithmeticException ex) {
                throw new MacroException(ex);
            }
        }
        throw new MacroException("Invalid operator: " + operator);
    }
    
    /**
     * Evaluates a three-word logical expression.
     * @param script the script
     * @param left left operand (integer or variable)
     * @param operator ==, !=, &lt;, &gt;, &lt;=, &gt;= or isset.
     * isset tests the existence of a variable;
     * {@code x isset 1} is true if {@code x} exists and
     * {@code x isset 0} is true if {@code x} is nonexistent.
     * @param right right operand (integer or variable)
     */
    public static boolean evaluateLogical(
            final Script script,
            final String left,
            final String operator,
            final String right) throws MacroException {
        
        if ("==".equals(operator)) {
            return script.getValue(left) == script.getValue(right);
        } else if ("!=".equals(operator)) {
            return script.getValue(left) != script.getValue(right);
        } else if ("<".equals(operator)) {
            return script.getValue(left) < script.getValue(right);
        } else if (">".equals(operator)) {
            return script.getValue(left) > script.getValue(right);
        } else if ("<=".equals(operator)) {
            return script.getValue(left) <= script.getValue(right);
        } else if (">=".equals(operator)) {
            return script.getValue(left) >= script.getValue(right);
        } else if ("isset".equals(operator)) {
            final boolean existence = script.getValue(right) != 0;
            return script.getVariableNames().contains(left) == existence;
        }
        throw new MacroException("Invalid operator: " + operator);
    }
    
    /**
     * Generates key events that types the given value.
     * @param robot event generator
     * @param value the digits to type
     */
    public static void type(final Robot robot, final int value) {
        final String digits = String.valueOf(value);
        for (int i = 0; i < digits.length(); i++) {
            final char ch = digits.charAt(i);
            final int codePoint = (int)ch;
            robot.keyPress(codePoint);
            robot.keyRelease(codePoint);
        }
    }

}
