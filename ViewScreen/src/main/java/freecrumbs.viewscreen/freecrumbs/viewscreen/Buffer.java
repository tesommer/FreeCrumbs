package freecrumbs.viewscreen;

import static java.util.Objects.requireNonNull;

import java.awt.image.BufferedImage;

public final class Buffer {
    private final BufferedImage image;
    private final String variable;
    private int x;
    private int y;
    private boolean visible;

    public Buffer(final BufferedImage image, final String variable) {
        this.image = requireNonNull(image, "image");
        this.variable = requireNonNull(variable, "variable");
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getVariable() {
        return variable;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

}
