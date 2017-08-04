package freecrumbs.macrec;

import static java.util.Objects.requireNonNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

import javax.swing.JFrame;

import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;
import freecrumbs.macro.gesture.KeyPress;
import freecrumbs.macro.gesture.KeyRelease;

/**
 * This frame sends key gestures to the receiver.
 * 
 * @author Tone Sommerland
 */
public class KeyRecorder extends JFrame implements KeyListener {
    
    private static final long serialVersionUID = 1L;
    
    private static final int WIDTH = 123;
    private static final int HEIGHT = 123;
    
    private final Script script = Util.createEmptyScript();
    private final Consumer<String> receiver;

    public KeyRecorder(final Consumer<String> receiver) {
        this.receiver = requireNonNull(receiver, "receiver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
        setSize(WIDTH, HEIGHT);
        Util.addKeyCodeVariables(script);
    }

    @Override
    public void keyTyped(final KeyEvent evt) {
    }

    @Override
    public void keyPressed(final KeyEvent evt) {
        receiver.accept(KeyPress.NAME + " " + getParameter(evt.getKeyCode()));
    }

    @Override
    public void keyReleased(final KeyEvent evt) {
        receiver.accept(KeyRelease.NAME + " " + getParameter(evt.getKeyCode()));
    }
    
    private String getParameter(final int keyCode) {
        for (final String name : script.getVariables().getNames()) {
            try {
                if (script.getVariables().get(name) == keyCode) {
                    return name;
                }
            } catch (final MacroException ex) {
                throw new AssertionError(ex);
            }
        }
        return String.valueOf(keyCode);
    }

}
