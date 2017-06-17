package freecrumbs.macrec;

import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

/**
 * This layer fills the screen with a checker pattern.
 * 
 * @author Tone Sommerland
 */
public class CheckerLayer implements Layer {
    private final Color color;
    private final int squareWidth;

    public CheckerLayer(final Color color, final int squareWidth) {
        this.color = requireNonNull(color, "color");
        this.squareWidth = squareWidth;
    }

    @Override
    public void paint(final Graphics g) {
        g.setColor(color);
        final Dimension screenSize
            = Toolkit.getDefaultToolkit().getScreenSize();
        int x = 0;
        int y = 0;
        int row = 0;
        while (y < screenSize.height) {
            boolean fill = row % 2 == 0;
            while (x < screenSize.width) {
                if (fill) {
                    g.fillRect(x, y, squareWidth, squareWidth);
                }
                x += squareWidth;
                fill ^= true;
            }
            x = 0;
            y += squareWidth;
            row++;
        }
    }

}
