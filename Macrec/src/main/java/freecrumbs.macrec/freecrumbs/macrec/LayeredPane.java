package freecrumbs.macrec;

import java.awt.Graphics;
import java.util.stream.Stream;

import javax.swing.JPanel;

/**
 * This panel's visual appearance is rendered by a stack of {@link Layer}s.
 * 
 * @author Tone Sommerland
 */
public final class LayeredPane extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final Layer[] layers;
    
    public LayeredPane(final Layer... layers) {
        this.layers = layers.clone();
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        Stream.of(layers).forEach(layer -> layer.paint(g));
    }
}
