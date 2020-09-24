package freecrumbs.finf.config.command;

import java.io.IOException;
import java.util.ArrayList;

import freecrumbs.finf.DynamicValue;
import freecrumbs.finf.config.AvailableFields;
import freecrumbs.finf.config.ParameterizedSetting;
import freecrumbs.finf.config.Settings;
import freecrumbs.finf.config.TokenFormatter;
import freecrumbs.finf.field.Command;

public final class CommandParser
{
    private static final String MAIN_PART_DELIM = "`";
    private static final String PIPE            = "|";
    
    private CommandParser()
    {
    }
    
    /**
     * Whether or not the given parameterized setting is a command.
     */
    public static boolean isCommand(final String setting)
    {
        return ParameterizedSetting.isMainPartDelim(MAIN_PART_DELIM, setting);
    }
    
    public static AvailableFields withAnotherCommand(
            final AvailableFields availableFields,
            final Command.Params initialCommandParams,
            final String setting) throws IOException
    {
        final var parameterized = new ParameterizedSetting(
                setting, MAIN_PART_DELIM);
        final Command.Params commandParamsWithCommands =  initialCommandParams
                .withCommands(commands(parameterized, availableFields));
        final Command.Params resultingCommandParams = remainingCommandParams(
                commandParamsWithCommands, parameterized);
        return availableFields.coCaching(
                availableFields.params().withAnotherCommand(
                        resultingCommandParams));
        
    }
    
    private static DynamicValue[][] commands(
            final ParameterizedSetting parameterized,
            final AvailableFields availableFields) throws IOException
    {
        final var commands = new ArrayList<DynamicValue[]>();
        final var command = new ArrayList<DynamicValue>();
        for (final String term
                : Settings.splitAtWhitespace(parameterized.mainPart()))
        {
            if (PIPE.equals(term))
            {
                addCommand(
                        command, commands, emptyCommandMessage(parameterized));
                command.clear();
            }
            else
            {
                addTerm(term, availableFields, command);
            }
        }
        addCommand(command, commands, emptyCommandMessage(parameterized));
        return commands.stream().toArray(DynamicValue[][]::new);
    }

    private static void addCommand(
            final ArrayList<DynamicValue> command,
            final ArrayList<DynamicValue[]> commands,
            final String message) throws IOException
    {
        if (command.isEmpty())
        {
            throw new IOException(message);
        }
        commands.add(command.stream().toArray(DynamicValue[]::new));
    }

    private static void addTerm(
            final String term,
            final AvailableFields availableFields,
            final ArrayList<DynamicValue> command) {
        
        final var formatter = new TokenFormatter(term);
        final String[] used = formatter.usedFieldNames(availableFields.names());
        command.add(DynamicValue.of(availableFields.readerOf(used), formatter));
    }
    
    private static Command.Params remainingCommandParams(
            final Command.Params commandParams,
            final ParameterizedSetting parameterized) throws IOException
    {
        return commandParams;
    }
    
    private static String emptyCommandMessage(
            final ParameterizedSetting parameterized)
    {
        return "Empty command: " + parameterized.whole();
    }

}
