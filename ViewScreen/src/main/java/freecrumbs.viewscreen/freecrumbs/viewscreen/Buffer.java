package freecrumbs.viewscreen;

import static java.util.Objects.requireNonNull;

import java.awt.image.BufferedImage;

/**
 * Accessed from the EDT.
 */
public final class Buffer {
    private final BufferedImage image;
    private final String variable;
    private int x;
    private int y;
    private boolean visible;

    Buffer(final BufferedImage image, final String variable) {
        this.image = requireNonNull(image, "image");
        this.variable = requireNonNull(variable, "variable");
    }

    BufferedImage getImage() {
        return image;
    }

    String getVariable() {
        return variable;
    }

    int getX() {
        return x;
    }

    void setX(final int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(final int y) {
        this.y = y;
    }

    boolean isVisible() {
        return visible;
    }

    void setVisible(final boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return Buffer.class.getSimpleName() + ": " + variable;
    }

}
