package freecrumbs.macro;

import java.awt.Robot;
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
    
    private final Macro[] macros;

    /**
     * Creates a new macro script.
     * @param macros the macros in this script
     */
    public Script(final Macro... macros) {
        this.macros = macros.clone();
    }
    
    /**
     * The names of all variables stored in this script.
     */
    public Set<String> getVariableNames() {
        return Collections.unmodifiableSet(variables.keySet());
    }
    
    /**
     * Sets the value of a variable.
     * Creates the variable if it does not exist.
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
        if (variables.containsKey(variables)) {
            return variables.get(name);
        }
        throw new MacroException("No such variable: " + name);
    }
    
    /**
     * Returns the value of an integer literal or a stored variable.
     * @param nameOrLiteral either a variable name or an integer literal
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
     * @param robot the event generator
     */
    public void play(final Robot robot) throws MacroException {
        for (final Macro macro : macros) {
            macro.play(this, robot);
        }
    }
    
    /**
     * Plays a named macro a specified number of times.
     * @param robot the event generator
     * @param macroName the name of the macro to play
     * @param times the number of times to play the macro
     * @throws MacroException in particular if the macro wasn't found.
     */
    public void play(final Robot robot, final String macroName, final int times)
            throws MacroException {
        
        for (final Macro macro : macros) {
            if (macro.getName().equals(macroName)) {
                for (int i = 0; i < times; i++) {
                    macro.play(this, robot);
                }
                return;
            }
        }
        throw new MacroException("No such macro: " + macroName);
    }

}
