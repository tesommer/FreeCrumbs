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
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        final Config config = loadConfig(parsedArgs, Locale.getDefault());
        final List<Info> infoList = new ArrayList<>();
        for (final String inputFile : parsedArgs.inputFiles) {
            process(new File(inputFile), infoList, config);
        }
        Finf.output(infoList, config, System.out);
    }
    
    private static void process(
        final File file,
        final Collection<Info> infoList,
        final Config config) throws IOException {
        
        if (file.isDirectory()) {
            processDir(file, infoList, config);
        } else if (Finf.acceptsInput(file, config)) {
            processFile(file, infoList, config);
        }
    }
    
    private static void processFile(
        final File file,
        final Collection<Info> infoList,
        final Config config) throws IOException {
        
        infoList.add(Finf.getInfo(file, config));
    }
    
    private static void processDir(
        final File dir,
        final Collection<Info> infoList,
        final Config config) throws IOException {
        
        for (final File file : dir.listFiles()) {
            process(file, infoList, config);
        }
    }
    
    private static Config loadConfig(final Args args, final Locale locale)
            throws IOException {
        
        final ConfigLoader loader
            = new PropertiesConfigLoader(locale, getConfigOverrides(args));
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
    
    private static Map<String, String> getConfigOverrides(final Args args)
            throws IOException {
        
        final Map<String, String> overrides = new HashMap<>();
        for (final String override : args.configOverrides) {
            final int indexOfEquals = override.indexOf('=');
            if (indexOfEquals < 0) {
                throw new IOException(override);
            }
            final String key = override.substring(0, indexOfEquals);
            final String value = override.substring(indexOfEquals + 1);
            overrides.put(key, value);
        }
        return overrides;
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
