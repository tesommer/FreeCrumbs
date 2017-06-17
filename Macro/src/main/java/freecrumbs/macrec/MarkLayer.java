package freecrumbs.macrec;

import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A layer used for marking positions on the screen.
 * 
 * @author Tone Sommerland
 */
public class MarkLayer implements Layer {
    
    private static final class Mark {
        final Point point;
        final Color color;
        final int length;
        
        public Mark(final Point point, final Color color, final int length) {
            this.point = requireNonNull(point, "point");
            this.color = requireNonNull(color, "color");
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
        
        marks.add(new Mark(new Point(point), color, length));
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
