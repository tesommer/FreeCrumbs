package freecrumbs.macro.gesture;

import static java.util.Objects.requireNonNull;

import java.awt.Robot;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
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
 * {@code script-file->macro-name}.
 * Input may be passed to the script like this:
 * {@code script-file:variable1=value1:variable2=value2->macro-name}.
 * The variable-value pairs will be set as script variables
 * in the external script.
 * 
 * @author Tone Sommerland
 */
public final class Play extends Command {
    
    public static final GestureParser INSTANCE = new Play();
    
    public static final String NAME = "play";
    
    private static final String
    SCRIPT_MACRO_SEPARATOR = "->";
    
    private static final String
    SCRIPT_INPUT_SEPARATOR = ":";
    
    private static final String
    VARIABLE_VALUE_SEPARATOR = "=";
    
    private static final String
    INPUT_DELIMITER = SCRIPT_INPUT_SEPARATOR + "|" + VARIABLE_VALUE_SEPARATOR;
    
    private Play() {
        super(NAME, 1, 5);
    }

    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        if (params.length == 3 || params.length == 4) {
            throw new MacroException("Syntax error: " + line);
        }
        return (script, robot) -> play(script, robot, params);
    }

    private static void play(
            final Script script,
            final Robot robot,
            final String[] params) throws MacroException {
        
        final String times = paramOrDefault(params, 1, "1");
        final MacroSpecifier macroSpecifier
            = getMacroSpecifier(script, params[0]);
        if (params.length != 5 || Util.evaluateLogical(
                script, params[2], params[3], params[4])) {
            macroSpecifier.play(script, robot, times);
        }
    }
    
    private static MacroSpecifier getMacroSpecifier(
            final Script current,
            final String macroParam) throws MacroException {
        
        final int index = macroParam.lastIndexOf(SCRIPT_MACRO_SEPARATOR);
        if (index < 0) {
            return new MacroSpecifier(macroParam);
        }
        final String macroName
            = macroParam.substring(index + SCRIPT_MACRO_SEPARATOR.length());
        final String scriptParam = macroParam.substring(0, index);
        return new MacroSpecifier(
                macroName, new ScriptSpecifier(current, scriptParam));
    }
    
    private static final class ScriptSpecifier {
        private final Map<String, Integer> input = new HashMap<>();
        private final String scriptLocation;

        public ScriptSpecifier(final Script current, final String scriptParam)
                throws MacroException {
            
            final int index = scriptParam.indexOf(SCRIPT_INPUT_SEPARATOR);
            if (index < 0) {
                scriptLocation = scriptParam;
            } else {
                scriptLocation = scriptParam.substring(0, index);
                extractInput(current, scriptParam, index);
            }
        }

        private void extractInput(
                final Script current,
                final String scriptParam,
                final int index) throws MacroException {
            
            try (final var scanner
                    = new Scanner(scriptParam.substring(index))) {
                try (final Scanner delimited
                        = scanner.useDelimiter(INPUT_DELIMITER)) {
                    extractInput(current, scriptParam, delimited);
                }
            }
        }

        private void extractInput(
                final Script current,
                final String scriptParam,
                final Scanner scanner) throws MacroException {
            
            while (scanner.hasNext()) {
                final String variable = scanner.next();
                if (!scanner.hasNext()) {
                    throw new MacroException(scriptParam);
                }
                input.put(
                        variable,
                        current.variables().value(scanner.next()));
            }
        }

        public String getScriptLocation() {
            return scriptLocation;
        }
        
        public void setInputVariables(final Script script) {
            for (final String variable : input.keySet()) {
                script.variables().set(variable, input.get(variable));
            }
        }
        
    }
    
    /**
     * Specifies a macro in the current script or another script.
     */
    private static final class MacroSpecifier {
        private final String macroName;
        private final ScriptSpecifier scriptSpecifier;
        
        /**
         * Creates a specifier for a macro in another script.
         * @param macroName the macro name
         * @param scriptSpecifier the script location and input (nullable)
         */
        public MacroSpecifier(
                final String macroName, final ScriptSpecifier scriptSpecifier) {
            
            this.macroName = requireNonNull(macroName, "macroName");
            this.scriptSpecifier = scriptSpecifier;
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
                script.play(robot, current.variables().value(times));
            } else {
                script.play(robot, current.variables().value(times),
                        macroName);
            }
        }
        
        private Script getScript(final Script current) throws MacroException {
            if (scriptSpecifier == null) {
                return current;
            }
            final Script script = Script.load(
                    current.location()
                        .refer(scriptSpecifier.getScriptLocation()),
                    current.loader());
            scriptSpecifier.setInputVariables(script);
            return script;
        }
    }

}
