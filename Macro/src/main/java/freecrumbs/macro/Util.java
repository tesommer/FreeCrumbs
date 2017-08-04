package freecrumbs.macro;

import java.awt.Dimension;
import java.awt.Graphics;
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

import javax.swing.ImageIcon;

/**
 * Utility methods.
 * 
 * @author Tone Sommerland
 */
public final class Util {
    
    private static final Location
    UXO_LOCATION = new Location() {

        @Override
        public String getBase() {
            return "";
        }

        @Override
        public Location refer(final String relative) throws MacroException {
            throw new MacroException("UXO location just exploded!");
        }

        @Override
        public InputStream open() throws MacroException {
            return new ByteArrayInputStream(new byte[0]);
        }

    };
    
    private static final Loader UXO_LOADER = in -> new Macro[0];

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
        for (final Field field : KeyEvent.class.getDeclaredFields()) {
            if (
                    field.getName().startsWith("VK_")
                    && (field.getModifiers() & Modifier.PUBLIC) != 0
                    && (field.getModifiers() & Modifier.STATIC) != 0) {
                try {
                    script.variables()
                        .set(field.getName(), field.getInt(null));
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
            return
                    script.variables().getValue(left)
                  + script.variables().getValue(right);
        } else if ("-".equals(operator)) {
            return
                    script.variables().getValue(left)
                  - script.variables().getValue(right);
        } else if ("*".equals(operator)) {
            return
                    script.variables().getValue(left)
                  * script.variables().getValue(right);
        } else if ("/".equals(operator)) {
            try {
                return
                        script.variables().getValue(left)
                      / script.variables().getValue(right);
            } catch (final ArithmeticException ex) {
                throw new MacroException(ex);
            }
        } else if ("%".equals(operator)) {
            try {
                return
                        script.variables().getValue(left)
                      % script.variables().getValue(right);
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
            return
                    script.variables().getValue(left)
                 == script.variables().getValue(right);
        } else if ("!=".equals(operator)) {
            return
                    script.variables().getValue(left)
                 != script.variables().getValue(right);
        } else if ("<".equals(operator)) {
            return
                    script.variables().getValue(left)
                  < script.variables().getValue(right);
        } else if (">".equals(operator)) {
            return
                    script.variables().getValue(left)
                  > script.variables().getValue(right);
        } else if ("<=".equals(operator)) {
            return
                    script.variables().getValue(left)
                 <= script.variables().getValue(right);
        } else if (">=".equals(operator)) {
            return
                    script.variables().getValue(left)
                 >= script.variables().getValue(right);
        } else if ("isset".equals(operator)) {
            final boolean existence = script.variables().getValue(right) != 0;
            return script.variables().getNames().contains(left) == existence;
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
     * Loads an image from file.
     * @throws MacroException if the image could not be loaded.
     */
    public static BufferedImage loadImage(final String file)
            throws MacroException {
        
        final ImageIcon icon = new ImageIcon(file);
        if (icon.getIconWidth() < 1 || icon.getIconHeight() < 1) {
            throw new MacroException("Image could not be loaded: " + file);
        }
        final BufferedImage image = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        final Graphics g = image.getGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);
        g.dispose();
        return image;
    }
    
    /**
     * Finds the x-y coordinates of an image within another image.
     * Starts looking at the top left of {@code scanImage}.
     * @param occurrence the occurrence to find
     * (the first occurrence has number one).
     * @return an empty array if not found.
     */
    public static int[] xyOf(
            final BufferedImage findImage,
            final BufferedImage scanImage,
            final int occurrence) {
        
        int count = 0;
        for (int x = 0; x < scanImage.getWidth() - findImage.getWidth(); x++) {
            for (int y = 0;
                    y < scanImage.getHeight() -  findImage.getHeight(); y++) {
                if (isImageAt(findImage, scanImage, x, y)
                        && ++count == occurrence) {
                    return new int[] {x, y};
                }
            }
        }
        return new int[0];
    }

    private static boolean isImageAt(
            final BufferedImage findImage,
            final BufferedImage scanImage,
            final int x,
            final int y) {
        
        for (int x2 = 0; x2 < findImage.getWidth(); x2++) {
            for (int y2 = 0; y2 < findImage.getHeight(); y2++) {
                final int rgb = findImage.getRGB(x2, y2);
                final int rgb2 = scanImage.getRGB(x + x2, y + y2);
                if (rgb != rgb2) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Creates a screenshot of the entire screen.
     */
    public static BufferedImage createScreenCapture(final Robot robot) {
        final Dimension screenSize
            = Toolkit.getDefaultToolkit().getScreenSize();
        return robot.createScreenCapture(new Rectangle(screenSize));
    }
    
    /**
     * Creates a dummy macro script.
     */
    public static Script createEmptyScript() {
        try {
            return new Script(UXO_LOCATION, UXO_LOADER);
        } catch (final MacroException ex) {
            throw new AssertionError(ex);
        }
    }

}
