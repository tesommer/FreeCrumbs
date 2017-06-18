package freecrumbs.macro.gesture;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Macros;
import freecrumbs.macro.Script;

/**
 * Stores the coordinates of an image within the current screen capture
 * to script variables.
 * Syntax:
 * {@code image_xy x-variable y-variable occurrence image-file}.
 * Occurrences are counted from the top.
 * The first occurrence has number one.
 * Image location relative to script location is supported.
 * If the image was not on screen, the variables will be set to -1.
 * 
 * @author Tone Sommerland
 */
public class ImageXY implements GestureParser {
    
    public static final String NAME = "image_xy";

    public ImageXY() {
    }

    @Override
    public boolean supports(final String line) {
        return Macros.isFirstWord(NAME, line);
    }

    @Override
    public Gesture parse(final String line) throws MacroException {
        final String parts[] = Macros.split(line, 5);
        if (parts.length != 5) {
            throw new MacroException("Syntax error: " + line);
        }
        return new ImageXYGesture(parts[1], parts[2], parts[3], parts[4]);
    }
    
    private static final class ImageXYGesture implements Gesture {
        private final String xVariable;
        private final String yVariable;
        private final String occurrence;
        private final String file;
        
        public ImageXYGesture(
                final String xVariable,
                final String yVariable,
                final String occurrence,
                final String file) {
            
            this.xVariable = xVariable;
            this.yVariable = yVariable;
            this.occurrence = occurrence;
            this.file = file;
        }
        
        @Override
        public void play(final Script script, final Robot robot)
                throws MacroException {

            final BufferedImage image = loadImage(script, file);
            final Dimension screenSize
                = Toolkit.getDefaultToolkit().getScreenSize();
            final BufferedImage capture
                = robot.createScreenCapture(new Rectangle(screenSize));
            final int[] xy = findImageInCapture(
                    image, capture, script.getValue(occurrence));
            setXYVariables(script, xy);
        }

        private void setXYVariables(final Script script, final int[] xy) {
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

        private static BufferedImage loadImage(
                final Script script, final String file) throws MacroException {
            
            final ImageIcon icon
                = new ImageIcon(getFile(script.getLocation(), file));
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
        
        private static String getFile(
                final String scriptFile, final String file) {
            
            final int index = scriptFile.lastIndexOf(File.separator);
            if (index > -1) {
                final File relative
                    = new File(scriptFile.substring(0, index), file);
                if (relative.isFile()) {
                    return relative.getPath();
                }
            }
            return file;
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

}
