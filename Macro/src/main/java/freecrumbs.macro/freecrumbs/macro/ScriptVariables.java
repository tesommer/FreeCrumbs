package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Script integer-variables.
 * 
 * @author Tone Sommerland
 */
public final class ScriptVariables
{
    private final Map<String, Integer> variables = new HashMap<>();

    ScriptVariables()
    {
    }

    /**
     * The names of all variables stored in the script.
     */
    public Set<String> getNames()
    {
        return Collections.unmodifiableSet(variables.keySet());
    }
    
    /**
     * Sets the value of a variable.
     * Creates the variable if it doesn't exist.
     * @param name the variable name
     * @param value the variable value
     */
    public void set(final String name, final int value)
    {
        variables.put(requireNonNull(name, "name"), value);
    }
    
    /**
     * Removes a variable if it exists.
     * @param name the variable name
     */
    public void remove(final String name)
    {
        variables.remove(requireNonNull(name, "name"));
    }
    
    /**
     * Returns the value of a variable.
     * @param name the variable name
     * @throws MacroException if the variable does not exist
     */
    public int get(final String name) throws MacroException
    {
        if (variables.containsKey(name))
        {
            return variables.get(name);
        }
        throw new MacroException("No such variable: " + name);
    }
    
    /**
     * Returns the value of an integer literal or a stored variable.
     * @param nameOrLiteral either an integer literal or a variable name
     * @throws MacroException if it's neither
     * a valid integer nor a stored variable
     */
    public int value(final String nameOrLiteral) throws MacroException
    {
        try
        {
            return Integer.valueOf(nameOrLiteral);
        }
        catch (final NumberFormatException ex)
        {
            return get(nameOrLiteral);
        }
    }

}
