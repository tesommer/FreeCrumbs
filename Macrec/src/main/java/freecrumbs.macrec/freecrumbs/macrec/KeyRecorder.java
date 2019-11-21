package freecrumbs.macrec;

import static java.util.Objects.requireNonNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

import javax.swing.JFrame;

import freecrumbs.macro.Loader;
import freecrumbs.macro.Location;
import freecrumbs.macro.Macro;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.RecursionGuard;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;
import freecrumbs.macro.gesture.KeyPress;
import freecrumbs.macro.gesture.KeyRelease;

/**
 * This frame sends key gestures to the receiver.
 * 
 * @author Tone Sommerland
 */
public final class KeyRecorder extends JFrame implements KeyListener
{
    private static final long serialVersionUID = 1L;
    
    private static final Location
    UXO_LOCATION = new Location()
    {
        @Override
        public Location refer(final String target) throws MacroException
        {
            throw new MacroException("UXO location just exploded!");
        }
        @Override
        public InputStream open() throws MacroException
        {
            return new ByteArrayInputStream(new byte[0]);
        }
    };
    
    private static final Loader UXO_LOADER = new Loader()
    {
        @Override
        public Macro[] load(final InputStream in) throws MacroException
        {
            return new Macro[0];
        }
        @Override
        public RecursionGuard getRecursionGuard()
        {
            return RecursionGuard.getAtomic(0);
        }
    };
    
    private static final int WIDTH = 123;
    private static final int HEIGHT = 123;
    
    private final Script script = emptyScript();
    private final Consumer<? super String> receiver;

    public KeyRecorder(final Consumer<? super String> receiver)
    {
        this.receiver = requireNonNull(receiver, "receiver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
        setSize(WIDTH, HEIGHT);
        Util.addKeyCodeVariables(script);
    }
    
    private static Script emptyScript()
    {
        try
        {
            return Script.load(UXO_LOCATION, UXO_LOADER);
        }
        catch (final MacroException ex)
        {
            throw new AssertionError(ex);
        }
    }

    @Override
    public void keyTyped(final KeyEvent evt)
    {
    }

    @Override
    public void keyPressed(final KeyEvent evt)
    {
        receiver.accept(KeyPress.NAME + " " + getParameter(evt.getKeyCode()));
    }

    @Override
    public void keyReleased(final KeyEvent evt)
    {
        receiver.accept(KeyRelease.NAME + " " + getParameter(evt.getKeyCode()));
    }
    
    private String getParameter(final int keyCode)
    {
        for (final String name : script.variables().getNames())
        {
            try
            {
                if (script.variables().get(name) == keyCode)
                {
                    return name;
                }
            }
            catch (final MacroException ex)
            {
                throw new AssertionError(ex);
            }
        }
        return String.valueOf(keyCode);
    }

}
