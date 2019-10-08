package freecrumbs.viewscreen;

import static freecrumbs.viewscreen.Arguments.parseBoolean;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.calclipse.lib.util.EncodingUtil;

public final class Executor {
    
    private static final String PREFIX = "vsc:";

    private static final String CMD_INIT       = "init";
    private static final String CMD_SETVAR     = "setvar";
    private static final String CMD_RMVAR      = "rmvar";
    private static final String CMD_SAMPLE     = "sample";
    private static final String CMD_MKBUFFER   = "mkbuffer";
    private static final String CMD_RMBUFFER   = "rmbuffer";
    private static final String CMD_UPLOAD     = "upload";
    private static final String CMD_DOWNLOAD   = "download";
    private static final String CMD_BACKGROUND = "background";
    private static final String CMD_POSITION   = "position";
    private static final String CMD_VISIBLE    = "visible";
    private static final String CMD_INDEX      = "index";
    private static final String CMD_BEGIN      = "begin";
    private static final String CMD_END        = "end";
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
    private static final String CMD_EXIT       = "exit";

    private Executor() {
    }
    
    public static void execute(
            final String input,
            final ExecutionContext context) throws IOException {
        
        if (!input.startsWith(PREFIX)) {
            context.getOut().println(input);
            return;
        }
        final String[] parts = input.substring(PREFIX.length()).split(" ");
        final String command = parts[0];
        final String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        if (command.equals(CMD_INIT)) {
            execInit(input, context, args);
        } else if (command.equals(CMD_SETVAR)) {
            execSetvar(input, context, args);
        } else if (command.equals(CMD_RMVAR)) {
            execRmvar(input, context, args);
        } else if (command.equals(CMD_SAMPLE)) {
            execSample(input, context, args);
        } else if (command.equals(CMD_MKBUFFER)) {
            execMkbuffer(input, context, args);
        } else if (command.equals(CMD_RMBUFFER)) {
            execRmbuffer(input, context, args);
        } else if (command.equals(CMD_UPLOAD)) {
            execUpload(input, context, args);
        } else if (command.equals(CMD_DOWNLOAD)) {
            execDownload(input, context, args);
        } else if (command.equals(CMD_BACKGROUND)) {
            execBackground(input, context, args);
        } else if (command.equals(CMD_POSITION)) {
            execPosition(input, context, args);
        } else if (command.equals(CMD_VISIBLE)) {
            execVisible(input, context, args);
        } else if (command.equals(CMD_INDEX)) {
            execIndex(input, context, args);
        } else if (command.equals(CMD_BEGIN)) {
            execBegin(input, context, args);
        } else if (command.equals(CMD_END)) {
            execEnd(input, context, args);
        } else if (command.equals(CMD_BUFFER)) {
            execBuffer(input, context, args);
        } else if (command.equals(CMD_COLOR)) {
            execColor(input, context, args);
        } else if (command.equals(CMD_FONT)) {
            execFont(input, context, args);
        } else if (command.equals(CMD_CLIP)) {
            execClip(input, context, args);
        } else if (command.equals(CMD_MOVE)) {
            execMove(input, context, args);
        } else if (command.equals(CMD_LINE)) {
            execLine(input, context, args);
        } else if (command.equals(CMD_RECTANGLE)) {
            execRectangle(input, context, args);
        } else if (command.equals(CMD_POLYGON)) {
            execPolygon(input, context, args);
        } else if (command.equals(CMD_OVAL)) {
            execOval(input, context, args);
        } else if (command.equals(CMD_TEXT)) {
            execText(input, context, args);
        } else if (command.equals(CMD_IMAGE)) {
            execImage(input, context, args);
        } else if (command.equals(CMD_REFRESH)) {
            execRefresh(input, context, args);
        } else if (command.equals(CMD_EXIT)) {
            execExit(input, context, args);
        } else {
            throw new IOException("Invalid command: " + command);
        }
    }
    
    /******************
     * Initialization *
     ******************/
    
