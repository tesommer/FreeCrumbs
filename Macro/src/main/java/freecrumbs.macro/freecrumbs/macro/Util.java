package freecrumbs.macro;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility methods.
 * 
 * @author Tone Sommerland
 */
public final class Util {

    private Util() {
    }
    
    /**
     * Splits a line into words separated by space or tab.
     */
    public static String[] split(final String line) {
        return split(line, 0);
    }
    
    /**
     * Splits a line into words separated by space or tab.
     * {@code limit} is the maximum number of parts to split the line into,
     * starting left.
     * The returned array will be no longer than the limit.
     * If the line contains more words than the limit,
     * the last element will contain a trimmed substring of the input line
     * after the first {@code limit - 1} words.
     * A limit of zero or less turns the limit off.
     */
    private static String[] split(final String line, final int limit) {
        final String[] words = line.trim().split("[ \\t]+", limit);
        if (words[0].isEmpty()) {
            return new String[0];
        }
        return words;
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
        Stream.of(KeyEvent.class.getDeclaredFields())
            .filter(Util::isKeyCodeConstant)
            .forEach(field -> addKeyCodeVariable(script, field));
    }
    
    private static boolean isKeyCodeConstant(final Field field) {
        return
                    field.getName().startsWith("VK_")
                && (field.getModifiers() & Modifier.PUBLIC) != 0
                && (field.getModifiers() & Modifier.STATIC) != 0;
    }

    private static void addKeyCodeVariable(
            final Script script, final Field field) {
        
        try {
            script.variables().set(field.getName(), field.getInt(null));
        } catch (final IllegalAccessException ex) {
            throw new AssertionError(ex);
        }
    }
    
    /**
     * Evaluates a three-word arithmetic expression.
     * @param script the script
     * @param left left operand (integer or variable)
     * @param operator one of {@code + - * / %}
     * @param right right operand (integer or variable)
     */
    public static int evaluateArithmetic(
            final Script script,
            final String left,
            final String operator,
            final String right) throws MacroException {
        
        final int leftValue = script.variables().value(left);
        final int rightValue = script.variables().value(right);
        if ("+".equals(operator)) {
            return leftValue + rightValue;
        } else if ("-".equals(operator)) {
            return leftValue - rightValue;
        } else if ("*".equals(operator)) {
            return leftValue * rightValue;
        } else if ("/".equals(operator)) {
            try {
                return leftValue / rightValue;
            } catch (final ArithmeticException ex) {
                throw new MacroException(ex);
            }
        } else if ("%".equals(operator)) {
            try {
                return leftValue % rightValue;
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
     * @param operator one of {@code == != < > <= >= isset}.
     * {@code isset} tests the existence of a variable;
     * {@code x isset 1} is true if {@code x} exists and
     * {@code x isset 0} is true if {@code x} is nonexistent.
     * @param right right operand (integer or variable)
     */
    public static boolean evaluateLogical(
            final Script script,
            final String left,
            final String operator,
            final String right) throws MacroException {
        
        if ("isset".equals(operator)) {
            final boolean existence
                = script.variables().value(right) != 0;
            return script.variables().getNames().contains(left) == existence;
        }
        final int leftValue = script.variables().value(left);
        final int rightValue = script.variables().value(right);
        if ("==".equals(operator)) {
            return leftValue == rightValue;
        } else if ("!=".equals(operator)) {
            return leftValue != rightValue;
        } else if ("<".equals(operator)) {
            return leftValue < rightValue;
        } else if (">".equals(operator)) {
            return leftValue > rightValue;
        } else if ("<=".equals(operator)) {
            return leftValue <= rightValue;
        } else if (">=".equals(operator)) {
            return leftValue >= rightValue;
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
        IntStream.iterate(0, index -> index + 1)
            .limit(digits.length())
            .map(digits::codePointAt)
            .forEach(codePoint -> {
                robot.keyPress(codePoint);
                robot.keyRelease(codePoint);
            });
    }
    
    /**
     * Creates a screenshot of the entire screen.
     */
    public static BufferedImage createScreenCapture(final Robot robot) {
        final Dimension screenSize
            = Toolkit.getDefaultToolkit().getScreenSize();
        return robot.createScreenCapture(new Rectangle(screenSize));
    }

}
