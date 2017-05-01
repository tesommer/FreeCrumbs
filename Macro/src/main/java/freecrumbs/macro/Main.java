package freecrumbs.macro;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import freecrumbs.macro.gesture.Delay;
import freecrumbs.macro.gesture.KeyPress;
import freecrumbs.macro.gesture.KeyRelease;
import freecrumbs.macro.gesture.Play;
import freecrumbs.macro.gesture.Print;

/**
 * The entry point to Macro.
 * 
 * @author Tone Sommerland
 */
public final class Main {
    
    private static final String
    HELP
        = "@macrohelp@";
    
    private static final String MACRO_NAME_OPTION = "-m";
    private static final String TIMES_OPTION = "-t";
    private static final String HELP_OPTION = "-h";
    
    private static final GestureParser[]
    GESTURE_PARSERS
        = new GestureParser[] {
            new Delay(),
            new KeyPress(),
            new KeyRelease(),
            new Play(),
            new Print(),
        };

    private Main() {
    }
    
    public static void main(final String[] args) throws MacroException {
        final Args parsedArgs = parseArgs(args);
        if (parsedArgs == null) {
            System.out.println(HELP);
            return;
        }
        final Script script = loadScript(parsedArgs);
        play(script, parsedArgs);
    }
    
    private static void play(final Script script, final Args args)
            throws MacroException {
        
        try {
            final Robot robot = new Robot();
            if (args.macroName == null) {
                script.play(robot);
            } else {
                script.play(robot, args.macroName, args.times);
            }
        } catch (final AWTException|SecurityException ex) {
            throw new MacroException(ex);
        }
    }
    
    private static Script loadScript(final Args args) throws MacroException {
        final ScriptLoader loader = new StandardScriptLoader(GESTURE_PARSERS);
        try (
            final Reader reader
                = new InputStreamReader(new FileInputStream(args.inputFile));
        ) {
            return loader.load(reader);
        } catch (final IOException ex) {
            throw new MacroException(ex);
        }
    }
    
    /**
     * Returns null if help option or if args contains error.
     */
    private static Args parseArgs(final String[] args) throws MacroException {
        String macroName = null;
        String times = null;
        String inputFile = null;
        int i = -1;
        while (++i < args.length) {
            if (HELP_OPTION.equals(args[i])) {
                return null;
            } else if (MACRO_NAME_OPTION.equals(args[i])) {
                if (i == args.length - 1 || macroName != null) {
                    return null;
                }
                macroName = args[++i];
            } else if (TIMES_OPTION.equals(args[i])) {
                if (i == args.length - 1 || times != null) {
                    return null;
                }
                times = args[++i];
            } else {
                if (i != args.length - 1) {
                    return null;
                }
                inputFile = args[i];
            }
        }
        if (inputFile == null) {
            return null;
        }
        final int playTimes;
        if (times == null) {
            playTimes = 1;
        } else {
            if (macroName == null) {
                return null;
            }
            try {
                playTimes = Integer.valueOf(times);
            } catch (final NumberFormatException ex) {
                throw new MacroException(ex);
            }
        }
        return new Args(macroName, playTimes, inputFile);
    }
    
    private static final class Args {
        final String macroName;
        final int times;
        final String inputFile;
        
        public Args(
                final String macroName,
                final int times,
                final String inputFile) {
            
            this.macroName = macroName;
            this.times = times;
            this.inputFile = inputFile;
        }
    }

}
