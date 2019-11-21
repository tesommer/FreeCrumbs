package freecrumbs.macrec;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import freecrumbs.macro.MacroException;

/**
 * This frame displays a maximized screenshot
 * for use by mouse gesture recorders.
 * This class implements mouse and key listener for convenience.
 * The event handlers do nothing.
 * 
 * @author Tone Sommerland
 */
public class ScreenCaptureFrame extends JFrame
    implements MouseListener, KeyListener
{
    private static final long serialVersionUID = 1L;
    
    protected final BufferedImage screenCapture;
    
    protected ScreenCaptureFrame() throws MacroException
    {
        this.screenCapture = createScreenCapture();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(this);
        addMouseListener(this);
        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
    }

    private static BufferedImage createScreenCapture() throws MacroException
    {
        final Dimension screenSize
            = Toolkit.getDefaultToolkit().getScreenSize();
        try
        {
            return new Robot().createScreenCapture(new Rectangle(screenSize));
        }
        catch (final AWTException ex)
        {
            throw new MacroException(ex);
        }
    }
    
    @Override
    public void keyTyped(final KeyEvent evt)
    {
    }

    @Override
    public void keyPressed(final KeyEvent evt)
    {
    }

    @Override
    public void keyReleased(final KeyEvent evt)
    {
    }

    @Override
    public void mouseClicked(final MouseEvent evt)
    {
    }

    @Override
    public void mousePressed(final MouseEvent evt)
    {
    }

    @Override
    public void mouseReleased(final MouseEvent evt)
    {
    }

    @Override
    public void mouseEntered(final MouseEvent evt)
    {
    }

    @Override
    public void mouseExited(final MouseEvent evt)
    {
    }

}
