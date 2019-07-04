package freecrumbs.macro.gesture;

import java.awt.Robot;
import java.awt.image.BufferedImage;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Scanner;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;

/**
 * Stores the coordinates of an image within a screen capture
 * to script variables.
 * Syntax:
 * {@code
 *  scan
 *   from-x
 *    from-y
 *     to-x
 *      to-y
 *       x-variable
 *        y-variable
 *         image
 *          [occurrence=1,
 *           [delay=0,
 *            [times=1,
 *             [success-macro-name,
 *              [failure-macro-name]]]]]}.
 * The from/to parameters may be -1 to disregard them.
 * If they are out of bounds, they are restricted automatically.
 * Occurrences are counted from the top.
 * If the image was not seen, the variables will be set to -1.
 * 
 * @author Tone Sommerland
 */
public final class Scan extends Command {
    
    public static final GestureParser INSTANCE = new Scan();
    
    public static final String NAME = "scan";

    private Scan() {
        super(NAME, 7, 12);
    }
    
    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return new ScanGesture(new ScanParams(params));
    }

    private static final class ScanGesture implements Gesture {
        private final ScanParams params;
        
        public ScanGesture(final ScanParams params) {
            this.params = params;
        }
        
        @Override
        public void play(final Script script, final Robot robot)
                throws MacroException {

            final BufferedImage image = params.getImage(script);
            final int[] xy = params.scanScreenForImage(script, robot, image);
            params.setXYVariables(script, xy);
            params.playResultMacro(script, robot, xy);
        }
    }
    
    private static final class FromToParams {
        private final String fromX;
        private final String fromY;
        private final String toX;
        private final String toY;
        
        public FromToParams(final String[] params, final int firstIndex) {
            this.fromX = params[firstIndex];
            this.fromY = params[firstIndex + 1];
            this.toX =   params[firstIndex + 2];
            this.toY =   params[firstIndex + 3];
        }
        
        public Scanner getScanner(
                final Script script,
                final Robot robot) throws MacroException {
            
            return new Scanner(
                    Util.createScreenCapture(robot),
                    script.variables().value(fromX),
                    script.variables().value(fromY),
                    script.variables().value(toX),
                    script.variables().value(toY));
        }
    }
    
    private static final class VariableParams {
        private final String xVariable;
        private final String yVariable;
        
        public VariableParams(final String[] params, final int firstIndex) {
            this.xVariable = params[firstIndex];
            this.yVariable = params[firstIndex + 1];
        }

        public void setXYVariables(final Script script, final int[] xy) {
            final int x;
            final int y;
            if (xy.length == 2) {
                x = xy[0];
                y = xy[1];
            } else {
                x = -1;
                y = -1;
            }
            script.variables().set(xVariable, x);
            script.variables().set(yVariable, y);
        }
    }
    
    private static final class ResultMacroParams {
        private final String successMacroName;
        private final String failureMacroName;
        
        public ResultMacroParams(final String[] params, final int firstIndex) {
            this.successMacroName = paramOrDefault(params, firstIndex, null);
            this.failureMacroName
                = paramOrDefault(params, firstIndex + 1, null);
        }
        
        public void playResultMacro(
                final Script script,
                final Robot robot,
                final int[] xy) throws MacroException {
            
            if (xy.length == 0) {
                if (failureMacroName != null) {
                    script.play(robot, 1, failureMacroName);
                }
            } else if (successMacroName != null) {
                script.play(robot, 1, successMacroName);
            }
        }
    }
    
    private static final class ScanParams {
        private final FromToParams fromToParams;
        private final VariableParams variableParams;
        private final ResultMacroParams resultMacroParams;
        private final String image;
        private final String occurrence;
        private final String delay;
        private final String times;

        public ScanParams(final String[] params) {
            this.fromToParams = new FromToParams(params, 0);
            this.variableParams = new VariableParams(params, 4);
            this.resultMacroParams = new ResultMacroParams(params, 10);
            this.image      = params[6];
            this.occurrence       = paramOrDefault(params, 7, "1");
            this.delay            = paramOrDefault(params, 8, "0");
            this.times            = paramOrDefault(params, 9, "1");
        }

        public BufferedImage getImage(final Script script)
                throws MacroException {
            
            return script.images().getOrLoad(image);
        }
        
        public int[] scanScreenForImage(
                final Script script,
                final Robot robot,
                final BufferedImage image) throws MacroException {
            
            int[] xy = new int[0];
            int time = 0;
            while (xy.length == 0
                    && time++ < script.variables().value(times)) {
                robot.delay(script.variables().value(delay));
                xy = fromToParams.getScanner(script, robot)
                    .xyOf(image, script.variables().value(occurrence));
            }
            return xy;
        }
        
        public void setXYVariables(final Script script, final int[] xy) {
            variableParams.setXYVariables(script, xy);
        }
        
        public void playResultMacro(
                final Script script,
                final Robot robot,
                final int[] xy) throws MacroException {
            
            resultMacroParams.playResultMacro(script, robot, xy);
        }
        
    }

}
