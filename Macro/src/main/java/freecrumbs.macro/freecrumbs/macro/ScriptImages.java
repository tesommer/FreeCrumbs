package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * Script images.
 * 
 * @author Tone Sommerland
 */
public final class ScriptImages
{
    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Location location;

    ScriptImages(final Location location)
    {
        this.location = requireNonNull(location, "location");
    }
    
    /**
     * The names of all images stored in the script.
     */
    public Set<String> getNames()
    {
        return Collections.unmodifiableSet(images.keySet());
    }
    
    /**
     * Sets a script image.
     * Associates the image with the given name.
     * Replaces any image already associated with the name.
     */
    public void set(final String name, final BufferedImage image)
    {
        images.put(
                requireNonNull(name, "name"), requireNonNull(image, "image"));
    }
    
    /**
     * Removes the image with the given name if it exists.
     */
    public void remove(final String name)
    {
        images.remove(requireNonNull(name, "name"));
    }
    
    /**
     * Returns the image with the given name.
     * @throws MacroException if the image was not found
     */
    public BufferedImage get(final String name) throws MacroException
    {
        if (images.containsKey(name))
        {
            return images.get(name);
        }
        throw new MacroException("No such image: " + name);
    }
    
    /**
     * Loads an image.
     * @param imageLocation the location of the image
     * @throws MacroException if the image could not be loaded
     */
    public BufferedImage load(final String imageLocation)
            throws MacroException
    {
        try (final InputStream in = location.refer(imageLocation).open())
        {
            return ImageIO.read(in);
        }
        catch (final IOException ex)
        {
            throw new MacroException(ex);
        }
    }
    
    /**
     * First tries to get the image with the given name,
     * and if that fails, tries to load it.
     * @param nameOrLocation either an image name or location.
     * @throws MacroException if not found.
     */
    public BufferedImage getOrLoad(final String nameOrLocation)
            throws MacroException
    {
        try
        {
            return get(nameOrLocation);
        }
        catch (final MacroException ex)
        {
            return load(nameOrLocation);
        }
    }

}
