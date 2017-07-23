package freecrumbs.macro;

import java.awt.Robot;
import java.util.stream.Stream;

/**
 * A macro script.
 * 
 * @author Tone Sommerland
 */
public class Script {
    private final ScriptVariables variables;
    private final ScriptImages images;
    private final Macro[] macros;

    /**
     * Creates a new macro script.
     * @param scriptFile the location of the script file
     * @param macros the macros in this script
     */
    public Script(final String scriptFile, final Macro... macros) {
        this.variables = new ScriptVariables();
        this.images = new ScriptImages(scriptFile);
        this.macros = macros.clone();
    }
    
    /**
     * Script variables.
     */
    public ScriptVariables variables() {
        return variables;
    }

    /**
     * Script images.
     */
    public ScriptImages images() {
        return images;
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
        return Stream.of(macros)
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
