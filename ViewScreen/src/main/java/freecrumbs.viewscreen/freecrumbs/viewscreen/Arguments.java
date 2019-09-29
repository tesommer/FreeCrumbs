package freecrumbs.viewscreen;

import java.awt.Color;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Arguments {
    
    private static final Pattern
    RGBA_COLOR_REGEX = Pattern.compile(
            "rgba\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)");
    
    private static final Pattern
    HEX_COLOR_REGEX = Pattern.compile(
             "#([0-9a-fA-F][0-9a-fA-F])"
            + "([0-9a-fA-F][0-9a-fA-F])"
            + "([0-9a-fA-F][0-9a-fA-F])"
            + "([0-9a-fA-F][0-9a-fA-F])");

    private Arguments() {
    }
    
    public static boolean parseBoolean(final String arg) {
        if ("0".equals(arg) || "false".equals(arg)) {
            return false;
        }
        return true;
    }
    
    /**
     * Parses either a decimal or hexadecimal number.
     */
    public static int parseInt(final String arg) throws IOException {
        try {
            return Integer.parseInt(arg);
        } catch (final NumberFormatException ex) {
            try {
                return Integer.parseInt(arg, 16);
            } catch (final NumberFormatException ex2) {
                throw new IOException(ex2);
            }
        }
    }
    
    public static Color parseColor(final String arg) throws IOException {
        final Matcher colorMatcher;
        final Matcher rgbaMatcher = RGBA_COLOR_REGEX.matcher(arg);
        if (rgbaMatcher.matches()) {
            colorMatcher = rgbaMatcher;
        } else {
            final Matcher hexMatcher = HEX_COLOR_REGEX.matcher(arg);
            if (!hexMatcher.matches()) {
                throw invalidColor(arg);
            }
            colorMatcher = hexMatcher;
        }
        final int red   = requireValidColorComponent(
                parseInt(colorMatcher.group(1)), arg);
        final int green = requireValidColorComponent(
                parseInt(colorMatcher.group(2)), arg);
        final int blue  = requireValidColorComponent(
                parseInt(colorMatcher.group(3)), arg);
        final int alpah = requireValidColorComponent(
                parseInt(colorMatcher.group(4)), arg);
        return new Color(red, green, blue, alpah);
    }
    
    public static int requireOnePluss(
            final int arg, final String message) throws IOException  {
        
        if (arg < 1) {
            throw new IOException(message);
        }
        return arg;
    }
    
    private static int requireValidColorComponent(
            final int component, final String arg) throws IOException {
        
        if (component < 0 || component > 255) {
            throw invalidColor(arg);
        }
        return component;
    }
    
    private static IOException invalidColor(final String arg) {
        return new IOException("Invalid color: " + arg);
    }

}
