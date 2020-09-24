package freecrumbs.finf.config;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A setting on a format similar to
 * {@code /main-part/key=value,key=value,...}.
 * The first char is the main delimiter.
 * The last occurrence of the main delimiter
 * is followed by an optional parameter list.
 * 
 * @author Tone Sommerland
 */
public final class ParameterizedSetting
{
    private static final String DEFAULT_PARAM_DELIM     = ",";
    private static final String DEFAULT_KEY_VALUE_DELIM = "=";
    
    private final String whole;
    private final String mainPartDelim;
    private final String paramDelim;
    private final String keyValueDelim;
    private final String mainPart;
    private final Map<String, String> params;
    
    public ParameterizedSetting(
            final String setting,
            final String mainPartDelim,
            final String paramDelim,
            final String keyValueDelim) throws IOException
    {
        this.whole         = requireNonNull(setting,       "setting");
        this.mainPartDelim = requireNonNull(mainPartDelim, "mainPartDelim");
        this.paramDelim    = requireNonNull(paramDelim,    "paramDelim");
        this.keyValueDelim = requireNonNull(keyValueDelim, "keyValueDelim");
        this.mainPart      = mainPart(setting);
        this.params        = Map.copyOf(params(setting));
    }
    
    public ParameterizedSetting(
            final String setting, final String mainPartDelim) throws IOException
    {
        this(
                setting,
                mainPartDelim,
                DEFAULT_PARAM_DELIM,
                DEFAULT_KEY_VALUE_DELIM);
    }
    
    /**
     * Returns {@code true} if the given setting
     * has the given delimiter as its main delimiter.
     * @param delim the main-delimiter candidate
     * @param setting the parameterized setting
     */
    public static boolean isMainPartDelim(
            final String delim, final String setting)
    {
        return setting.startsWith(delim);
    }
    
    public String whole()
    {
        return whole;
    }

    public String mainPart()
    {
        return mainPart;
    }

    public Map<String, String> params()
    {
        return params;
    }

    private String mainPart(final String setting) throws IOException
    {
        if (!setting.startsWith(mainPartDelim))
        {
            throw new IOException(setting);
        }
        final int end = setting.lastIndexOf(mainPartDelim);
        if (end < 1)
        {
            throw new IOException(setting);
        }
        return setting.substring(mainPartDelim.length(), end);
    }
    
    private Map<String, String> params(final String setting) throws IOException
    {
        final int endOfMainPart = setting.lastIndexOf(mainPartDelim);
        final String paramsPart = setting.substring(
                endOfMainPart + mainPartDelim.length());
        if (paramsPart.isEmpty())
        {
            return Map.of();
        }
        final var params = new HashMap<String, String>();
        for (final String param : paramsPart.split(paramDelim))
        {
            addParam(param, params);
        }
        return params;
    }
    
    private void addParam(
            final String param,
            final Map<? super String, ? super String> params) throws IOException
    {
        final int index = param.indexOf(keyValueDelim);
        if (index < 0)
        {
            throw new IOException(param);
        }
        final String key = param.substring(0, index);
        final String value = param.substring(index + keyValueDelim.length());
        params.put(key, value);
    }

}
