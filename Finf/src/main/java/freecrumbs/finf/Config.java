package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.FileFilter;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Optional;

/**
 * Finf config.
 * Contains the following properties:
 * <ul>
 *  <li>{@code messageDigest}: for generating file hashes</li>
 *  <li>{@code infoFormat}: format of the file info units</li>
 *  <li>{@code fileFilter}: filters input files (optional)</li>
 *  <li>{@code order}: sort order of the info units (optional)</li>
 *  <li>{@code count}: max lines to output (-1 = all)</li>
 * </ul>
 *
 * @author Tone Sommerland
 */
public class Config {
    private final MessageDigest messageDigest;
    private final InfoFormat infoFormat;
    private final FileFilter fileFilter;
    private final Comparator<Info> order;
    private final int count;
    
    /**
     * Creates a new configuration.
     * @param messageDigest the hash generator
     * @param infoFormat the file info format
     * @param fileFilter the file filter (nullable)
     * @param order the file info sorter (nullable)
     * @param count max lines to output (-1 = all)
     */
    public Config(
        final MessageDigest messageDigest,
        final InfoFormat infoFormat,
        final FileFilter fileFilter,
        final Comparator<Info> order,
        final int count) {
        
        this.messageDigest = requireNonNull(messageDigest, "messageDigest");
        this.infoFormat = requireNonNull(infoFormat, "infoFormat");
        this.fileFilter = fileFilter;
        this.order = order;
        this.count = count;
    }
    
    public MessageDigest getMessageDigest() {
        return messageDigest;
    }
    
    public InfoFormat getInfoFormat() {
        return infoFormat;
    }
    
    public Optional<FileFilter> getFileFilter() {
        return Optional.ofNullable(fileFilter);
    }
    
    public Optional<Comparator<Info>> getOrder() {
        return Optional.ofNullable(order);
    }
    
    public int getCount() {
        return count;
    }
    
}
