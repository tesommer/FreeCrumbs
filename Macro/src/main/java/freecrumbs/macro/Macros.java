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
     * Splits a line into pieces separated by space or tab.
     */
    public static String[] split(final String line) {
        final String[] parts = line.trim().split("[ \\t]+");
        if (parts[0].isEmpty()) {
            return new String[0];
        }
        return parts;
    }
    
    /**
     * Whether or not the given prefix is the first part of a line
     * with parts separated by space or tab.
     */
    public static boolean isFirstPart(final String line, final String prefix) {
        final String[] parts = split(line);
        return parts.length >= 1 && parts[0].equals(prefix);
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
