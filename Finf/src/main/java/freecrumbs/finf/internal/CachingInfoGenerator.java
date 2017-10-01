package freecrumbs.finf.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.calclipse.lib.util.EncodingUtil;

import freecrumbs.finf.HashGenerator;
import freecrumbs.finf.Info;
import freecrumbs.finf.InfoGenerator;

/**
 * This info generator caches the info it generates.
 * 
 * @author Tone Sommerland
 */
public class CachingInfoGenerator implements InfoGenerator {
    
    private final Map<File, Info> cache = new HashMap<File, Info>();

    public CachingInfoGenerator() {
    }

    @Override
    public Info getInfo(final File file, final HashGenerator hashGenerator)
            throws IOException {
        
        Info info = cache.get(file);
        if (info == null) {
            info = generateInfo(file, hashGenerator);
            cache.put(file, info);
        }
        return info;
    }
    
    /**
     * Returns the file info of a single file.
     * @throws IOException if the hash generator does.
     */
    private static Info generateInfo(
            final File file,
            final HashGenerator hashGenerator) throws IOException {
        
        final String path;
        final String filename;
        final int index = file.getPath().lastIndexOf(File.separatorChar);
        if (index < 0) {
            path = "";
            filename = file.getName();
        } else {
            path = file.getPath().substring(0, index + 1);
            filename = file.getPath().substring(index + 1);
        }
        final String hash
            = EncodingUtil.bytesToHex(false, hashGenerator.digest(file));
        return new Info(
                path, filename, file.length(), file.lastModified(), hash);
    }

}
