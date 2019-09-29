package freecrumbs.viewscreen;

import static freecrumbs.viewscreen.Arguments.parseBoolean;
import static freecrumbs.viewscreen.Arguments.parseColor;
import static freecrumbs.viewscreen.Arguments.parseInt;

import java.awt.Font;
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
            execBackground(input, context, args);
        } else if (CMD_UPLOAD.equals(command)) {
            execUpload(input, context, args);
        } else if (CMD_MKBUFFER.equals(command)) {
            execMkbuffer(input, context, args);
        } else if (CMD_RMBUFFER.equals(command)) {
            execRmbuffer(input, context, args);
        } else if (CMD_POSITION.equals(command)) {
            execPosition(input, context, args);
        } else if (CMD_VISIBLE.equals(command)) {
            execVisible(input, context, args);
        } else if (CMD_INDEX.equals(command)) {
            execIndex(input, context, args);
        } else if (CMD_BUFFER.equals(command)) {
            execBuffer(input, context, args);
        } else if (CMD_COLOR.equals(command)) {
            execColor(input, context, args);
        } else if (CMD_FONT.equals(command)) {
            execFont(input, context, args);
        } else if (CMD_CLIP.equals(command)) {
            execClip(input, context, args);
        } else if (CMD_MOVE.equals(command)) {
            execMove(input, context, args);
        } else if (CMD_LINE.equals(command)) {
            execLine(input, context, args);
        } else if (CMD_RECTANGLE.equals(command)) {
            execRectangle(input, context, args);
        } else if (CMD_POLYGON.equals(command)) {
            execPolygon(input, context, args);
        } else if (CMD_OVAL.equals(command)) {
            execOval(input, context, args);
        } else if (CMD_TEXT.equals(command)) {
            execText(input, context, args);
        } else if (CMD_IMAGE.equals(command)) {
            execImage(input, context, args);
        } else if (CMD_REFRESH.equals(command)) {
            execRefresh(input, context, args);
        } else {
            throw new IOException("Invalid command: " + command);
        }
    }
    
    /**********************
     * Command executions *
     **********************/
    
    private static void execBackground(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().setBackground(
                parseColor(args[0])));
    }
    
    private static void execUpload(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        // TODO something witchy
    }
    
    private static void execMkbuffer(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().makeBuffer(
                args[0], parseInt(args[1]), parseInt(args[2])));
    }
    
    private static void execRmbuffer(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().removeBuffer(args[0]));
    }
    
    private static void execPosition(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 3, args);
        if (args.length == 2) {
            context.schedule(() -> context.getViewScreen().setPosition(
                    parseInt(args[0]), parseInt(args[1])));
        } else {
            context.schedule(() -> context.getViewScreen().setPosition(
                    args[0], parseInt(args[1]), parseInt(args[2])));
        }
    }
    
    private static void execVisible(
            final String input,
            final Context context,
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
    
    private static void execIndex(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        context.schedule(() -> context.getViewScreen().setIndex(
                args[0], args[1]));
    }
    
    private static void execBuffer(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().setBuffer(args[0]));
    }
    
    private static void execColor(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().setColor(
                parseColor(args[0])));
    }
    
    private static void execFont(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 4, 4, args);
        int style = parseBoolean(args[2]) ? Font.BOLD : 0;
        if (parseBoolean(args[3])) {
            style |= Font.ITALIC;
        }
        final var font = new Font(args[0], parseInt(args[1]), style);
        context.schedule(() -> context.getViewScreen().setFont(font));
    }
    
    private static void execClip(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 4, 4, args);
        context.schedule(() -> context.getViewScreen().setClip(
                parseInt(args[0]),
                parseInt(args[1]),
                parseInt(args[2]),
                parseInt(args[3])));
    }
    
    private static void execMove(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        context.schedule(() -> context.getViewScreen().move(
                parseInt(args[0]), parseInt(args[1])));
    }
    
    private static void execLine(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        context.schedule(() -> context.getViewScreen().line(
                parseInt(args[0]), parseInt(args[1])));
    }
    
    private static void execRectangle(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().rectangle(
                parseBoolean(args[0]), parseInt(args[1]), parseInt(args[2])));
    }
    
    private static void execPolygon(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, Integer.MAX_VALUE, args);
        final var xy = new int[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            xy[i - 1] = parseInt(args[i]);
        }
        context.schedule(() -> context.getViewScreen().polygon(
                parseBoolean(args[0]), xy));
    }
    
    private static void execOval(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().oval(
                parseBoolean(args[0]), parseInt(args[1]), parseInt(args[2])));
    }
    
    private static void execText(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().text(args[0]));
    }
    
    private static void execImage(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 3, args);
        final int width = args.length > 1 ? parseInt(args[1]) : -1;
        final int height = args.length > 2 ? parseInt(args[2]) : -1;
        context.schedule(() -> context.getViewScreen().image(
                args[0], width, height));
    }
    
    private static void execRefresh(
            final String input,
            final Context context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 0, 0, args);
        context.schedule(() -> context.getViewScreen().refresh());
    }
    
    /*********
     * Misc. *
     *********/
    
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
