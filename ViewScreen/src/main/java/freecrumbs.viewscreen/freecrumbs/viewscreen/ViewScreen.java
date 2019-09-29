package freecrumbs.viewscreen;

import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class must be accessed from the event-dispatch thread.
 * 
 * @author Tone Sommerland
 */
public final class ViewScreen {
    private final JFrame frame = new JFrame();
    private final List<Buffer> buffers = new ArrayList<>();
    private final Drawing drawing = new Drawing();

    public ViewScreen() {
        frame.setContentPane(new BufferPanel());
        // TODO figure out if it's gonna be fullscreen, decorated, maximized and shit
        frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /******************
     * Frame & buffer *
     ******************/
    
    public void upload(final String variable, final byte[] data) {
        final Image image = new ImageIcon(data).getImage();
        final var buffered = new BufferedImage(
                image.getWidth(frame),
                image.getHeight(frame),
                BufferedImage.TYPE_INT_ARGB);
        final Graphics g = buffered.getGraphics();
        g.drawImage(image, 0, 0, frame);
        g.dispose();
        buffers.add(new Buffer(buffered, requireNonNull(variable, "variable")));
    }
    
    public void makeBuffer(
            final String variable, final int width, final int height) {
        
        buffers.add(new Buffer(
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB),
                requireNonNull(variable, "variable")));
    }
    
    public void removeBuffer(final String variable) throws IOException {
        final int index = getBufferIndex(variable);
        buffers.remove(index);
    }
    
    public void setPosition(final int x, final int y) {
        frame.setLocation(x, y);
    }
    
    public void setPosition(final String variable, final int x, final int y)
            throws IOException {
        
        final Buffer buffer = getBuffer(variable);
        buffer.setX(x);
        buffer.setY(y);
    }
    
    public void setVisible(final boolean visible) {
        frame.setVisible(visible);
    }
    
    public void setVisible(final String variable, final boolean visible)
            throws IOException {
        
        getBuffer(variable).setVisible(visible);
    }
    
    /**
     * Sets the index of the first buffer associate with the given variable.
     * @param arg a zero-based index,
     * optionally with sign to move relative to current index,
     * or "top" to move to top
     */
    public void setIndex(final String variable, final String arg)
            throws IOException {
        
        final int currentIndex = getBufferIndex(variable);
        if ("top".equals(arg)) {
            buffers.add(buffers.remove(currentIndex));
        } else {
            final int newIndex;
            if (arg.startsWith("+")) {
                newIndex = currentIndex + Arguments.parseInt(arg);
            } else if (arg.startsWith("-")) {
                newIndex = currentIndex - Arguments.parseInt(arg);
            } else {
                newIndex = Arguments.parseInt(arg);
            }
            if (newIndex < 0 || newIndex > buffers.size() - 1) {
                invalidIndex(arg);
            }
            buffers.add(newIndex, buffers.remove(currentIndex));
        }
    }
    
    public void setBackground(final Color background) {
        frame.getContentPane().setBackground(background);
    }
    
    /*****************
     * Drawing state *
     *****************/
    
    public void setBuffer(final String variable) throws IOException {
        drawing.setBuffer(variable);
    }
    
    public void setColor(final Color color) {
        drawing.setColor(color);
    }
    
    public void setFont(final Font font) {
        drawing.setFont(font);
    }
    
    public void setClip(
            final int x, final int y, final int width, final int height) {
        
        drawing.setClip(x, y, width, height);
    }
    
    /***********
     * Drawing *
     ***********/
    
    public void move(final int x, final int y) {
        drawing.move(x, y);
    }
    
    public void line(final int x, final int y) {
        drawing.line(x, y);
    }
    
    public void rectangle(
            final boolean fill, final int width, final int height) {
        
        drawing.rectangle(fill, width, height);
    }
    
    /**
     * Draws or fill a polygon.
     * @param xy x1 y1 x2 y2 etc.
     */
    public void polygon(final boolean fill, final int[] xy) throws IOException {
        drawing.polygon(fill, xy);
    }
    
    public void oval(final boolean fill, final int width, final int height) {
        drawing.oval(fill, width, height);
    }
    
    public void text(final String str) {
        drawing.text(str);
    }
    
