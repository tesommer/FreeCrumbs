package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.awt.image.BufferedImage;

/**
 * Scans an image for sub-images.
 * 
 * @author Tone Sommerland
 */
public final class Scanner {
    private final BufferedImage image;
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;

    /**
     * Creates a new scanner.
     * The from/to parameters may be negative to disregard them.
     * If they are out of bounds, they will be restricted automatically.
     * @param image the image to scan
     * @param fromX start at x
     * @param fromY start at y
     * @param toX stop at x (exclusive)
     * @param toY stop at y (exclusive)
     */
    public Scanner(
            final BufferedImage image,
            final int fromX,
            final int fromY,
            final int toX,
            final int toY) {
        
        this.image = requireNonNull(image, "image");
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }
    
    /**
     * Creates a scanner for the entire region of an image.
     * @param image the image to scan
     */
    public Scanner(final BufferedImage image) {
        this(image, -1, -1, -1, -1);
    }

    /**
     * Searches for a sub-image.
     * @param subImage the image to find
     * @param occurrence the occurrence to find
     * @return an array containing x and y, or an empty array
     */
    public int[] xyOf(final BufferedImage subImage, final int occurrence) {
        if (occurrence < 1) {
            throw new IllegalArgumentException("occurrence < 1: " + occurrence);
        }
        int count = 0;
        for (int x = restrictFromX(); x < restrictToX(subImage); x++) {
            for (int y = restrictFromY(); y < restrictToY(subImage); y++) {
                if (isSubImageAt(subImage, x, y) && ++count == occurrence) {
                    return new int[] {x, y};
                }
            }
        }
        return new int[0];
    }

    private boolean isSubImageAt(
            final BufferedImage subImage,
            final int x,
            final int y) {
        
        for (int x2 = 0; x2 < subImage.getWidth(); x2++) {
            for (int y2 = 0; y2 < subImage.getHeight(); y2++) {
                final int rgb = subImage.getRGB(x2, y2);
                final int rgb2 = image.getRGB(x + x2, y + y2);
                if (rgb != rgb2) {
                    return false;
                }
            }
        }
        return true;
    }

    private int restrictFromX() {
        return fromX < 0 ? 0 : fromX;
    }

    private int restrictFromY() {
        return fromY < 0 ? 0 : fromY;
    }

    private int restrictToX(final BufferedImage subImage) {
        final int max = image.getWidth() - subImage.getWidth() + 1;
        return toX < 0 || toX > max ? max : toX;
    }

    private int restrictToY(final BufferedImage subImage) {
        final int max = image.getHeight() - subImage.getHeight() + 1;
        return toY < 0 || toY > max ? max : toY;
    }
}
