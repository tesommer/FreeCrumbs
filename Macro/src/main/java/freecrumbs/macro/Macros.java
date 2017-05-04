package freecrumbs.macro;

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
     */
    public static String[] split(final String line) {
        final String[] words = line.trim().split("[ \\t]+");
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

}
