package freecrumbs.viewscreen;

import java.io.IOException;

public final class Arguments {

    private Arguments() {
    }
    
    static boolean parseBoolean(final String arg) {
        if (arg.equals("0") || arg.equals("false")) {
            return false;
        }
        return true;
    }
    
    static int parseInt(final String arg) throws IOException {
        try {
            return Integer.parseInt(arg);
        } catch (final NumberFormatException ex) {
            throw new IOException(ex);
        }
    }
    
    static int requireZeroPlus(final int arg, final String message)
            throws IOException {
        
        if (arg < 0) {
            throw new IOException(message);
        }
        return arg;
    }
    
    static int requireOnePlus(final int arg, final String message)
            throws IOException {
        
        if (arg < 1) {
            throw new IOException(message);
        }
        return arg;
    }
    
    static int requireByte(final int arg, final String message)
            throws IOException {
        
        if (arg < 0 || arg > 255) {
            throw new IOException(message);
        }
        return arg;
    }

}