    /**
     * Draws the image assigned to the specified variable.
     * @param width zero or less to disregard
     * @param height zero or less to disregard
     */
    public void image(final String variable, final int width, final int height)
            throws IOException {
        
        drawing.image(variable, width, height);
    }
    
    public void refresh() {
        frame.getContentPane().repaint();
    }
    
    /*******************
     * Private methods *
     *******************/
    
    private Buffer getBuffer(final String variable) throws IOException {
        return buffers.get(getBufferIndex(variable));
    }
    
    private int getBufferIndex(final String variable) throws IOException {
        for (int i = 0; i < buffers.size(); i++) {
            if (buffers.get(i).getVariable().equals(variable)) {
                return i;
            }
        }
        throw noSuchBuffer(variable);
    }
    
    private static IOException noSuchBuffer(final String variable) {
        return new IOException("No such buffer: " + variable);
    }
    
    private static IOException invalidIndex(final String arg) {
        return new IOException("Invalid index: " + arg);
    }
    
    private static IOException invalidPolygonCoordinates() {
        return new IOException("Invalid polygon coordinates");
    }
    
    /*******************
     * Private classes *
     *******************/
    
    private final class BufferPanel extends JPanel {
        
        private static final long serialVersionUID = 1L;

        private BufferPanel() {
            super.setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            buffers.stream()
                .filter(Buffer::isVisible)
                .forEach(buffer -> g.drawImage(
                        buffer.getImage(),
                        buffer.getX(),
                        buffer.getY(),
                        frame));
        }
        
    }
    
    private final class Drawing {
        private Graphics g;
        private int x;
        private int y;
        
        private Drawing() {
        }

        private void setBuffer(final String variable) throws IOException {
            final Buffer buffer = getBuffer(variable);
            if (g != null) {
                g.dispose();
            }
            g = buffer.getImage().getGraphics();
        }
        
        private void setColor(final Color color) {
            if (g != null) {
                g.setColor(color);
            }
        }
        
        private void setFont(final Font font) {
            if (g != null) {
                g.setFont(font);
            }
        }
        
        private void setClip(
                final int x, final int y, final int width, final int height) {
            
            if (g != null) {
                g.setClip(x, y, width, height);
            }
        }
        
        private void move(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
        
        private void line(final int x, final int y) {
            if (g != null) {
                g.drawLine(this.x, this.y, x, y);
            }
        }
        
        private void rectangle(
                final boolean fill, final int width, final int height) {
            
            if (g != null) {
                if (fill) {
                    g.fillRect(this.x, this.y, width, height);
                } else {
                    g.drawRect(this.x, this.y, width, height);
                }
            }
        }
        
        private void polygon(final boolean fill, final int xy[])
                throws IOException {
            
            if (xy.length % 2 != 0) {
                throw invalidPolygonCoordinates();
            }
            if (g != null) {
                final var xPoints = new int[xy.length / 2];
                final var yPoints = new int[xy.length / 2];
                for (int i = 0; i < xy.length - 1; i += 2) {
                    xPoints[i / 2] = xy[i];
                    yPoints[i / 2] = xy[i + 1];
                }
                if (fill) {
                    g.fillPolygon(xPoints, yPoints, xy.length);
                } else {
                    g.drawPolygon(xPoints, yPoints, xy.length);
                }
            }
        }
        
        private void oval(
                final boolean fill, final int width, final int height) {
            
            if (g != null) {
                if (fill) {
                    g.fillOval(this.x, this.y, width, height);
                } else {
                    g.fillOval(this.x, this.y, width, height);
                }
            }
        }
        
        private void text(final String str) {
            if (g != null) {
                g.drawString(str, this.x, this.y);
            }
        }
        
        private void image(
                final String variable,
                final int width,
                final int height) throws IOException {
            
            if (g != null) {
                final BufferedImage image = getBuffer(variable).getImage();
                final int actualWidth = width < 1
                        ? image.getWidth() : width;
                final int actualHeight = height < 1
                        ? image.getHeight() : height;
                g.drawImage(
                        image,
                        this.x,
                        this.y,
                        actualWidth,
                        actualHeight,
                        frame);
            }
        }
    }

}
