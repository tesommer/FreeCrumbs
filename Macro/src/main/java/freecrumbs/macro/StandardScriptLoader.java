package freecrumbs.macro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

public class StandardScriptLoader implements ScriptLoader {
    
    private static final int RECURSION_LIMIT = 2;

    private static final String NAME_PREFIX = "name ";
    
    private final GestureParser[] gestureParsers;

    public StandardScriptLoader(final GestureParser... gestureParsers) {
        this.gestureParsers = gestureParsers.clone();
    }

    @Override
    public Script load(final Reader reader) throws MacroException {
        final Collection<Macro> macros = new ArrayList<>();
        final Collection<Gesture> gestures = new ArrayList<>();
        final BufferedReader bReader = new BufferedReader(reader);
        String macroName = null;
        try {
            String line = bReader.readLine();
            while (line != null) {
                if (line.isEmpty()) {
                    addMacro(macros, gestures, macroName);
                    gestures.clear();
                    macroName = null;
                } else if (line.startsWith(NAME_PREFIX)) {
                    macroName = line.substring(NAME_PREFIX.length()).trim();
                } else {
                    addGesture(gestures, line);
                }
                line = bReader.readLine();
            }
            addMacro(macros, gestures, macroName);
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
        return new Script(macros.stream().toArray(Macro[]::new));
    }

    /**
     * Parses the given line
     * and adds the resulting gesture to the gestures collection.
     */
    private void addGesture(
            final Collection<Gesture> gestures,
            final String line) throws MacroException {
        
        for (final GestureParser parser : gestureParsers) {
            if (parser.supports(line)) {
                gestures.add(parser.parse(line));
                return;
            }
        }
        throw new MacroException(line);
    }

    /**
     * Adds a macro to the macros collection
     * with the given gestures and macro name.
     * Does nothing if gestures is empty.
     * @param macroName the macro name (nullable)
     */
    private static void addMacro(
            final Collection<Macro> macros,
            final Collection<? extends Gesture> gestures,
            final String macroName) {
        
        if (gestures.isEmpty()) {
            return;
        }
        if (macroName == null) {
            macros.add(new Macro(
                    new AtomicPlayCounter(RECURSION_LIMIT),
                    gestures.stream().toArray(Gesture[]::new)));
        } else {
            macros.add(new Macro(
                    new AtomicPlayCounter(RECURSION_LIMIT),
                    macroName,
                    gestures.stream().toArray(Gesture[]::new)));
        }
    }

}
