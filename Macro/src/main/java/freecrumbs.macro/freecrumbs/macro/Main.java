package freecrumbs.macro;

import java.awt.AWTException;
import java.awt.Robot;

import freecrumbs.macro.gesture.AddKeyCodeVariables;
import freecrumbs.macro.gesture.Beep;
import freecrumbs.macro.gesture.Delay;
import freecrumbs.macro.gesture.Exit;
import freecrumbs.macro.gesture.Idle;
import freecrumbs.macro.gesture.KeyChord;
import freecrumbs.macro.gesture.KeyPress;
import freecrumbs.macro.gesture.KeyRelease;
import freecrumbs.macro.gesture.Load;
import freecrumbs.macro.gesture.MouseMove;
import freecrumbs.macro.gesture.MousePress;
import freecrumbs.macro.gesture.MouseRelease;
import freecrumbs.macro.gesture.MouseWheel;
import freecrumbs.macro.gesture.Pixel;
import freecrumbs.macro.gesture.Play;
import freecrumbs.macro.gesture.Print;
import freecrumbs.macro.gesture.Scan;
import freecrumbs.macro.gesture.Screenshot;
import freecrumbs.macro.gesture.Set;
import freecrumbs.macro.gesture.Type;
import freecrumbs.macro.gesture.Wait;

/**
 * The entry point to Macro.
 * 
 * @author Tone Sommerland
 */
public final class Main {
    
    private static final String
    HELP
        = "@macrohelp@";

    private static final String TIMES_OPTION = "-t";
    private static final String MACRO_NAME_OPTION = "-m";
    private static final String HELP_OPTION = "-h";
    
    private static final GestureParser[]
    GESTURE_PARSERS
        = new GestureParser[] {
            new AddKeyCodeVariables(),
            new Beep(),
            new Delay(),
            new Exit(),
            new Idle(),
            new KeyPress(),
            new KeyChord(),
            new KeyRelease(),
            new Load(),
            new MouseMove(),
            new MousePress(),
            new MouseRelease(),
            new MouseWheel(),
            new Pixel(),
            new Play(),
            new Print(),
            new Scan(),
            new Screenshot(),
            new Set(),
            new Type(),
            new Wait(),
        };

    private Main() {
    }
    
    public static void main(final String[] args) {
        try {
            final Args parsedArgs = parseArgs(args);
            if (parsedArgs == null) {
                System.out.println(HELP);
                return;
            }
            final Script script = loadScript(parsedArgs);
            play(script, parsedArgs);
        } catch (final MacroException ex) {
            handle(ex);
        }
    }

    private static void handle(final MacroException ex) {
        System.err.println(ex.toString());
    }
    
    private static void play(final Script script, final Args args)
            throws MacroException {
        
        try {
            final Robot robot = new Robot();
            if (args.macroName == null) {
                script.play(robot, args.times);
            } else {
                script.play(robot, args.times, args.macroName);
            }
        } catch (final AWTException ex) {
            throw new MacroException(ex);
        }
    }
    
    private static Script loadScript(final Args args) throws MacroException {
        return new Script(
                ScriptFile.fromFilePath(args.inputFile),
                Loader.getDefault(GESTURE_PARSERS));
    }
    
    /**
     * Returns null if help option or if args contains error.
     */
    private static Args parseArgs(final String[] args) throws MacroException {
        String times = null;
        String macroName = null;
        String inputFile = null;
        int i = -1;
        while (++i < args.length) {
            if (HELP_OPTION.equals(args[i])) {
                return null;
            } else if (TIMES_OPTION.equals(args[i])) {
                if (i == args.length - 1 || times != null) {
                    return null;
                }
                times = args[++i];
            } else if (MACRO_NAME_OPTION.equals(args[i])) {
                if (i == args.length - 1 || macroName != null) {
                    return null;
                }
                macroName = args[++i];
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
            try {
                playTimes = Integer.valueOf(times);
            } catch (final NumberFormatException ex) {
                throw new MacroException(ex);
            }
        }
        return new Args(playTimes, macroName, inputFile);
    }
    
    private static final class Args {
        final int times;
        final String macroName;
        final String inputFile;
        
        public Args(
                final int times,
                final String macroName,
                final String inputFile) {

            this.times = times;
            this.macroName = macroName;
            this.inputFile = inputFile;
        }
    }

}