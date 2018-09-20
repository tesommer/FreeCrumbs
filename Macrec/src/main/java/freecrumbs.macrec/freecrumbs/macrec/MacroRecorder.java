package freecrumbs.macrec;

import freecrumbs.macro.MacroException;

/**
 * This program is a utility for creating macro scripts.
 * 
 * @author Tone Sommerland
 */
public final class MacroRecorder {
    
    private static final String
    HELP
        = "@macrechelp@";
    
    private static final String KEY_REC_OPTION = "-k";
    private static final String MOUSE_REC_OPTION = "-m";

    private MacroRecorder() {
    }
    
    public static void main(final String[] args) throws MacroException {
        if (args.length == 0) {
            printHelp();
        } else if (KEY_REC_OPTION.equals(args[0]) && args.length == 1) {
            startKeyRecorder();
        } else if (MOUSE_REC_OPTION.equals(args[0]) && args.length == 2) {
            try {
                final long millis = Long.parseLong(args[1]);
                Thread.sleep(millis);
                startMouseRecorder();
            } catch (final NumberFormatException ex) {
                printHelp();
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        } else {
            printHelp();
        }
    }
    
    private static void printHelp() {
        System.out.println(HELP);
    }
    
    private static void startKeyRecorder() {
        new KeyRecorder(System.out::println).setVisible(true);
    }
    
    private static void startMouseRecorder() throws MacroException {
        new MouseRecorder(System.out::println).setVisible(true);
    }

}
