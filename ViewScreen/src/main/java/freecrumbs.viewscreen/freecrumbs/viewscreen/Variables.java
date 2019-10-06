package freecrumbs.viewscreen;

import static java.util.stream.Collectors.joining;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.stream.Stream;

import javax.swing.JFrame;

public final class Variables {
    
    @FunctionalInterface
    static interface Arg2Int {
        public abstract int getInt(String arg) throws IOException;
    }
    
    @FunctionalInterface
    static interface Arg2Buffer {
        public abstract Buffer getBuffer(String arg) throws IOException;
    }
    
    private static final Dimension
    SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private Variables() {
    }
    
    static int valueOf(
            final String[] args,
            final JFrame frame,
            final Arg2Buffer arg2Buffer,
            final Arg2Int arg2Int) throws IOException {
        
        if (args.length == 3) {
            return three(args[0], args[1], args[2], arg2Int);
        } else if (args.length == 2) {
            return two(args[0], args[1], arg2Buffer, arg2Int);
        } else if (args.length == 1) {
            return one(args[0], frame, arg2Int);
        }
        throw invalidVariableOperation(args);
    }
    
    private static int three(
            final String first,
            final String second,
            final String third,
            final Arg2Int arg2Int) throws IOException {
        
        if (second.equals("+")) {
            return arg2Int.getInt(first) + arg2Int.getInt(third);
        } else if (second.equals("-")) {
            return arg2Int.getInt(first) - arg2Int.getInt(third);
        } else if (second.equals("*")) {
            return arg2Int.getInt(first) * arg2Int.getInt(third);
        } else if (second.equals("/")) {
            try {
                return arg2Int.getInt(first) / arg2Int.getInt(third);
            } catch (final ArithmeticException ex) {
                throw new IOException(ex);
            }
        } else if (second.equals("%")) {
            try {
                return arg2Int.getInt(first) % arg2Int.getInt(third);
            } catch (final ArithmeticException ex) {
                throw new IOException(ex);
            }
        } else if (second.equals("^")) {
            return (int)Math.pow(arg2Int.getInt(first), arg2Int.getInt(third));
        }
        throw invalidVariableOperation(first, second, third);
    }
    
    private static int two(
            final String first,
            final String second,
            final Arg2Buffer arg2Buffer,
            final Arg2Int arg2Int) throws IOException {
        
        if (first.equals("iw")) {
            return arg2Buffer.getBuffer(second).getImage().getWidth();
        } else if (first.equals("ih")) {
            return arg2Buffer.getBuffer(second).getImage().getHeight();
        }
        throw invalidVariableOperation(first, second);
    }
    
    private static int one(
            final String first,
            final JFrame frame,
            final Arg2Int arg2Int) throws IOException {
        
        if (first.equals("sw")) {
            return SCREEN_SIZE.width;
        } else if (first.equals("sh")) {
            return SCREEN_SIZE.height;
        } else if (first.equals("ww")) {
            return frame.getWidth();
        } else if (first.equals("wh")) {
            return frame.getHeight();
        } else if (first.equals("aw")) {
            return frame.getContentPane().getWidth();
        } else if (first.equals("ah")) {
            return frame.getContentPane().getHeight();
        } else {
            return arg2Int.getInt(first);
        }
    }
    
    private static IOException invalidVariableOperation(final String... args) {
        return new IOException("Invalid variable operation: "
                + Stream.of(args).collect(joining(" ")));
    }

}
