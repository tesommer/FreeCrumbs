package freecrumbs.finf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The entry point to Finf.
 * 
 * @author Tone Sommerland
 */
public final class Main {
    
    private static final String
    HELP
        = "@finfhelp@";
    
    private static final String CONFIG_FILE_OPTION = "-c";
    private static final String CONFIG_OVERRIDE_OPTION = "-o";
    private static final String HELP_OPTION = "-h";

    private Main() {
    }
    
    public static void main(final String[] args) throws IOException {
        final Args parsedArgs = parseArgs(args);
        if (parsedArgs == null) {
            System.out.println(HELP);
            return;
        }
        final Collection<File> inputFiles = getInputFiles(parsedArgs);
        final Config config = loadConfig(parsedArgs);
        Finf.output(inputFiles, config, System.out);
    }

    private static Collection<File> getInputFiles(final Args parsedArgs) {
        final Collection<File> inputFiles = new ArrayList<>();
        parsedArgs.inputFiles.stream()
            .map(File::new)
            .forEach(file -> addTree(inputFiles, file));
        return inputFiles;
    }
    
    private static void addTree(
            final Collection<? super File> files, final File file) {
        
        if (file.isDirectory()) {
            Stream.of(file.listFiles()).forEach(child -> addTree(files, child));
        } else {
            files.add(file);
        }
    }
    
    private static Config loadConfig(final Args args) throws IOException {
        final ConfigLoader loader
            = ConfigLoader.getDefault(getConfigOverrides(args));
        if (args.configFile == null) {
            return loader.loadConfig(new StringReader(""));
        } else if ("-".equals(args.configFile)) {
            return loader.loadConfig(new InputStreamReader(System.in));
        }
        try (
            final Reader reader
                = new InputStreamReader(new FileInputStream(args.configFile));
        ) {
            return loader.loadConfig(reader);
        }
    }
    
    private static Map<String, String> getConfigOverrides(final Args args) {
        final Map<String, String> overrides = new HashMap<>();
        args.configOverrides.forEach(
                override -> addOverride(overrides, override));
        return overrides;
    }

    private static void addOverride(
            final Map<? super String, ? super String> overrides,
            final String override) {
        
        final String key;
        final String value;
        final int indexOfEquals = override.indexOf('=');
        if (indexOfEquals < 0) {
            key = override;
            value = null;
        } else {
            key = override.substring(0, indexOfEquals);
            value = override.substring(indexOfEquals + 1);
        }
        overrides.put(key, value);
    }
    
    /**
     * Returns null if help option or if args contains error.
     */
    private static Args parseArgs(final String[] args) {
        String configFile = null;
        final Collection<String> configOverrides = new ArrayList<>();
        final Collection<String> inputFiles = new ArrayList<>();
        int i = -1;
        while (++i < args.length) {
            if (HELP_OPTION.equals(args[i])) {
                return null;
            } else if (CONFIG_FILE_OPTION.equals(args[i])) {
                if (i == args.length - 1 || configFile != null) {
                    return null;
                }
                configFile = args[++i];
            } else if (CONFIG_OVERRIDE_OPTION.equals(args[i])) {
                if (i == args.length - 1) {
                    return null;
                }
                configOverrides.add(args[++i]);
            } else {
                inputFiles.addAll(Arrays.asList(args).subList(i, args.length));
                break;
            }
        }
        return new Args(configFile, configOverrides, inputFiles);
    }
    
    private static final class Args {
        final String configFile;
        final Collection<String> configOverrides;
        final Collection<String> inputFiles;

        public Args(
                final String configFile,
                final Collection<String> configOverrides,
                final Collection<String> inputFiles) {
            
            this.configFile = configFile;
            this.configOverrides = new ArrayList<>(configOverrides);
            this.inputFiles = new ArrayList<>(inputFiles);
        }
    }

}
