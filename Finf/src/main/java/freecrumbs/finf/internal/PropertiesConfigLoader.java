package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import freecrumbs.finf.Config;
import freecrumbs.finf.ConfigLoader;
import freecrumbs.finf.HashGenerator;
import freecrumbs.finf.Info;
import freecrumbs.finf.InfoField;
import freecrumbs.finf.InfoFormat;

/**
 * Loads configuration from a properties file.
 * Sample file:
 * <pre>
 * {@code
 * hash.algorithm=SHA-256
 * info.format=${path}${filename} ${size} ${modified} ${hash}
 * date.format=yyyy-MM-dd HH:mm
 * file.filter=.*\.html
 * order=filename size asc modified desc
 * count=100
 * }
 * </pre>
 * 
 * @author Tone Sommerland
 */
public class PropertiesConfigLoader implements ConfigLoader {
    
    public static final String HASH_ALGORITHM_KEY = "hash.algorithm";
    public static final String INFO_FORMAT_KEY = "info.format";
    public static final String DATE_FORMAT_KEY = "date.format";
    public static final String FILE_FILTER_KEY = "file.filter";
    public static final String ORDER_KEY = "order";
    public static final String COUNT_KEY = "count";
    
    private static final String DEFAULT_HASH_ALGORITHM = "MD5";
    private static final String DEFAULT_INFO_FORMAT = "${filename}";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private static final String PATH_TOKEN = "${path}";
    private static final String FILENAME_TOKEN = "${filename}";
    private static final String SIZE_TOKEN = "${size}";
    private static final String MODIFIED_TOKEN = "${modified}";
    private static final String HASH_TOKEN = "${hash}";
    
    private static final int REGEX_FLAGS = 0;
    
    private final Locale locale;
    private final Map<String, String> overrides;

    /**
     * Creates a properties config loader.
     * @param overrides overrides keys in the properties file
     */
    public PropertiesConfigLoader(
            final Locale locale, final Map<String, String> overrides) {
        
        this.locale = requireNonNull(locale, "locale");
        this.overrides = new HashMap<>(overrides);
    }
    
    /**
     * Creates a properties config loader.
     */
    public PropertiesConfigLoader(final Locale locale) {
        this(locale, new HashMap<>());
    }
    
    @Override
    public Config loadConfig(final Reader reader) throws IOException {
        final Properties props = getProperties(reader);
        final TokenInfoFormat infoFormat = getInfoFormat(props);
        final HashGenerator hashGenerator;
        if (infoFormat.isHashUnused()) {
            hashGenerator = in -> new byte[] {};
        } else {
            hashGenerator = getHashGenerator(props);
        }
        final FileFilter fileFilter = getFileFilter(props);
        final Comparator<Info> order = getOrder(props);
        final int count = getCount(props);
        return new Config(
                hashGenerator,
                infoFormat,
                fileFilter,
                order,
                count);
    }

    private Properties getProperties(final Reader reader) throws IOException {
        final Properties props = new Properties();
        props.load(reader);
        for (final String key : overrides.keySet()) {
            props.put(key, overrides.get(key));
        }
        return props;
    }

    private static HashGenerator getHashGenerator(final Properties props) {
        return new MessageDigestHashGenerator(
                props.getProperty(HASH_ALGORITHM_KEY, DEFAULT_HASH_ALGORITHM));
    }

    private TokenInfoFormat getInfoFormat(final Properties props)
            throws IOException {
        
        return new TokenInfoFormat(
                props.getProperty(INFO_FORMAT_KEY, DEFAULT_INFO_FORMAT),
                props.getProperty(DATE_FORMAT_KEY, DEFAULT_DATE_FORMAT),
                locale);
    }

    private static FileFilter getFileFilter(final Properties props)
            throws IOException {
        
        final String regex = props.getProperty(FILE_FILTER_KEY);
        if (regex == null) {
            return null;
        }
        return new RegexFileFilter(regex, REGEX_FLAGS);
    }

    private Comparator<Info> getOrder(final Properties props) {
        final String orderProp = props.getProperty(ORDER_KEY);
        if (orderProp == null) {
            return null;
        }
        return new OrderSpecInfoSorter(getOrderSpecs(orderProp, locale));
    }

    private static int getCount(final Properties props) throws IOException {
        try {
            return Integer.parseInt(props.getProperty(COUNT_KEY, "-1"));
        } catch (final NumberFormatException ex) {
            throw new IOException(ex);
        }
    }
    
