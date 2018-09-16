package freecrumbs.macrec;

import static java.util.Objects.requireNonNull;

import java.awt.Graphics;
import java.awt.Image;

/**
 * A layer that draws an image.
 * 
 * @author Tone Sommerland
 */
public class ImageLayer implements Layer {
    private final Image image;
    
    public ImageLayer(final Image image) {
        this.image = requireNonNull(image, "image");
    }

    @Override
    public void paint(final Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}
