package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A setting on the format
 * {@code /main-part/key=value,key=value,...}.
 * 
 * @author Tone Sommerland
 */
public final class ParameterizedSetting
{
    private static final String MAIN_PART_DELIM = "/";
    private static final String PARAM_DELIM = ",";
    private static final String KEY_VALUE_DELIM = "=";
    
    private final String whole;
    private final String mainPart;
    private final Map<String, String> params;
    
    public ParameterizedSetting(final String setting) throws IOException
    {
        this.whole = requireNonNull(setting, "setting");
        this.mainPart = getMainPart(setting);
        this.params = Map.copyOf(getParams(setting));
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

    private static String getMainPart(final String setting) throws IOException
    {
        if (!setting.startsWith(MAIN_PART_DELIM))
        {
            throw new IOException(setting);
        }
        final int end = setting.lastIndexOf(MAIN_PART_DELIM);
        if (end < 1)
        {
            throw new IOException(setting);
        }
        return setting.substring(1, end);
    }
    
    private static Map<String, String> getParams(final String setting)
            throws IOException
    {
        final int endOfMainPart = setting.lastIndexOf(MAIN_PART_DELIM);
        final String paramsPart = setting.substring(endOfMainPart + 1);
        if (paramsPart.isEmpty())
        {
            return Map.of();
        }
        final var params = new HashMap<String, String>();
        for (final String param : paramsPart.split(PARAM_DELIM))
        {
            addParam(params, param);
        }
        return params;
    }
    
    private static void addParam(
            final Map<? super String, ? super String> params,
            final String param) throws IOException
    {
        final int index = param.indexOf(KEY_VALUE_DELIM);
        if (index < 0)
        {
            throw new IOException(param);
        }
        final String key = param.substring(0, index);
        final String value = param.substring(index + 1);
        params.put(key, value);
    }

}
