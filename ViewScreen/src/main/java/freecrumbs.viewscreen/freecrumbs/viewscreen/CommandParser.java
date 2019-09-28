package freecrumbs.viewscreen;

import static freecrumbs.viewscreen.Arguments.parseBoolean;
import static freecrumbs.viewscreen.Arguments.parseInt;

import java.io.IOException;
import java.util.Arrays;

import freecrumbs.viewscreen.command.Line;
import freecrumbs.viewscreen.command.MakeBuffer;
import freecrumbs.viewscreen.command.Move;
import freecrumbs.viewscreen.command.Refresh;
import freecrumbs.viewscreen.command.SetBuffer;
import freecrumbs.viewscreen.command.Visible;

public final class CommandParser {
    private static final String PREFIX = "vsc:";
    private static final String CMD_MAKE_BUFFER = "mkbuffer";
    private static final String CMD_VISIBLE = "visible";
    private static final String CMD_SET_BUFFER = "buffer";
    private static final String CMD_MOVE = "move";
    private static final String CMD_LINE = "line";
    private static final String CMD_REFRESH = "refresh";

    private CommandParser() {
    }
    
    public static Command parse(final String input) throws IOException {
        if (!input.startsWith(PREFIX)) {
            return context -> context.getOut().println(input);
        }
        final String[] parts = input.substring(PREFIX.length()).split(" ");
        final String command = parts[0];
        final String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        if (CMD_MAKE_BUFFER.equals(command)) {
            return getMakeBuffer(input, args);
        } else if (CMD_VISIBLE.equals(command)) {
            return getVisible(input, args);
        } else if (CMD_SET_BUFFER.equals(command)) {
            return getSetBuffer(input, args);
        } else if (CMD_MOVE.equals(command)) {
            return getMove(input, args);
        } else if (CMD_LINE.equals(command)) {
            return getLine(input, args);
        } else if (CMD_REFRESH.equals(command)) {
            return getRefresh(input, args);
        }
        throw new IOException("Invalid command: " + command);
    }
    
    private static Command getMakeBuffer(
            final String input, final String[] args) throws IOException {
        
        if (args.length == 3) {
            return MakeBuffer.getInstance(
                    args[0], parseInt(args[1]), parseInt(args[2]));
        }
        throw invalidArgList(input);
    }
    
    private static Command getVisible(final String input, final String[] args)
            throws IOException {
        
        if (args.length == 1) {
            return Visible.getInstance(parseBoolean(args[0]));
        } else if (args.length == 2) {
            return Visible.getInstance(args[0], parseBoolean(args[1]));
        }
        throw invalidArgList(input);
    }
    
    private static Command getSetBuffer(final String input, final String[] args)
            throws IOException {
        
        if (args.length == 1) {
            return SetBuffer.getInstance(args[0]);
        }
        throw invalidArgList(input);
    }
    
    private static Command getMove(final String input, final String[] args)
            throws IOException {
        
        if (args.length == 2) {
            return Move.getInstance(parseInt(args[0]), parseInt(args[1]));
        }
        throw invalidArgList(input);
    }
    
    private static Command getLine(final String input, final String[] args)
            throws IOException {
        
        if (args.length == 2) {
            return Line.getInstance(parseInt(args[0]), parseInt(args[1]));
        }
        throw invalidArgList(input);
    }
    
    private static Command getRefresh(final String input, final String[] args)
            throws IOException {
        
        if (args.length == 0) {
            return Refresh.INSTANCE;
        }
        throw invalidArgList(input);
    }
    
    private static IOException invalidArgList(final String input) {
        return new IOException("Invalid argument list: " + input);
    }

}
