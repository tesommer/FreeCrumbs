package freecrumbs.macro.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import freecrumbs.macro.AtomicRecursionGuard;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.Loader;
import freecrumbs.macro.Macro;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Util;

public class DefaultLoader implements Loader {
    
    private static final int RECURSION_LIMIT = 20;
    private static final String NAME_PREFIX = "name";
    private static final String COMMENT_PREFIX = "#";
    
    private final GestureParser[] gestureParsers;

    public DefaultLoader(final GestureParser... gestureParsers) {
        this.gestureParsers = gestureParsers.clone();
    }

    @Override
    public Macro[] load(final InputStream in) throws MacroException {
        final BufferedReader reader
            = new BufferedReader(new InputStreamReader(in));
        try {
            final Collection<Macro> macros = new ArrayList<>();
            final Collection<Gesture> gestures = new ArrayList<>();
            String macroName = null;
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().isEmpty()) {
                    addMacro(macros, gestures, macroName);
                    gestures.clear();
                    macroName = null;
                } else if (!isComment(line)) {
                    final String name = getMacroName(line);
                    if (name == null) {
                        addGesture(gestures, line);
                    } else {
                        macroName = name;
                    }
                }
                line = reader.readLine();
            }
            addMacro(macros, gestures, macroName);
            return macros.stream().toArray(Macro[]::new);
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
    }
    
    /**
     * Returns null if the given line does not specify the macro name.
     */
    private static String getMacroName(final String line) {
        if (Util.isFirstWord(NAME_PREFIX, line)) {
            return line.trim().substring(NAME_PREFIX.length()).trim();
        }
        return null;
    }
    
    private static boolean isComment(final String line) {
        return line.trim().startsWith(COMMENT_PREFIX);
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
                    new AtomicRecursionGuard(RECURSION_LIMIT),
                    gestures.stream().toArray(Gesture[]::new)));
        } else {
            macros.add(new Macro(
                    new AtomicRecursionGuard(RECURSION_LIMIT),
                    macroName,
                    gestures.stream().toArray(Gesture[]::new)));
        }
    }

}
