package freecrumbs.macro;

import static java.util.Objects.requireNonNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Script images.
 * 
 * @author Tone Sommerland
 */
public class ScriptImages {
    private final Map<String, BufferedImage> images = new HashMap<>();
    private final String scriptFile;

    public ScriptImages(final String scriptFile) {
        this.scriptFile = requireNonNull(scriptFile, "scriptFile");
    }
    
    /**
     * The names of all images stored in the script.
     */
    public Set<String> getNames() {
        return Collections.unmodifiableSet(images.keySet());
    }
    
    /**
     * Sets a script image.
     * Associates the image with the given name.
     * Replaces any image already associated with the name.
     */
    public void set(final String name, final BufferedImage image) {
        images.put(
                requireNonNull(name, "name"), requireNonNull(image, "image"));
    }
    
    /**
     * Removes the image with the given name if it exists.
     */
    public void remove(final String name) {
        images.remove(name);
    }
    
    /**
     * Returns the image with the given name.
     * @throws MacroException if the image was not found.
     */
    public BufferedImage get(final String name) throws MacroException {
        if (images.containsKey(name)) {
            return images.get(name);
        }
        throw new MacroException("No such image: " + name);
    }
    
    /**
     * Loads an image from file.
     * @param file the image file, may be relative to the script
     * @throws MacroException if the image could not be loaded.
     */
    public BufferedImage load(final String file) throws MacroException {
        return Util.loadImage(getScriptRelativeFile(file));
    }
    
    /**
     * First tries to get the image with the given name,
     * and if that fails,
     * tries to load it from file.
     * @param nameOrFile either an image name or file path.
     * @throws MacroException if not found.
     */
    public BufferedImage getOrLoad(final String nameOrFile)
            throws MacroException {
        
        try {
            return get(nameOrFile);
        } catch (final MacroException ex) {
            return load(nameOrFile);
        }
    }
    
    private String getScriptRelativeFile(final String file) {
        final int index = scriptFile.lastIndexOf(File.separator);
        if (index > -1) {
            final File relative
                = new File(scriptFile.substring(0, index), file);
            if (relative.isFile()) {
                return relative.getPath();
            }
        }
        return file;
    }

}
