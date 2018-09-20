package freecrumbs.macrec.layer;

import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import freecrumbs.macrec.Layer;

/**
 * A layer used for marking positions on the screen.
 * 
 * @author Tone Sommerland
 */
public final class MarkLayer implements Layer {
    
    private static final class Mark {
        final Point point;
        final Color color;
        final int length;
        
        Mark(final Point point, final Color color, final int length) {
            assert point != null;
            assert color != null;
            assert length >= 0;
            this.point = point;
            this.color = color;
            this.length = length;
        }
    }
    
    private final List<Mark> marks = new ArrayList<>();

    public MarkLayer() {
    }

    @Override
    public void paint(final Graphics g) {
        for (final Mark mark : marks) {
            g.setColor(mark.color);
            g.drawLine(
                    mark.point.x - mark.length,
                    mark.point.y,
                    mark.point.x + mark.length,
                    mark.point.y);
            g.drawLine(
                    mark.point.x,
                    mark.point.y - mark.length,
                    mark.point.x,
                    mark.point.y + mark.length);
        }
    }
    
    public void addMark(
            final Point point, final Color color, final int length) {
        
        if (length < 0) {
            throw new IllegalArgumentException("length < 0: " + length);
        }
        marks.add(new Mark(
                new Point(requireNonNull(point, "null")),
                requireNonNull(color, "color"),
                length));
    }
    
    public void removeMark(final int index) {
        marks.remove(index);
    }
    
    public Point getMarkPoint(final int index) {
        return new Point(marks.get(index).point);
    }
    
    public int getMarkCount() {
        return marks.size();
    }
    
    public void clearMarks() {
        marks.clear();
    }

}
