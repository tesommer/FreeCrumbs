package freecrumbs.macro;

import java.util.Arrays;

/**
 * An abstract base class for commands
 * with the syntax {@code command [params...]}.
 * The command and parameters are separated by white space.
 * 
 * @author Tone Sommerland
 */
public abstract class Command implements GestureParser
{
    private final String name;
    private final int minParams;
    private final int maxParams;

    /**
     * Base constructor.
     * The command name will be trimmed,
     * and must contain one or more non-whitespace chars.
     * @param name the name of the command
     * @param minParams the minimum number of supported parameters
     * @param maxParams the maximum number of supported parameters
     * @throws IllegalArgumentException
     * if {@code name} is empty or contains only whitespace chars,
     * if {@code minParams} or {@code maxParams} are less than zero,
     * or if {@code minParams} is greater than {@code maxParams}
     */
    protected Command(
            final String name, final int minParams, final int maxParams)
    {
        if (name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Empty name: " + name);
        }
        if (minParams < 0 || maxParams < 0 || minParams > maxParams)
        {
            throw new IllegalArgumentException(
                    "min/maxParams: minParams="
                            + minParams + ", maxParams=" + maxParams);
        }
        this.name = name.trim();
        this.minParams = minParams;
        this.maxParams = maxParams;
    }
    
    /**
     * Convenience method that either returns a parameter,
     * or a default value if the index is greater than or equal to
     * the length of the parameter array.
     * @throws IllegalArgumentException if {@code index < 0}
     */
    protected static String paramOrDefault(
            final String[] params,
            final int index,
            final String defaultParam)
    {
        if (index < 0)
        {
            throw new IllegalArgumentException("index < 0: " +  index);
        }
        return index < params.length ? params[index] : defaultParam;
    }

    @Override
    public final boolean supports(final String line)
    {
        return Util.isFirstWord(name, line);
    }

    @Override
    public final Gesture parse(final String line) throws MacroException
    {
        final String[] words = Util.split(line);
        final String[] params = Arrays.copyOfRange(words, 1, words.length);
        if (params.length < minParams || params.length > maxParams)
        {
            throw new MacroException("Syntax incorrect: " + line);
        }
        return gesture(line, params);
    }
    
    /**
     * Returns a gesture that executes this command.
     * @param line the parsed line
     * @param params the command parameters
     */
    protected abstract Gesture gesture(String line, String[] params)
            throws MacroException;

}
