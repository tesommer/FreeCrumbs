package freecrumbs.macro.main;

import java.awt.AWTException;
import java.awt.Robot;

import freecrumbs.macro.GestureParser;
import freecrumbs.macro.Loader;
import freecrumbs.macro.Location;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;
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
public final class Main
{
    private static final String
    HELP
        = "@macrohelp@";

    private static final String TIMES_OPTION = "-t";
    private static final String MACRO_NAME_OPTION = "-m";
    private static final String HELP_OPTION = "-h";
    
    private static final GestureParser[]
    GESTURE_PARSERS = new GestureParser[]
        {
                AddKeyCodeVariables.INSTANCE,
                Beep.INSTANCE,
                Delay.INSTANCE,
                Exit.INSTANCE,
                Idle.INSTANCE,
                KeyPress.INSTANCE,
                KeyChord.INSTANCE,
                KeyRelease.INSTANCE,
                Load.INSTANCE,
                MouseMove.INSTANCE,
                MousePress.INSTANCE,
                MouseRelease.INSTANCE,
                MouseWheel.INSTANCE,
                Pixel.INSTANCE,
                Play.INSTANCE,
                Print.INSTANCE,
                Scan.INSTANCE,
                Screenshot.INSTANCE,
                Set.INSTANCE,
                Type.INSTANCE,
                Wait.INSTANCE,
        };

    private Main()
    {
    }
    
    public static void main(final String[] args)
    {
        try
        {
            final Args parsedArgs = parseArgs(args);
            if (parsedArgs == null)
            {
                System.out.println(HELP);
                return;
            }
            final Script script = loadScript(parsedArgs);
            play(script, parsedArgs);
        }
        catch (final MacroException ex)
        {
            handle(ex);
        }
    }

    private static void handle(final MacroException ex)
    {
        System.err.println(ex.toString());
    }
    
    private static void play(final Script script, final Args args)
            throws MacroException
    {
        try
        {
            final var robot = new Robot();
            if (args.macroName == null)
            {
                script.play(robot, args.times);
            }
            else
            {
                script.play(robot, args.times, args.macroName);
            }
        }
        catch (final AWTException ex)
        {
            throw new MacroException(ex);
        }
    }
    
    private static Script loadScript(final Args args) throws MacroException
    {
        return Script.load(
                Location.fromFilePath(args.inputFile),
                Loader.supporting(GESTURE_PARSERS));
    }
    
    /**
     * Returns null if help option or if args contains error.
     */
    private static Args parseArgs(final String[] args) throws MacroException
    {
        String times = null;
        String macroName = null;
        String inputFile = null;
        int i = -1;
        while (++i < args.length)
        {
            if (HELP_OPTION.equals(args[i]))
            {
                return null;
            }
            else if (TIMES_OPTION.equals(args[i]))
            {
                if (i == args.length - 1 || times != null)
                {
                    return null;
                }
                times = args[++i];
            }
            else if (MACRO_NAME_OPTION.equals(args[i]))
            {
                if (i == args.length - 1 || macroName != null)
                {
                    return null;
                }
                macroName = args[++i];
            }
            else
            {
                if (i != args.length - 1)
                {
                    return null;
                }
                inputFile = args[i];
            }
        }
        if (inputFile == null)
        {
            return null;
        }
        final int playTimes;
        if (times == null)
        {
            playTimes = 1;
        }
        else
        {
            try
            {
                playTimes = Integer.parseInt(times);
            }
            catch (final NumberFormatException ex)
            {
                throw new MacroException(ex);
            }
        }
        return new Args(playTimes, macroName, inputFile);
    }
    
    private static final class Args
    {
        final int times;
        final String macroName;
        final String inputFile;
        
        Args(
                final int times,
                final String macroName,
                final String inputFile)
        {
            assert inputFile != null;
            this.times = times;
            this.macroName = macroName;
            this.inputFile = inputFile;
        }
    }

}
