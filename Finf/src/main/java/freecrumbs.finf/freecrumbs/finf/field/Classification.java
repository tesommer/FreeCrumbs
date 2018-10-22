package freecrumbs.finf.field;

import static freecrumbs.finf.field.Classification.Category.BINARY;
import static freecrumbs.finf.field.Classification.Category.EMPTY;
import static freecrumbs.finf.field.Classification.Category.TEXT;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

/**
 * Classifies a file as either text, binary or empty.
 * The name of this field is {@code "class"}.
 * The heuristic used by this computation
 * checks at most {@code limit} bytes to make the determination,
 * unless the limit is zero or negative,
 * in which case it reads the entire file.
 * The file is deemed binary
 * if the number of non-text-chars exceeds the {@code threshold},
 * a normalized percentage of total bytes checked.
 * If the threshold is negative, the file is always binary.
 * 
 * @author Tone Sommerland
 */
public final class Classification implements FieldComputation {
    
    /**
     * Represents the heuristic used by this computation.
     * 
     * @author Tone Sommerland
     */
    public static final class Heuristic {
        
        private static final int DEFAULT_LIMIT = 512;
        
        private static final double DEFAULT_THRESHOLD = .3;
        
        private static final int[]
        DEFAULT_TEXT_CHARS = IntStream.concat(
                IntStream.rangeClosed(32, 127),
                IntStream.of('\r', '\n', '\t', '\b', '\f'))
            .toArray();
        
        private static final IntPredicate
        DEFAULT_IS_TEXT_CHAR
            = ch -> IntStream.of(DEFAULT_TEXT_CHARS)
                .anyMatch(i -> i == ch);
            
        /**
         * The default heuristic uses a limit of 512 bytes,
         * a threshold of .3 (30 percent)
         * and chars 32–127 + \r, \n, \t, \b and \f to be text chars.
         */
        public static final Heuristic
        DEFAULT = new Heuristic(
                DEFAULT_LIMIT, DEFAULT_THRESHOLD, DEFAULT_IS_TEXT_CHAR);
        
        private final int limit;
        private final double threshold;
        private final IntPredicate isTextChar;
        
        private Heuristic(
                final int limit,
                final double threshold,
                final IntPredicate isTextChar) {
            
            this.limit = limit;
            this.threshold = threshold;
            this.isTextChar = requireNonNull(isTextChar, "isTextChar");
        }
        
        public Heuristic withLimit(final int limit) {
            return new Heuristic(limit, this.threshold, this.isTextChar);
        }
        
        public Heuristic withThreshold(final double threshold) {
            return new Heuristic(this.limit, threshold, this.isTextChar);
        }
        
        public Heuristic withIsTextChar(final IntPredicate isTextChar) {
            return new Heuristic(this.limit, this.threshold, isTextChar);
        }
    }

    /**
     * Text, binary or empty.
     * 
     * @author Tone Sommerland
     */
    public static enum Category {
        TEXT,
        BINARY,
        EMPTY,
    }
    
    private static final String NAME = "class";
    
    private final Heuristic heuristic;
    private final Function<? super Category, String> value;
    private int bytesRead;
    private int binCount;
    private Category category;
    
    private Classification(
            final Heuristic heuristic,
            final Function<? super Category, String> value) {
        
        this.heuristic = requireNonNull(heuristic, "heuristic");
        this.value = requireNonNull(value, "value");
    }
    
    public static Field getField(
            final Heuristic heuristic,
            final Function<? super Category, String> value) {
        
        return Field.getInstance(NAME, new Classification(heuristic, value));
    }

    @Override
    public void reset() throws IOException {
        bytesRead = 0;
        binCount = 0;
        category = null;
    }

    @Override
    public boolean update(
            final byte[] input,
            final int offset,
            final int length) throws IOException {
        
        if (input.length == 0) {
            category = EMPTY;
            return false;
        }
        final int offsetPlusLength = offset + length;
        for (int i = offset; i < offsetPlusLength; i++) {
            if (++bytesRead > heuristic.limit && heuristic.limit > 0) {
                return false;
            }
            final byte ch = input[i];
            if (ch == 0) {
                category = BINARY;
                return false;
            } else if (!heuristic.isTextChar.test(ch)) {
                binCount++;
            }
        }
        return true;
    }

    @Override
    public String get() throws IOException {
        if (category == null) {
            category = binCount / (double)bytesRead > heuristic.threshold
                    ? BINARY : TEXT;
        }
        return value.apply(category);
    }

}
