package freecrumbs.viewscreen;

import static freecrumbs.viewscreen.Arguments.parseBoolean;
import static freecrumbs.viewscreen.Arguments.parseInt;

import java.io.IOException;
import java.util.Arrays;

public final class Executor {
    private static final String PREFIX = "vsc:";
    private static final String CMD_BACKGROUND = "background";
    private static final String CMD_UPLOAD     = "upload";
    private static final String CMD_MKBUFFER   = "mkbuffer";
    private static final String CMD_RMBUFFER   = "rmbuffer";
    private static final String CMD_POSITION   = "position";
    private static final String CMD_VISIBLE    = "visible";
    private static final String CMD_INDEX      = "index";
    private static final String CMD_BUFFER     = "buffer";
    private static final String CMD_COLOR      = "color";
    private static final String CMD_FONT       = "font";
    private static final String CMD_CLIP       = "clip";
    private static final String CMD_MOVE       = "move";
    private static final String CMD_LINE       = "line";
    private static final String CMD_RECTANGLE  = "rectangle";
    private static final String CMD_POLYGON    = "polygon";
    private static final String CMD_OVAL       = "oval";
    private static final String CMD_TEXT       = "text";
    private static final String CMD_IMAGE      = "image";
    private static final String CMD_REFRESH    = "refresh";

    private Executor() {
    }
    
    public static void execute(
            final String input, final Context context) throws IOException {
        
        if (!input.startsWith(PREFIX)) {
            context.getOut().println(input);
            return;
        }
        final String[] parts = input.substring(PREFIX.length()).split(" ");
        final String command = parts[0];
        final String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        if (CMD_BACKGROUND.equals(command)) {
            // TODO
        } else if (CMD_UPLOAD.equals(command)) {
            // TODO
        } else if (CMD_MKBUFFER.equals(command)) {
            execMkbuffer(context, input, args);
        } else if (CMD_RMBUFFER.equals(command)) {
            // TODO
        } else if (CMD_POSITION.equals(command)) {
            // TODO
        } else if (CMD_VISIBLE.equals(command)) {
            execVisible(context, input, args);
        } else if (CMD_INDEX.equals(command)) {
            // TODO
        } else if (CMD_BUFFER.equals(command)) {
            // TODO
        } else if (CMD_COLOR.equals(command)) {
            // TODO
        } else if (CMD_FONT.equals(command)) {
            // TODO
        } else if (CMD_CLIP.equals(command)) {
            // TODO
        } else if (CMD_MOVE.equals(command)) {
            // TODO
        } else if (CMD_LINE.equals(command)) {
            // TODO
        } else if (CMD_RECTANGLE.equals(command)) {
            // TODO
        } else if (CMD_POLYGON.equals(command)) {
            // TODO
        } else if (CMD_OVAL.equals(command)) {
            // TODO
        } else if (CMD_TEXT.equals(command)) {
            // TODO
        } else if (CMD_IMAGE.equals(command)) {
            // TODO
        } else if (CMD_REFRESH.equals(command)) {
            // TODO
        }
        throw new IOException("Invalid command: " + command);
    }
    
    private static void execMkbuffer(
            final Context context,
            final String input,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().makeBuffer(
                args[0], parseInt(args[1]), parseInt(args[2])));
    }
    
    private static void execVisible(
            final Context context,
            final String input,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 2, args);
        if (args.length == 1) {
            context.schedule(() -> context.getViewScreen().setVisible(
                    parseBoolean(args[0])));
        } else {
            context.schedule(() -> context.getViewScreen().setVisible(
                    args[0], parseBoolean(args[1])));
        }
    }
    
    private static void requireMinMaxArgs(
            final String input,
            final int min,
            final int max,
            final String[] args) throws IOException {
        
        if (args.length < min || args.length > max) {
            throw invalidArgList(input);
        }
    }
    
    private static IOException invalidArgList(final String input) {
        return new IOException("Invalid argument list: " + input);
    }

}
