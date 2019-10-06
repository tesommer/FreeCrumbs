package freecrumbs.viewscreen;

import static freecrumbs.viewscreen.Arguments.parseInt;
import static freecrumbs.viewscreen.Arguments.requireByte;
import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Accessed from the EDT.
 */
public final class ViewScreen {
    private final List<Buffer> buffers = new ArrayList<>();
    private final Map<String, Integer> variables = new HashMap<>();
    private final JFrame frame = new JFrame();
    private DrawingContext context = new DrawingContext(null);

    ViewScreen() {
        final var panel = new BufferPanel();
        panel.setBackground(Color.WHITE);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private final class BufferPanel extends JPanel {
        
        private static final long serialVersionUID = 1L;

        private BufferPanel() {
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
    
    /*************
     * Variables *
     *************/
    
    void setVariable(final String name, final String[] args)
            throws IOException {
        
        variables.put(
                name,
                Variables.valueOf(args, frame, this::getBuffer, this::getInt));
    }
    
    void removeVariable(final String name) {
        variables.remove(requireNonNull(name, "name"));
    }
    
    /*********************
     * Upload & download *
     *********************/
    
    void upload(final String variable, final byte[] bytes) {
        final Image image = new ImageIcon(bytes).getImage();
        final BufferedImage buffered = createImage(
                image.getWidth(frame), image.getHeight(frame));
        final Graphics g = buffered.getGraphics();
        g.drawImage(image, 0, 0, frame);
        g.dispose();
        buffers.add(new Buffer(buffered, variable));
    }
    
    void download(
            final String variable,
            final String type,
            final OutputStream out) throws IOException {
        
        writeImage(getBuffer(variable).getImage(), type, out);
    }
    
    void download(final String type, final OutputStream out)
            throws IOException {
        
        final BufferedImage image = createImage(
                frame.getContentPane().getWidth(),
                frame.getContentPane().getHeight());
        final Graphics g = image.getGraphics();
        frame.getContentPane().paint(g);
        g.dispose();
        writeImage(image, type, out);
    }
    
    /******************
     * Frame & buffer *
     ******************/
    
    void setBackground(final String red, final String green, final String blue)
            throws IOException {
        
        frame.getContentPane().setBackground(getColor(red, green, blue));
    }
    
    void setSize(final String width, final String height) throws IOException {
        frame.setSize(getInt(width), getInt(height));
    }
    
    void setArea(final String width, final String height) throws IOException {
        frame.getContentPane().setPreferredSize(
                new Dimension(getInt(width), getInt(height)));
        frame.pack();
    }
    
    void setMaximized(final boolean maximized) {
        if (maximized) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setExtendedState(JFrame.NORMAL);
        }
    }
    
    void setDecorated(final boolean decorated) throws IOException {
        if (frame.isDisplayable()) {
            throw frameDisplayable();
        }
        frame.setUndecorated(!decorated);
    }
    
    void makeBuffer(
            final String variable,
            final String width,
            final String height) throws IOException {
        
        final int iw = getInt(width);
        final int ih = getInt(height);
        buffers.add(new Buffer(
                new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB),
                variable));
    }
    
    void removeBuffer(final String variable) throws IOException {
        final int index = getBufferIndex(variable);
        buffers.remove(index);
    }
    
    void setPosition(final String variable, final String x, final String y)
            throws IOException {
        
        final Buffer buffer = getBuffer(variable);
        final int ix = getInt(x);
        final int iy = getInt(y);
        buffer.setX(ix);
        buffer.setY(iy);
    }
    
    void setPosition(final String x, final String y) throws IOException {
        final int ix = getInt(x);
        final int iy = getInt(y);
        frame.setLocation(ix, iy);
    }
    
    void setVisible(final String variable, final boolean visible)
            throws IOException {
        
        getBuffer(variable).setVisible(visible);
    }
    
    void setVisible(final boolean visible) {
        frame.setVisible(visible);
    }
    
    /**
     * Sets the index of the first buffer associate with the given variable.
     * @param index a zero-based index,
     * optionally with sign to move relative to current index,
     * or "top" to move to top
     */
    void setIndex(final String variable, final String index)
            throws IOException {
        
        final int currentIndex = getBufferIndex(variable);
        if (index.equals("top")) {
            buffers.add(buffers.remove(currentIndex));
        } else {
            final int newIndex;
            if (index.startsWith("+")) {
                newIndex = currentIndex + getInt(index.substring(1));
            } else if (index.startsWith("-")) {
                newIndex = currentIndex - getInt(index.substring(1));
            } else {
                newIndex = getInt(index);
            }
            if (newIndex < 0 || newIndex > buffers.size() - 1) {
                invalidIndex(index);
            }
            buffers.add(newIndex, buffers.remove(currentIndex));
        }
    }
    
    /*******************
     * Drawing context *
     *******************/
    
    void begin() {
        this.context = new DrawingContext(this.context);
    }
    
    void end() {
        if (this.context.parent != null) {
            this.context = this.context.parent;
        }
    }

    void setBuffer(final String variable) throws IOException {
        final Buffer buffer = getBuffer(variable);
        if (context.g != null) {
            context.g.dispose();
        }
        context.g = buffer.getImage().getGraphics();
    }
    
    void setColor(
            final String red,
            final String green,
            final String blue,
            final String alpha) throws IOException {
        
        final Color color = getColor(red, green, blue, alpha);
        if (context.g == null) {
            return;
        }
        context.g.setColor(color);
    }
    
    void setColor(
            final String red,
            final String green,
            final String blue) throws IOException {
        
        final Color color = getColor(red, green, blue);
        if (context.g == null) {
            return;
        }
        context.g.setColor(color);
    }
    
    void setFont(
            final String name,
            final String size,
            final boolean bold,
            final boolean italic) throws IOException {
        
        int style = bold ? Font.BOLD : 0;
        if (italic) {
            style |= Font.ITALIC;
        }
        final var font = new Font(name, getInt(size), style);
        if (context.g == null) {
            return;
        }
        context.g.setFont(font);
    }
    
    void setClip(
            final String x,
            final String y,
            final String width,
            final String height) throws IOException {
        
        final int ix = getInt(x);
        final int iy = getInt(y);
        final int iw = getInt(width);
        final int ih = getInt(height);
        if (context.g == null) {
            return;
        }
        context.g.setClip(ix, iy, iw, ih);
    }
    
    /***********
     * Drawing *
     ***********/
    
    void move(final String x, final String y) throws IOException {
        final int ix = getInt(x);
        final int iy = getInt(y);
        context.x = ix;
        context.y = iy;
    }
    
    void line(final String x, final String y) throws IOException {
        final int ix = getInt(x);
        final int iy = getInt(y);
        if (context.g == null) {
            return;
        }
        context.g.drawLine(context.x, context.y, ix, iy);
    }
    
    void rectangle(final boolean fill, final String width, final String height)
            throws IOException {
        
        final int iw = getInt(width);
        final int ih = getInt(height);
        if (context.g == null) {
            return;
        }
        if (fill) {
            context.g.fillRect(context.x, context.y, iw, ih);
        } else {
            context.g.drawRect(context.x, context.y, iw, ih);
        }
    }
    
    void polygon(final boolean fill, final String xy[]) throws IOException {
        if (xy.length % 2 != 0) {
            throw invalidPolygonCoordinates();
        }
        final int vertices = xy.length / 2;
        final var xPoints = new int[vertices];
        final var yPoints = new int[vertices];
        for (int i = 0; i < xy.length - 1; i += 2) {
            final int j = i / 2;
            xPoints[j] = getInt(xy[i]);
            yPoints[j] = getInt(xy[i + 1]);
        }
        if (context.g == null) {
            return;
        }
        if (fill) {
            context.g.fillPolygon(xPoints, yPoints, vertices);
        } else {
            context.g.drawPolygon(xPoints, yPoints, vertices);
        }
    }
    
    void oval(final boolean fill, final String width, final String height)
            throws IOException {
        
        final int iw = getInt(width);
        final int ih = getInt(height);
        if (context.g == null) {
            return;
        }
        if (fill) {
            context.g.fillOval(context.x, context.y, iw, ih);
        } else {
            context.g.drawOval(context.x, context.y, iw, ih);
        }
    }
    
    void text(final String str) {
        if (context.g == null) {
            return;
        }
        context.g.drawString(str, context.x, context.y);
    }
    
    void image(
            final String variable,
            final String width,
            final String height) throws IOException {
        
        final BufferedImage image = getBuffer(variable).getImage();
        final int iw = getInt(width);
        final int ih = getInt(height);
        if (context.g == null) {
            return;
        }
        final int actualWidth = iw < 1 ? image.getWidth() : iw;
        final int actualHeight = ih < 1 ? image.getHeight() : ih;
        context.g.drawImage(
                image,
                context.x,
                context.y,
                actualWidth,
                actualHeight,
                frame);
    }
    
    void image(final String variable) throws IOException {
        final BufferedImage image = getBuffer(variable).getImage();
        if (context.g == null) {
            return;
        }
        context.g.drawImage(
                image,
                context.x,
                context.y,
                frame);
    }
    
    void refresh() {
        frame.getContentPane().repaint();
    }
    
    /*****************
     * Private stuff *
     *****************/
    
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
    
    private int getInt(final String arg) throws IOException {
        final Integer value = variables.get(requireNonNull(arg, "arg"));
        return value == null ? parseInt(arg) : value;
    }
    
    private Color getColor(
            final String red,
            final String green,
            final String blue,
            final String alpha) throws IOException {
        
        final String msg = "Invalid color component: ";
        final int intAlpha;
        if (alpha == null) {
            intAlpha = 255;
        } else {
            intAlpha = requireByte(getInt(alpha), msg + alpha);
        }
        return new Color(
                requireByte(getInt(red),   msg + red),
                requireByte(getInt(green), msg + green),
                requireByte(getInt(blue),  msg + blue),
                intAlpha);
    }
    
    private Color getColor(
            final String red,
            final String green,
            final String blue) throws IOException {
        
        return getColor(red, green, blue, null);
    }
    
    private static BufferedImage createImage(
            final int width, final int height) {
        
        return new BufferedImage(
                Math.max(1, width),
                Math.max(1, height),
                BufferedImage.TYPE_INT_ARGB);
    }
    
    private static void writeImage(
            final BufferedImage image,
            final String type,
            final OutputStream out) throws IOException {
        
        final Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix(type);
        if (it.hasNext()) {
            final ImageWriter writer = it.next();
            writer.setOutput(new MemoryCacheImageOutputStream(out));
            writer.write(image);
        } else {
            throw unsupportedImageType(type);
        }
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
    
    private static IOException unsupportedImageType(final String type) {
        return new IOException("Unsupported image type: " + type);
    }
    
    private static IOException frameDisplayable() {
        return new IOException("Frame is already displayable.");
    }
    
    private static final class DrawingContext {
        private DrawingContext parent;
        private Graphics g;
        private int x;
        private int y;
        
        private DrawingContext(final DrawingContext parent) {
            this.parent = parent;
        }
    }

}
