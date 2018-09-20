package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

/**
 * Finf config.
 * Contains the following properties:
 * <ul>
 *  <li>{@code infoGenerator}: for generating file info</li>
 *  <li>{@code infoFormat}: format of the file info units</li>
 *  <li>{@code fileFilter}: filters input files (optional)</li>
 *  <li>{@code order}: sort order of the info units (optional)</li>
 *  <li>{@code count}: max info units to output ({@code < 0} = all)</li>
 * </ul>
 *
 * @author Tone Sommerland
 */
public final class Config {
    
    /**
     * Builds config instances.
     * 
     * @author Tone Sommerland
     */
    public static final class Builder {
        private final Function<? super File, ? extends Info> infoGenerator;
        private final InfoFormat infoFormat;
        private FileFilter fileFilter;
        private Comparator<? super Info> order;
        private int count = -1;
        
        public Builder(
                final Function<? super File, ? extends Info> infoGenerator,
                final InfoFormat infoFormat) {
            
            this.infoGenerator = requireNonNull(infoGenerator, "infoGenerator");
            this.infoFormat = requireNonNull(infoFormat, "infoFormat");
        }
        
        /**
         * Sets the file filter.
         * @param fileFilter the file filter (nullable)
         * @return {@code this}
         */
        public Builder setFileFilter(final FileFilter fileFilter) {
            this.fileFilter = fileFilter;
            return this;
        }

        /**
         * Sets the sorter.
         * @param order the sorter (nullable)
         * @return {@code this}
         */
        public Builder setOrder(final Comparator<? super Info> order) {
            this.order = order;
            return this;
        }

        /**
         * Sets the output count.
         * @param count the count ({@code < 0} to turn off)
         * @return {@code this}
         */
        public Builder setCount(final int count) {
            this.count = count;
            return this;
        }

        /**
         * Builds the config instance.
         */
        public Config build() {
            return new Config(
                    infoGenerator, infoFormat, fileFilter, order, count);
        }
    }
    
    private final Function<? super File, ? extends Info> infoGenerator;
    private final InfoFormat infoFormat;
    private final FileFilter fileFilter;
    private final Comparator<? super Info> order;
    private final int count;
    
    private Config(
        final Function<? super File, ? extends Info> infoGenerator,
        final InfoFormat infoFormat,
        final FileFilter fileFilter,
        final Comparator<? super Info> order,
        final int count) {

        assert infoGenerator != null;
        assert infoFormat != null;
        this.infoGenerator = infoGenerator;
        this.infoFormat = infoFormat;
        this.fileFilter = fileFilter;
        this.order = order;
        this.count = count;
    }
    
    public Function<? super File, ? extends Info> getInfoGenerator() {
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
