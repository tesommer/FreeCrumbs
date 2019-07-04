package freecrumbs.macrec.layer;

import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import freecrumbs.macrec.Layer;

/**
 * This layer draws text.
 * Lines are LF-terminated.
 * 
 * @author Tone Sommerland
 */
public final class TextLayer implements Layer {
    private final Point point;
    private final Color color;
    private final Font font;
    private final int lineHeight;
    private String text = "";

    public TextLayer(
            final Point point,
            final Color color,
            final Font font,
            final int lineHeight) {
        
        this.point = new Point(requireNonNull(point, "point"));
        this.color = requireNonNull(color, "color");
        this.font = requireNonNull(font, "font");
        this.lineHeight = lineHeight;
    }

    @Override
    public void paint(final Graphics g) {
        g.setColor(color);
        g.setFont(font);
        int y = point.y;
        for (final String line : text.split("\\n")) {
            g.drawString(line.trim(), point.x, y);
            y += lineHeight;
        }
    }
    
    public void setText(final String text) {
        this.text = requireNonNull(text, "text");
    }

}
