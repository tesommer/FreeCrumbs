package freecrumbs.macro;

import java.util.Arrays;

/**
 * An abstract base class for commands
 * with the syntax {@code command [params...]}.
 * The command and parameters are separated by white space.
 * 
 * @author Tone Sommerland
 */
public abstract class Command implements GestureParser {
    private final String name;
    private final int minParams;
    private final int maxParams;

    /**
     * Base constructor.
     * The command name will be trimmed,
     * and must contain one or more non-white space chars.
     * @param name the name of the command
     * @param minParams the minimum number of supported parameters
     * @param maxParams the maximum number of supported parameters
     */
    public Command(
            final String name, final int minParams, final int maxParams) {
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException(name);
        }
        if (minParams < 0 || maxParams < 0 || minParams > maxParams) {
            throw new IllegalArgumentException("min/maxParams");
        }
        this.name = name.trim();
        this.minParams = minParams;
        this.maxParams = maxParams;
    }

    @Override
    public boolean supports(final String line) {
        final String[] parts = split(line);
        return parts.length >= 1 && parts[0].equals(name);
    }

    @Override
    public Gesture parse(final String line) throws MacroException {
        final String[] parts = split(line);
        final String[] params = Arrays.copyOfRange(parts, 1, parts.length);
        if (params.length < minParams || params.length > maxParams) {
            throw new MacroException("Syntax incorrect: " + line);
        }
        return getGesture(params);
    }
    
    /**
     * Returns a gesture that executes this command.
     * @param params the command parameters
     */
    protected abstract Gesture getGesture(String[] params)
            throws MacroException;
    
    /**
     * Splits a line into pieces separated by space or tab.
     */
    private static String[] split(final String line) {
        final String[] parts = line.trim().split("[ \\t]+");
        if (parts[0].isEmpty()) {
            return new String[0];
        }
        return parts;
    }

}
