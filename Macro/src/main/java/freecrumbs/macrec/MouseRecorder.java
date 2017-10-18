package freecrumbs.macrec;

import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import freecrumbs.macro.MacroException;
import freecrumbs.macro.gesture.MouseMove;
import freecrumbs.macro.gesture.MousePress;
import freecrumbs.macro.gesture.MouseRelease;

/**
 * This frame records mouse gestures to the receiver.
 * There are four modes activated by pressing K, P, C and S:
 * <ul>
 * <li>P: record mouse button press/release</li>
 * <li>M: record mouse move</li>
 * <li>C: record screen capture</li>
 * <li>S: show coordinates of mouse presses</li>
 * </ul>
 * When in the C mode,
 * one presses two points to select a sub-image,
 * which is then saved to a PNG file in the current directory.
 * Pressing escape exits.
 * 
 * @author Tone Sommerland
 */
public class MouseRecorder extends ScreenCaptureFrame {
    
    private static final long serialVersionUID = 1L;
    
    private static final Logger
    LOGGER = Logger.getLogger(MouseRecorder.class.getName());
    
    private static final Color
    CHECKER_PATTERN_COLOR = new Color(0, 0, 121, 11);
    
    private static final int
    CHECKER_SQUARE_WIDTH = 26;
    
    private static final Color
    MARK_COLOR = Color.RED;
    
    private static final int
    MARK_LENGTH = 7;
    
    private static final Point
    TEXT_POINT  = new Point(32, 32);
    
    private static final Color
    TEXT_COLOR = new Color(198, 83, 255, 200);
    
    private static final Font
    TEXT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    
    private static final int
    TEXT_LINE_HEIGHT = 32;
    
    private static final String
    TEXT_FORMAT = "MODE: {0}\nSWITCH MODE: [P|M|C|S]\nEXIT: [Esc]";

    private static final String
    CAPTURE_FILENAME_EXTENSION = "png";
    
    private static final String
    CAPTURE_FILENAME_FORMAT = "capture{0}." + CAPTURE_FILENAME_EXTENSION;
    
    private static enum State {
        RECORDING_PRESS_RELEASE,
        RECORDING_MOVE,
        RECORDING_CAPTURE,
        SHOWING_XY,
    }

    private final MarkLayer
    markLayer = new MarkLayer();
    
    private final TextLayer
    textLayer
        = new TextLayer(TEXT_POINT, TEXT_COLOR, TEXT_FONT, TEXT_LINE_HEIGHT);
    
    private final Consumer<? super String> receiver;
    private State state = State.RECORDING_PRESS_RELEASE;

    public MouseRecorder(final Consumer<? super String> receiver)
            throws MacroException {
        
        this.receiver = requireNonNull(receiver, "receiver");
        final LayeredPane layeredPane
            = new LayeredPane(
                    new ImageLayer(screenCapture),
                    new CheckerLayer(
                            CHECKER_PATTERN_COLOR, CHECKER_SQUARE_WIDTH),
                    markLayer,
                    textLayer);
        getContentPane().add(layeredPane);
        setState(state);
    }

    @Override
    public void keyPressed(final KeyEvent evt) {
        clearMarks();
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (evt.getKeyCode() == KeyEvent.VK_P) {
            setState(State.RECORDING_PRESS_RELEASE);
        } else if (evt.getKeyCode() == KeyEvent.VK_M) {
            setState(State.RECORDING_MOVE);
        } else if (evt.getKeyCode() == KeyEvent.VK_C) {
            setState(State.RECORDING_CAPTURE);
        } else if (evt.getKeyCode() == KeyEvent.VK_S) {
            setState(State.SHOWING_XY);
        } else {
            textLayer.setText("");
        }
    }

    @Override
    public void mousePressed(final MouseEvent evt) {
        if (state == State.RECORDING_PRESS_RELEASE) {
            recordPress(evt);
        } else if (state == State.RECORDING_MOVE) {
            recordMove(evt);
        } else if (state == State.RECORDING_CAPTURE) {
            recordCapture(evt);
        } else if (state == State.SHOWING_XY) {
            showXY(evt);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent evt) {
        if (state == State.RECORDING_PRESS_RELEASE) {
            recordRelease(evt);
        }
    }
    
    private void setState(final State state) {
        this.state = state;
        final String text = MessageFormat.format(TEXT_FORMAT, state.name());
        textLayer.setText(text);
        repaint();
    }

    private void addMark(final MouseEvent evt) {
        markLayer.addMark(
                evt.getPoint(),
                MARK_COLOR,
                MARK_LENGTH);
        repaint();
    }

    private void clearMarks() {
        markLayer.clearMarks();
        repaint();
    }

    private void recordPress(final MouseEvent evt) {
        receiver.accept(MousePress.NAME + " " + evt.getButton());
        clearMarks();
        addMark(evt);
    }

    private void recordRelease(final MouseEvent evt) {
        receiver.accept(MouseRelease.NAME + " " + evt.getButton());
    }

    private void recordMove(final MouseEvent evt) {
        receiver.accept(
                MouseMove.NAME + " " + evt.getX() + " " + evt.getY());
        addMark(evt);
    }

    private void recordCapture(final MouseEvent evt) {
        addMark(evt);
        if (markLayer.getMarkCount() == 2) {
            final Point point1 = markLayer.getMarkPoint(0);
            final Point point2 = markLayer.getMarkPoint(1);
            final BufferedImage image = copyCapture(point1, point2);
            try {
                saveImage(image, generateCaptureFilename());
            } catch (final IOException ex) {
                LOGGER.warning(ex.toString());
            }
            clearMarks();
        }
    }
    
    private void showXY(final MouseEvent evt) {
        final String coordinates = "(" + evt.getX() + ", " + evt.getY() + ")";
        receiver.accept(coordinates);
        textLayer.setText(coordinates);
        repaint();
    }

    private BufferedImage copyCapture(final Point point1, final Point point2) {
        final int x = Math.min(point1.x, point2.x);
        final int y = Math.min(point1.y, point2.y);
        final int width = Math.abs(point2.x - point1.x);
        final int height = Math.abs(point2.y - point1.y);
        final BufferedImage image
            = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final int[] rgbArray = new int[width * height];
        screenCapture.getRGB(x, y, width, height, rgbArray, 0, width);
        image.setRGB(0, 0, width, height, rgbArray, 0, width);
        return image;
    }

    private static String generateCaptureFilename() {
        int number = 0;
        String filename = MessageFormat.format(
                CAPTURE_FILENAME_FORMAT, number);
        while (new File(filename).exists()) {
            filename = MessageFormat.format(
                    CAPTURE_FILENAME_FORMAT, ++number);
        }
        return filename;
    }

    private static void saveImage(
            final BufferedImage image,
            final String filename) throws IOException {
        
        final Iterator<ImageWriter> it
            = ImageIO.getImageWritersBySuffix(CAPTURE_FILENAME_EXTENSION);
        if (it.hasNext()) {
            final ImageWriter writer = it.next();
            writer.setOutput(new FileImageOutputStream(new File(filename)));
            writer.write(image);
        }
    }

}