    private static void execInit(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 4, args);
        if (args.length == 4) {
            context.schedule(() -> context.getViewScreen().init(
                    parseBoolean(args[0]),
                    parseBoolean(args[1]),
                    args[2],
                    args[3]));
        } else if (args.length == 2) {
            context.schedule(() -> context.getViewScreen().init(
                    parseBoolean(args[0]), parseBoolean(args[1])));
        } else {
            throw invalidArgList(input);
        }
    }
    
    /*************
     * Variables *
     *************/
    
    private static void execSetvar(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, Integer.MAX_VALUE, args);
        context.schedule(() -> context.getViewScreen().setVariable(
                args[0], Arrays.copyOfRange(args, 1, args.length)));
    }
    
    private static void execRmvar(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().removeVariable(args[0]));
    }
    
    private static void execSample(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 7, 7, args);
        context.schedule(() -> context.getViewScreen().sample(
                args[0], args[1], args[2], args[3], args[4], args[5], args[6]));
    }
    
    /***********
     * Buffers *
     ***********/
    
    private static void execMkbuffer(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().makeBuffer(
                args[0], args[1], args[2]));
    }
    
    private static void execRmbuffer(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().removeBuffer(args[0]));
    }
    
    private static void execUpload(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        try {
            final byte[] imageData = EncodingUtil.hexToBytes(args[1]);
            context.schedule(() -> context.getViewScreen().upload(
                    args[0], imageData));
        } catch (final IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }
    
    private static void execDownload(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 3, args);
        final String file = args[args.length - 1];
        final OutputStream out;
        if (file.equals("-")) {
            out = context.getOut();
        } else {
            out = new FileOutputStream(file);
        }
        if (args.length == 3) {
            context.schedule(() -> context.getViewScreen().download(
                    args[0], args[1], out));
        } else {
            context.schedule(() -> context.getViewScreen().download(
                    args[0], out));
        }
    }
    
    /*****************************
     * Frame & buffer properties *
     *****************************/
    
    private static void execBackground(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().setBackground(
                args[0], args[1], args[2]));
    }
    
    private static void execPosition(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 3, args);
        if (args.length == 3) {
            context.schedule(() -> context.getViewScreen().setPosition(
                    args[0], args[1], args[2]));
        } else {
            context.schedule(() -> context.getViewScreen().setPosition(
                    args[0], args[1]));
        }
    }
    
    private static void execVisible(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        context.schedule(() -> context.getViewScreen().setVisible(
                args[0], parseBoolean(args[1])));
    }
    
    private static void execIndex(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        context.schedule(() -> context.getViewScreen().setIndex(
                args[0], args[1]));
    }
    
    /*******************
     * Drawing context *
     *******************/
    
    private static void execBegin(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 0, 0, args);
        context.schedule(() -> context.getViewScreen().begin());
    }
    
    private static void execEnd(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 0, 0, args);
        context.schedule(() -> context.getViewScreen().end());
    }
    
    private static void execBuffer(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().setBuffer(args[0]));
    }
    
    private static void execColor(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 4, args);
        if (args.length == 4) {
            context.schedule(() -> context.getViewScreen().setColor(
                    args[0], args[1], args[2], args[3]));
        } else {
            context.schedule(() -> context.getViewScreen().setColor(
                    args[0], args[1], args[2]));
        }
    }
    
    private static void execFont(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 4, 4, args);
        context.schedule(() -> context.getViewScreen().setFont(
                args[0],
                args[1],
                parseBoolean(args[2]),
                parseBoolean(args[3])));
    }
    
    private static void execClip(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 4, 4, args);
        context.schedule(() -> context.getViewScreen().setClip(
                args[0], args[1], args[2], args[3]));
    }
    
    /***********
     * Drawing *
     ***********/
    
    private static void execMove(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        context.schedule(() -> context.getViewScreen().move(args[0], args[1]));
    }
    
    private static void execLine(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 2, 2, args);
        context.schedule(() -> context.getViewScreen().line(args[0], args[1]));
    }
    
    private static void execRectangle(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().rectangle(
                parseBoolean(args[0]), args[1], args[2]));
    }
    
    private static void execPolygon(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, Integer.MAX_VALUE, args);
        context.schedule(() -> context.getViewScreen().polygon(
                parseBoolean(args[0]),
                Arrays.copyOfRange(args, 1, args.length)));
    }
    
    private static void execOval(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 3, 3, args);
        context.schedule(() -> context.getViewScreen().oval(
                parseBoolean(args[0]), args[1], args[2]));
    }
    
    private static void execText(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 1, args);
        context.schedule(() -> context.getViewScreen().text(args[0]));
    }
    
    private static void execImage(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 1, 3, args);
        if (args.length == 3) {
            context.schedule(() -> context.getViewScreen().image(
                    args[0], args[1], args[2]));
        } else if (args.length == 1) {
            context.schedule(() -> context.getViewScreen().image(
                    args[0]));
        } else {
            throw invalidArgList(input);
        }
    }
    
    private static void execRefresh(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 0, 0, args);
        context.schedule(() -> context.getViewScreen().refresh());
    }
    
    /********
     * Exit *
     ********/
    
    private static void execExit(
            final String input,
            final ExecutionContext context,
            final String[] args) throws IOException {
        
        requireMinMaxArgs(input, 0, 0, args);
        context.schedule(() -> System.exit(0));
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
            throw new IOException(
                    "Invalid number of arguments: "
                            + args.length
                            + " (expected min="
                            + min
                            + ", max="
                            + max
                            + "): "
                            + input);
        }
    }
    
    private static IOException invalidArgList(final String input) {
        return new IOException("Invalid argument list: " + input);
    }

}
