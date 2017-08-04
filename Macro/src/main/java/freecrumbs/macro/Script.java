package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * A macro script.
 * 
 * @author Tone Sommerland
 */
public class Script {
    private final Location location;
    private final Loader loader;
    private final ScriptVariables variables;
    private final ScriptImages images;
    private final Macro[] macros;
    
    /**
     * Creates a new macro script.
     * @param location the location to open
     * @param loader the loader that loads macros
     */
    public Script(final Location location, final Loader loader)
            throws MacroException {

        this.location = requireNonNull(location, "location");
        this.loader = requireNonNull(loader, "loader");
        this.variables = new ScriptVariables();
        this.images = new ScriptImages(location);
        try (final InputStream in = location.open()) {
            this.macros = loader.load(in);
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
    }

    /**
     * The location of this script.
     */
    public Location location() {
        return location;
    }
    
    /**
     * The loader of this script.
     */
    public Loader loader() {
        return loader;
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
