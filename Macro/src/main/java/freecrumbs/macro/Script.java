package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A macro script.
 * 
 * @author Tone Sommerland
 */
public class Script {
    
    private final Map<String, Integer>
    variables = new HashMap<String, Integer>();
    
    private final String location;
    private final Macro[] macros;

    /**
     * Creates a new macro script.
     * @param location the location of the script file
     * @param macros the macros in this script
     */
    public Script(final String location, final Macro... macros) {
        this.location = requireNonNull(location, "location");
        this.macros = macros.clone();
    }
    
    /**
     * The location of the script file.
     */
    public String getLocation() {
        return location;
    }

    /**
     * The names of all variables stored in this script.
     */
    public Set<String> getVariableNames() {
        return Collections.unmodifiableSet(variables.keySet());
    }
    
    /**
     * Sets the value of a variable.
     * Creates the variable if it doesn't exist.
     * @param name the variable name
     * @param value the variable value
     */
    public void setVariable(final String name, final int value) {
        variables.put(name, value);
    }
    
    /**
     * Removes a variable if it exists.
     * @param name the variable name
     */
    public void removeVariable(final String name) {
        variables.remove(name);
    }
    
    /**
     * Returns the value of a variable.
     * @param name the variable name
     * @throws MacroException if the variable does not exist.
     */
    public int getVariable(final String name) throws MacroException {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        throw new MacroException("No such variable: " + name);
    }
    
    /**
     * Returns the value of an integer literal or a stored variable.
     * @param nameOrLiteral either an integer literal or a variable name
     * @throws MacroException if it's neither
     * a valid integer nor a stored variable.
     */
    public int getValue(final String nameOrLiteral) throws MacroException {
        try {
            return Integer.valueOf(nameOrLiteral);
        } catch (final NumberFormatException ex) {
            return getVariable(nameOrLiteral);
        }
    }
    
    /**
     * Runs this script.
     * Plays the first macro, if any, a specified number of times.
     * @param robot the event generator
     * @param times the number of times to play
     */
    public void play(final Robot robot, final int times) throws MacroException {
        if (macros.length > 0) {
            play(macros[0], robot, times);
        }
    }
    
    /**
     * Plays a named macro a specified number of times.
     * @param robot the event generator
     * @param times the number of times to play
     * @param macroName the name of the macro to play
     * @throws MacroException in particular if the macro wasn't found.
     */
    public void play(final Robot robot, final int times, final String macroName)
            throws MacroException {

        play(getMacro(macroName), robot, times);
    }
    
    private Macro getMacro(final String name) throws MacroException {
        return Arrays.stream(macros)
            .filter(m -> m.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new MacroException("No such macro: " + name));
    }
    
    private void play(final Macro macro, final Robot robot, final int times)
            throws MacroException {
        
        for (int i = 0; i < times; i++) {
            macro.play(this, robot);
        }
    }

}
