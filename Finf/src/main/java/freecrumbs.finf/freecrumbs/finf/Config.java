package freecrumbs.finf;

import static java.util.Objects.requireNonNull;

import java.io.FileFilter;
import java.util.Comparator;
import java.util.Optional;

/**
 * Finf config.
 * Contains the following properties:
 * <ul>
 *  <li>{@code generator}: for generating file info</li>
 *  <li>{@code formatter}: format of the file info units</li>
 *  <li>{@code filter}: filters input files (optional)</li>
 *  <li>{@code order}: sort order of the info units (optional)</li>
 *  <li>{@code count}: max info units to output ({@code < 0} = all)</li>
 * </ul>
 *
 * @author Tone Sommerland
 */
public final class Config
{
    /**
     * Builds config instances.
     * 
     * @author Tone Sommerland
     */
    public static final class Builder
    {
        private final InfoGenerator generator;
        private final InfoFormatter formatter;
        private FileFilter filter;
        private Comparator<? super Info> order;
        private int count = -1;
        
        public Builder(
                final InfoGenerator generator, final InfoFormatter formatter)
        {
            this.generator = requireNonNull(generator, "generator");
            this.formatter = requireNonNull(formatter, "formatter");
        }
        
        /**
         * Sets the file filter.
         * @param filter the file filter (nullable)
         * @return {@code this}
         */
        public Builder setFilter(final FileFilter filter)
        {
            this.filter = filter;
            return this;
        }

        /**
         * Sets the sorter.
         * @param order the sorter (nullable)
         * @return {@code this}
         */
        public Builder setOrder(final Comparator<? super Info> order)
        {
            this.order = order;
            return this;
        }

        /**
         * Sets the output count.
         * @param count the count ({@code < 0} to turn off)
         * @return {@code this}
         */
        public Builder setCount(final int count)
        {
            this.count = count;
            return this;
        }

        /**
         * Builds the config instance.
         */
        public Config build()
        {
            return new Config(generator, formatter, filter, order, count);
        }
    }
    
    private final InfoGenerator generator;
    private final InfoFormatter formatter;
    private final FileFilter filter;
    private final Comparator<? super Info> order;
    private final int count;
    
    private Config(
        final InfoGenerator generator,
        final InfoFormatter formatter,
        final FileFilter filter,
        final Comparator<? super Info> order,
        final int count)
    {
        assert generator != null;
        assert formatter != null;
        this.generator = generator;
        this.formatter = formatter;
        this.filter = filter;
        this.order = order;
        this.count = count;
    }
    
    public InfoGenerator generator()
    {
        return generator;
    }
    
    public InfoFormatter formatter()
    {
        return formatter;
    }
    
    public Optional<FileFilter> filter()
    {
        return Optional.ofNullable(filter);
    }
    
    public Optional<Comparator<? super Info>> order()
    {
        return Optional.ofNullable(order);
    }
    
    public int count()
    {
        return count;
    }
    
}
