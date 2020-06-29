package freecrumbs.macro.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.Loader;
import freecrumbs.macro.Macro;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.RecursionGuard;
import freecrumbs.macro.Util;

public final class DefaultLoader implements Loader
{
    private static final int RECURSION_LIMIT = 21;
    
    private static final String NAME_PREFIX = "name";
    private static final String COMMENT_PREFIX = "#";
    
    private final RecursionGuard
    recursionGuard = RecursionGuard.atomic(RECURSION_LIMIT);
    
    private final GestureParser[] gestureParsers;

    public DefaultLoader(final GestureParser... gestureParsers)
    {
        this.gestureParsers = gestureParsers.clone();
    }

    @Override
    public Macro[] load(final InputStream in) throws MacroException
    {
        final var reader = new BufferedReader(new InputStreamReader(in));
        try
        {
            final var macros = new ArrayList<Macro>();
            final var gestures = new ArrayList<Gesture>();
            String macroName = null;
            for (
                    String line = reader.readLine();
                    line != null;
                    line = reader.readLine())
            {
                if (line.trim().isEmpty())
                {
                    addMacro(macroName, gestures, macros);
                    gestures.clear();
                    macroName = null;
                }
                else if (!isComment(line))
                {
                    final String name = macroNameOrNull(line);
                    if (name == null)
                    {
                        addGesture(line, gestures);
                    }
                    else
                    {
                        macroName = name;
                    }
                }
            }
            addMacro(macroName, gestures, macros);
            return macros.stream().toArray(Macro[]::new);
        }
        catch (final IOException ex)
        {
            throw new MacroException(ex);
        }
    }
    
    @Override
    public RecursionGuard recursionGuard()
    {
        return recursionGuard;
    }

    /**
     * Returns null if the given line does not specify the macro name.
     */
    private static String macroNameOrNull(final String line)
    {
        if (Util.isFirstWord(NAME_PREFIX, line))
        {
            return line.trim().substring(NAME_PREFIX.length()).trim();
        }
        return null;
    }
    
    private static boolean isComment(final String line)
    {
        return line.trim().startsWith(COMMENT_PREFIX);
    }

    /**
     * Parses the given line
     * and adds the resulting gesture to the gestures collection.
     */
    private void addGesture(
            final String line,
            final Collection<? super Gesture> gestures) throws MacroException
    {
        for (final GestureParser parser : gestureParsers)
        {
            if (parser.supports(line))
            {
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
            final String macroName,
            final Collection<? extends Gesture> gestures,
            final Collection<? super Macro> macros)
    {
        if (gestures.isEmpty())
        {
            return;
        }
        if (macroName == null)
        {
            macros.add(Macro.nameless(
                    gestures.stream().toArray(Gesture[]::new)));
        }
        else
        {
            macros.add(Macro.named(
                    macroName,
                    gestures.stream().toArray(Gesture[]::new)));
        }
    }

}
