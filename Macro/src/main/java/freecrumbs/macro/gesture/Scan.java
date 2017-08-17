package freecrumbs.macro.gesture;

import java.awt.Robot;
import java.awt.image.BufferedImage;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
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
 *   x-variable
 *    y-variable
 *     image
 *      [occurrence=1,
 *       [delay=0,
 *        [times=1,
 *         [success-macro-name,
 *          [failure-macro-name]]]]]}.
 * Occurrences are counted from the top.
 * If the image was not seen, the variables will be set to -1.
 * 
 * @author Tone Sommerland
 */
public class Scan extends Command {
    
    public static final String NAME = "scan";

    public Scan() {
        super(NAME, 3, 8);
    }
    
    @Override
    protected Gesture getGesture(final String line, final String[] params)
            throws MacroException {
        
        return new ImageXYGesture(new ImageXYParams(params));
    }
    
    private static final class ImageXYParams {
        private final String xVariable;
        private final String yVariable;
        private final String image;
        private final String occurrence;
        private final String delay;
        private final String times;
        private final String successMacroName;
        private final String failureMacroName;

        public ImageXYParams(final String[] params) {
            this.xVariable = params[0];
            this.yVariable = params[1];
            this.image      = params[2];
            this.occurrence       = paramOrDefault(params, 3, "1");
            this.delay            = paramOrDefault(params, 4, "0");
            this.times            = paramOrDefault(params, 5, "1");
            this.successMacroName = paramOrDefault(params, 6, null);
            this.failureMacroName = paramOrDefault(params, 7, null);
        }
        
        public BufferedImage getImage(final Script script)
                throws MacroException {
            
            return script.getImages().getOrLoad(image);
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
            script.getVariables().set(xVariable, x);
            script.getVariables().set(yVariable, y);
        }
        
        public int[] scanScreenForImage(
                final Script script,
                final Robot robot,
                final BufferedImage image) throws MacroException {
            
            int[] xy = new int[0];
            int time = 0;
            while (xy.length == 0
                    && time++ < script.getVariables().valueOf(times)) {
                robot.delay(script.getVariables().valueOf(delay));
                xy = new Scanner(Util.createScreenCapture(robot))
                    .xyOf(image, script.getVariables().valueOf(occurrence));
            }
            return xy;
        }
        
    }

    private static final class ImageXYGesture implements Gesture {
        private final ImageXYParams params;
        
        public ImageXYGesture(final ImageXYParams params) {
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

}
