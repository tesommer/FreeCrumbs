package freecrumbs.macro.gesture;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

import freecrumbs.macro.Command;
import freecrumbs.macro.Gesture;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

/**
 * Stores the coordinates of an image within a screen capture
 * to script variables.
 * Syntax:
 * {@code
 *  image_xy
 *   x-variable
 *    y-variable
 *     image-file
 *      [occurrence=1,
 *       [delay=0,
 *        [time=1,
 *         [success-macro-name,
 *          [failure-macro-name]]]]]}.
 * Occurrences are counted from the top.
 * Image location relative to script location is supported.
 * If the image was not seen, the variables will be set to -1.
 * 
 * @author Tone Sommerland
 */
public class ImageXY extends Command {
    
    public static final String NAME = "image_xy";

    public ImageXY() {
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
        private final String file;
        private final String occurrence;
        private final String delay;
        private final String times;
        private final String successMacroName;
        private final String failureMacroName;

        public ImageXYParams(final String[] params) {
            this.xVariable = params[0];
            this.yVariable = params[1];
            this.file      = params[2];
            this.occurrence       = paramOrDefault(params, 3, "1");
            this.delay            = paramOrDefault(params, 4, "0");
            this.times            = paramOrDefault(params, 5, "1");
            this.successMacroName = paramOrDefault(params, 6, null);
            this.failureMacroName = paramOrDefault(params, 7, null);
        }
        
        public String getFile(final Script script) {
            final int index = script.getLocation().lastIndexOf(File.separator);
            if (index > -1) {
                final File relative
                    = new File(script.getLocation().substring(0, index), file);
                if (relative.isFile()) {
                    return relative.getPath();
                }
            }
            return file;
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
            script.setVariable(xVariable, x);
            script.setVariable(yVariable, y);
        }
        
        public int[] lookForImageInCapture(
                final Script script,
                final Robot robot,
                final BufferedImage image) throws MacroException {
            
            int[] xy = new int[0];
            int count = 0;
            while (xy.length == 0 && count++ < script.getValue(times)) {
                robot.delay(script.getValue(delay));
                xy = findImageInCapture(
                        image,
                        createScreenCapture(robot),
                        script.getValue(occurrence));
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

            final BufferedImage image = loadImage(params.getFile(script));
            final int[] xy = params.lookForImageInCapture(script, robot, image);
            params.setXYVariables(script, xy);
            params.playResultMacro(script, robot, xy);
        }
    }
    
    private static String paramOrDefault(
            final String[] params,
            final int index,
            final String defaultParam) {
        
        return index < params.length ? params[index] : defaultParam;
    }

    private static BufferedImage loadImage(final String file)
            throws MacroException {
        
        final ImageIcon icon = new ImageIcon(file);
        if (icon.getIconWidth() < 1 || icon.getIconHeight() < 1) {
            throw new MacroException("Image could not be loaded: " + file);
        }
        final BufferedImage image = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        final Graphics g = image.getGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);
        g.dispose();
        return image;
    }

    private static BufferedImage createScreenCapture(final Robot robot) {
        final Dimension screenSize
            = Toolkit.getDefaultToolkit().getScreenSize();
        return robot.createScreenCapture(new Rectangle(screenSize));
    }
    
    /**
     * Returns an empty array if not found.
     */
    private static int[] findImageInCapture(
            final BufferedImage image,
            final BufferedImage capture,
            final int occurrence) {
        
        int count = 0;
        for (int x = 0; x < capture.getWidth() - image.getWidth(); x++) {
            for (int y = 0;
                    y < capture.getHeight() -  image.getHeight(); y++) {
                if (isImageAt(image, capture, x, y)
                        && ++count == occurrence) {
                    return new int[] {x, y};
                }
            }
        }
        return new int[0];
    }

    private static boolean isImageAt(
            final BufferedImage image,
            final BufferedImage capture,
            final int x,
            final int y) {
        
        for (int x2 = 0; x2 < image.getWidth(); x2++) {
            for (int y2 = 0; y2 < image.getHeight(); y2++) {
                final int imageRGB = image.getRGB(x2, y2);
                final int captureRGB = capture.getRGB(x + x2, y + y2);
                if (imageRGB != captureRGB) {
                    return false;
                }
            }
        }
        return true;
    }

}
