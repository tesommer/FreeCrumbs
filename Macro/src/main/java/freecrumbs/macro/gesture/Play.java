package freecrumbs.macro.gesture;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

/**
 * Plays a named macro a certain number of times (default is one).
 * Syntax:
 * <ul>
 * <li>{@code play macro [times=1]}</li>
 * <li>{@code play macro [times=1 left operator right]}:
 * Plays the macro if a logical expression is true,
 * e.g.: {@code play macro-name 1 x == y}.
 * Supported operators: {@code == != <= >= < >}</li>
 * </ul>
 * The {@code macro} parameter
 * may be the name of a macro in the current script,
 * or a macro in another script.
 * In the latter case, the parameter format is
 * {@code script-file?macro-name}.
 * 
 * @author Tone Sommerland
 */
public class Play extends Command {
    
    public static final String NAME = "play";
    
    private static final String SEPARATOR = "->";
    
    public Play() {
        super(NAME, 1, 5);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        if (params.length == 3 || params.length == 4) {
            throw new MacroException("Syntax error: " + line);
        }
        final MacroSpecifier macroSpecifier = getMacroSpecifier(params[0]);
        final String times = paramOrDefault(params, 1, "1");
        return (script, robot) -> {
            if (params.length != 5 || Util.evaluateLogical(
                    script, params[2], params[3], params[4])) {
                macroSpecifier.play(script, robot, times);
            }
        };
    }
    
    /**
     * Specifies a macro in the current script or another script.
     */
    private static final class MacroSpecifier {
        private final String macroName;
        private final String scriptLocation;
        
        /**
         * Creates a specifier for a macro in another script.
         * @param macroName the macro name
         * @param scriptLocation the script location (nullable)
         */
        public MacroSpecifier(
                final String macroName, final String scriptLocation) {
            
            this.macroName = requireNonNull(macroName, "macroName");
            this.scriptLocation = scriptLocation;
        }
        
        /**
         * Creates a specifier for a macro in the current script.
         * @param macroName the macro name
         */
        public MacroSpecifier(final String macroName) {
            this(macroName, null);
        }
        
        public void play(
                final Script current,
                final Robot robot,
                final String times) throws MacroException {
            
            final Script script = getScript(current);
            if (macroName.isEmpty()) {
                script.play(robot, current.getVariables().valueOf(times));
            } else {
                script.play(robot, current.getVariables().valueOf(times),
                        macroName);
            }
        }
        
        private Script getScript(final Script current) throws MacroException {
            if (scriptLocation == null) {
                return current;
            }
            return new Script(
                    current.getLocation().refer(scriptLocation),
                    current.getLoader());
        }
    }
    
    private static MacroSpecifier getMacroSpecifier(final String macroParam) {
        final int index = macroParam.lastIndexOf(SEPARATOR);
        if (index < 0) {
            return new MacroSpecifier(macroParam);
        }
        final String macroName
            = macroParam.substring(index + SEPARATOR.length());
        final String scriptLocation = macroParam.substring(0, index);
        return new MacroSpecifier(macroName, scriptLocation);
    }

}
