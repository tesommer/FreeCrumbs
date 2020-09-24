package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import freecrumbs.finf.DynamicValue;
import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

public final class Command
{
    private static final String COUNT_FIELD_NAME  = "count";
    private static final String STATUS_FIELD_NAME = "status";
    private static final String OUT_FIELD_NAME    = "out";
    private static final String ERR_FIELD_NAME    = "err";
    private static final String PID_FIELD_NAME    = "pid";
    
    private static final String SEPARATOR = "-";
    
    @FunctionalInterface
    public static interface Encoder
    {
        public abstract String encode(byte[] data) throws IOException;
    }
    
    public static final class Params
    {
        private final String fieldNamePrefix;
        private final DynamicValue[][] commands;
        private final Encoder encoder;
        
        private Params(
                final String fieldNamePrefix,
                final DynamicValue[][] commands,
                final Encoder encoder)
        {
            this.fieldNamePrefix = requireNonNull(
                    fieldNamePrefix, "fieldNamePrefix");
            this.commands = Stream.of(commands)
                    .map(Params::requirePositiveLengthAndClone)
                    .toArray(DynamicValue[][]::new);
            this.encoder = requireNonNull(encoder, "encoder");
        }
        
        private static DynamicValue[] requirePositiveLengthAndClone(
                final DynamicValue[] command)
        {
            if (command.length == 0)
            {
                throw new IllegalArgumentException(
                        "Zero-length command is not allowed.");
            }
            return command.clone();
        }
        
        public Params()
        {
            this(
                    "",
                    new DynamicValue[0][0],
                    data -> new String(data, Charset.defaultCharset()));
        }
        
        public Params withFieldNamePrefix(final String fieldNamePrefix)
        {
            return new Params(fieldNamePrefix, this.commands, this.encoder);
        }
        
        public Params withCommands(final DynamicValue[][] commands)
        {
            return new Params(this.fieldNamePrefix, commands, this.encoder);
        }
        
        public Params withEncoder(final Encoder encoder)
        {
            return new Params(this.fieldNamePrefix, this.commands, encoder);
        }
    }

    private Command()
    {
    }
    
    public static Field[] fields(Params params)
    {
        final var commander = new Commander(params);
        final var fields = new ArrayList<Field>(params.commands.length * 4 + 1);
        fields.add(Field.simple(
                params.fieldNamePrefix + COUNT_FIELD_NAME,
                file -> String.valueOf(params.commands.length)));
        for (int i = 0; i < params.commands.length; i++)
        {
            addCommandFields(params, i, commander, fields);
        }
        return fields.toArray(Field[]::new);
    }
    
    private static void addCommandFields(
            final Params params,
            final int commandIndex,
            final Commander commander,
            final Collection<? super Field> fields)
    {
        final String prefix = prefix(params, commandIndex);
        fields.add(Field.computed(
                prefix + STATUS_FIELD_NAME,
                new CommandComputation(
                        commander, commandIndex, Command::status)));
        fields.add(Field.computed(
                prefix + OUT_FIELD_NAME,
                new CommandComputation(
                        commander, commandIndex, Command::out)));
        fields.add(Field.computed(
                prefix + ERR_FIELD_NAME,
                new CommandComputation(
                        commander, commandIndex, Command::err)));
        fields.add(Field.computed(
                prefix + PID_FIELD_NAME,
                new CommandComputation(
                        commander, commandIndex, Command::pid)));
    }
    
    private static String prefix(final Params params, final int commandIndex)
    {
        return params.fieldNamePrefix + (
                    commandIndex == params.commands.length - 1
                        ? ""
                        : String.valueOf(commandIndex + 1) + SEPARATOR
                );
    }
    
    private static String status(final Completion completion)
    {
        return String.valueOf(completion.status());
    }
    
    private static String out(final Completion completion) throws IOException
    {
        return completion.out();
    }
    
    private static String err(final Completion completion) throws IOException
    {
        return completion.err();
    }
    
    private static String pid(final Completion completion)
    {
        return String.valueOf(completion.pid());
    }
    
    @FunctionalInterface
    private static interface CompletionValue
    {
        public abstract String apply(Completion completion) throws IOException;
    }
    
    private static final class CommandComputation implements FieldComputation
    {
        private final Commander commander;
        private final int commandIndex;
        private final CompletionValue value;
        
        private CommandComputation(
                final Commander commander,
                final int commandIndex,
                final CompletionValue value)
        {
            assert commander    != null;
            assert commandIndex >= 0
                && commandIndex < commander.params.commands.length;
            assert value        != null;
            this.commander    = commander;
            this.commandIndex = commandIndex;
            this.value        = value;
        }

        @Override
        public void reset(final File file) throws IOException
        {
            commander.reset(this, file);
        }

        @Override
        public boolean update(
                final byte[] input,
                final int offset,
                final int length) throws IOException
        {
            return false;
        }

        @Override
        public String finish() throws IOException
        {
            return value.apply(commander.finish(this).get(commandIndex));
        }
    }
    
    private static final class Commander
    {
        private final List<Completion> completions = new ArrayList<>();
        private final Params params;
        private Object master;
        private List<Process> pipeline;

        private Commander(final Params params)
        {
            assert params != null;
            this.params = params;
        }
        
        private void reset(final Object caller, final File file)
                throws IOException
        {
            if (master != null)
            {
                return;
            }
            master = caller;
            completions.clear();
            pipeline = ProcessBuilder.startPipeline(
                    builders(params.commands, file));
        }
        
        private static List<ProcessBuilder> builders(
                final DynamicValue[][] commands,
                final File file) throws IOException
        {
            final var builders = new ArrayList<ProcessBuilder>(commands.length);
            for (final var command : commands)
            {
                builders.add(builder(command, file));
            }
            return builders;
        }
        
        private static ProcessBuilder builder(
                final DynamicValue[] command,
                final File file) throws IOException
        {
            final var stringCommand = new String[command.length];
            for (int i = 0; i < stringCommand.length; i++)
            {
                stringCommand[i] = command[i].get(file);
            }
            return new ProcessBuilder(stringCommand);
        }
        
        private List<Completion> finish(final Object caller) throws IOException
        {
            if (master == null)
            {
                return completions;
            }
            master = null;
            for (final Process process : pipeline)
            {
                completions.add(new Completion(params, process));
            }
            return completions;
        }
    }
    
    private static final class Completion
    {
        private final Params params;
        private final Process process;
        private final int status;
        
        private Completion(final Params params, final Process process)
                throws IOException
        {
            assert params  != null;
            assert process != null;
            this.params  = params;
            this.process = process;
            try
            {
                this.status = process.waitFor();
            }
            catch (final InterruptedException ex)
            {
                Thread.currentThread().interrupt();
                throw new IOException(ex);
            }
        }
        
        private int status()
        {
            return status;
        }
        
        private String out() throws IOException
        {
            return params.encoder.encode(
                    process.getInputStream().readAllBytes());
        }
        
        private String err() throws IOException
        {
            return params.encoder.encode(
                    process.getErrorStream().readAllBytes());
        }
        
        private long pid()
        {
            return process.pid();
        }
    }

}
