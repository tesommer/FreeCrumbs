package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.FileFilter;
import java.util.Comparator;
import java.util.Optional;

/**
 * Finf config.
 * Contains the following properties:
 * <ul>
 *  <li>{@code infoGenerator}: for generating file info</li>
 *  <li>{@code infoFormat}: format of the file info units</li>
 *  <li>{@code fileFilter}: filters input files (optional)</li>
 *  <li>{@code order}: sort order of the info units (optional)</li>
 *  <li>{@code count}: max lines to output (-1 = all)</li>
 * </ul>
 *
 * @author Tone Sommerland
 */
public class Config {
    private final InfoGenerator infoGenerator;
    private final InfoFormat infoFormat;
    private final FileFilter fileFilter;
    private final Comparator<? super Info> order;
    private final int count;
    
    /**
     * Creates a new configuration.
     * @param infoGenerator the file info generator
     * @param infoFormat the file info format
     * @param fileFilter the file filter (nullable)
     * @param order the file info sorter (nullable)
     * @param count max lines to output (-1 = all)
     */
    public Config(
        final InfoGenerator infoGenerator,
        final InfoFormat infoFormat,
        final FileFilter fileFilter,
        final Comparator<? super Info> order,
        final int count) {
        
        this.infoGenerator = requireNonNull(infoGenerator, "infoGenerator");
        this.infoFormat = requireNonNull(infoFormat, "infoFormat");
        this.fileFilter = fileFilter;
        this.order = order;
        this.count = count;
    }
    
    public InfoGenerator getInfoGenerator() {
        return infoGenerator;
    }
    
    public InfoFormat getInfoFormat() {
        return infoFormat;
    }
    
    public Optional<FileFilter> getFileFilter() {
        return Optional.ofNullable(fileFilter);
    }
    
    public Optional<Comparator<? super Info>> getOrder() {
        return Optional.ofNullable(order);
    }
    
    public int getCount() {
        return count;
    }
    
}
