package freecrumbs.macro;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
            script.getVariables().set(field.getName(), field.getInt(null));
        } catch (final IllegalAccessException ex) {
            throw new AssertionError(ex);
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
        
        final int leftValue = script.getVariables().valueOf(left);
        final int rightValue = script.getVariables().valueOf(right);
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
        
        if ("isset".equals(operator)) {
            final boolean existence
                = script.getVariables().valueOf(right) != 0;
            return script.getVariables().getNames().contains(left) == existence;
        }
        final int leftValue = script.getVariables().valueOf(left);
        final int rightValue = script.getVariables().valueOf(right);
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
    
    private static final Location
    UXO_LOCATION = new Location() {

        @Override
        public String getBase() {
            return "";
        }

        @Override
        public Location refer(final String target) throws MacroException {
            throw new MacroException("UXO location just exploded!");
        }

        @Override
        public InputStream open() throws MacroException {
            return new ByteArrayInputStream(new byte[0]);
        }

    };
    
    private static final Loader UXO_LOADER = new Loader() {

        @Override
        public Macro[] load(final InputStream in) throws MacroException {
            return new Macro[0];
        }

        @Override
        public RecursionGuard getRecursionGuard() {
            return new AtomicRecursionGuard(0);
        }
        
    };
    
    /**
     * Creates a dummy macro script.
     * The returned script contains no macros.
     */
    public static Script createEmptyScript() {
        try {
            return new Script(UXO_LOCATION, UXO_LOADER);
        } catch (final MacroException ex) {
            throw new AssertionError(ex);
        }
    }

}