    private static List<OrderSpec> getOrderSpecs(
            final String order, final Locale locale) {
        
        final String orderTLC = order.toLowerCase(locale);
        final List<OrderSpec> orderSpecs
            = new ArrayList<>(InfoField.values().length);
        for (final InfoField field : InfoField.values()) {
            boolean desc = false;
            final String name = field.name().toLowerCase(locale);
            int precedence = orderTLC.indexOf(name + " asc");
            if (precedence < 0) {
                precedence = orderTLC.indexOf(name + " desc");
                if (precedence > -1) {
                    desc = true;
                } else {
                    precedence = orderTLC.indexOf(name);
                }
            }
            if (precedence > -1) {
                orderSpecs.add(new OrderSpec(field, precedence, desc));
            }
        }
        return orderSpecs;
    }
    
    private static final class TokenInfoFormat implements InfoFormat {
        private final String infoFormat;
        private final DateFormat dateFormat;
    
        public TokenInfoFormat(
            final String infoFormat,
            final String dateFormat,
            final Locale locale) throws IOException {
            
            this.infoFormat = infoFormat;
            try {
                this.dateFormat = new SimpleDateFormat(dateFormat, locale);
            } catch (final IllegalArgumentException ex) {
                throw new IOException(ex);
            }
        }
        
        public boolean isHashUnused() {
            return !infoFormat.contains(HASH_TOKEN);
        }
        
        @Override
        public String toString(final Info info) {
            final String modified
                = dateFormat.format(new Date(info.getModified()));
            return infoFormat
                .replace(PATH_TOKEN, info.getPath())
                .replace(FILENAME_TOKEN, info.getFilename())
                .replace(SIZE_TOKEN, String.valueOf(info.getSize()))
                .replace(MODIFIED_TOKEN, modified)
                .replace(HASH_TOKEN, info.getHash());
        }
    }
    
    private static final class RegexFileFilter implements FileFilter {
        private final Pattern pattern;
        
        public RegexFileFilter(final String regex, final int flags)
            throws IOException {
            
            try {
                this.pattern = Pattern.compile(regex, flags);
            } catch (final PatternSyntaxException ex) {
                throw new IOException(ex);
            }
        }
        
        @Override
        public boolean accept(final File file) {
            return pattern.matcher(file.getName()).matches();
        }
    }
    
    private static final class OrderSpec {
        private final InfoField field;
        private final int precedence;
        private final boolean desc;
        
        public OrderSpec(
            final InfoField field, final int precedence, final boolean desc) {
            
            this.field = field;
            this.precedence = precedence;
            this.desc = desc;
        }
        
        public InfoField getField() {
            return field;
        }
        
        public int getPrecedence() {
            return precedence;
        }
        
        public boolean isDesc() {
            return desc;
        }
    }
    
    private static final class OrderSpecInfoSorter implements Comparator<Info> {
        private final List<OrderSpec> orderSpecs;
        
        public OrderSpecInfoSorter(
            final Collection<? extends OrderSpec> orderSpecs) {
            
            this.orderSpecs = orderSpecs.stream()
                    .sorted(OrderSpecComparator.INSTANCE)
                    .collect(Collectors.toList());
        }
        
        @Override
        public int compare(final Info info1, final Info info2) {
            int order = 0;
            for (final OrderSpec orderSpec : orderSpecs) {
                switch (orderSpec.getField()) {
                case PATH:
                    order = info1.getPath().compareTo(info2.getPath());
                    break;
                case FILENAME:
                    order = info1.getFilename().compareTo(info2.getFilename());
                    break;
                case SIZE:
                    order = Long.valueOf(info1.getSize()).compareTo(
                        Long.valueOf(info2.getSize()));
                    break;
                case MODIFIED:
                    order = Long.valueOf(info1.getModified()).compareTo(
                        Long.valueOf(info2.getModified()));
                    break;
                case HASH:
                default:
                    order = info1.getHash().compareTo(info2.getHash());
                }
                if (order != 0) {
                    if (orderSpec.isDesc()) {
                        order = -order;
                    }
                    break;
                }
            }
            return order;
        }
    }
    
    private static final class OrderSpecComparator
        implements Comparator<OrderSpec> {
        
        public static final Comparator<OrderSpec>
        INSTANCE = new OrderSpecComparator();
    
        private OrderSpecComparator() {
        }
        
        @Override
        public int compare(final OrderSpec os1, final OrderSpec os2) {
            return Integer.valueOf(os1.getPrecedence())
                .compareTo(Integer.valueOf(os2.getPrecedence()));
        }
    }
}
