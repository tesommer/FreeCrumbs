package freecrumbs.finf.internal;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import freecrumbs.finf.Config;
import freecrumbs.finf.Info;
import freecrumbs.finf.MockInfo;

public class PropertiesConfigLoaderTest {
    
    private static final Locale LOCALE = Locale.getDefault();
    
    private static final Info
    INFO = MockInfo.getInfo("p1", "f1", "2", "102", "h2");

    public PropertiesConfigLoaderTest() {
    }
    
    @Test
    public void testEmptyConfig() throws IOException {
        final Config config = getConfig("");
        assertConfig(config, false, false, -1);
        assertInfoFormat(config, "f1", INFO);
    }
    
    @Test
    public void testOrder() throws IOException {
        final String prop = "order=";
        final Config config = getConfig(prop);
        assertConfig(config, false, true, -1);
    }
    
    @Test
    public void testCount() throws IOException {
        final Config config = getConfig("count = 2");
        assertConfig(config, false, false, 2);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidCount() throws IOException {
        getConfig("count=THIS!");
    }
    
    @Test
    public void testInvalidProperty() throws IOException {
        final Config config = getConfig("count=22\nabc=xyz");
        assertConfig(config, false, false, 22);
    }
    
    @Test
    public void testInfoFormat() throws IOException {
        final String prop
            = "info.format= ${size}|${path}${hash}§${modified}$${filename}";
        final Config config = getConfig(prop);
        assertConfig(config, false, false, -1);
        assertInfoFormat(config, "2|p1h2§102$f1", INFO);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidDateFormat() throws IOException {
        getConfig("date.format=j");
    }
    
    @Test
    public void testOverride() throws IOException {
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("count", "21");
        final Config config = getConfig("count=22", overrides);
        assertConfig(config, false, false, 21);
    }
    
    @Test
    public void testOverrideToDefault() throws IOException {
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("count", null);
        final Config config = getConfig("count=22", overrides);
        assertConfig(config, false, false, -1);
    }
    
    private static Config getConfig(
            final String properties,
            final Map<String, String> overrides) throws IOException {
        
        return new PropertiesConfigLoader(LOCALE, overrides)
            .loadConfig(new StringReader(properties));
    }
    
    private static Config getConfig(final String properties)
            throws IOException {
        
        return getConfig(properties, new HashMap<>());
    }
    
    private static void assertConfig(
            final Config actual,
            final boolean fileFilterPresent,
            final boolean orderPresent,
            final int count) {
        
        if (fileFilterPresent) {
            Assert.assertTrue(
                    "Assert file filter present",
                    actual.getFileFilter().isPresent());
        } else {
            Assert.assertFalse(
                    "Assert file filter not present",
                    actual.getFileFilter().isPresent());
        }
        if (orderPresent) {
            Assert.assertTrue(
                    "Assert order present",
                    actual.getOrder().isPresent());
        } else {
            Assert.assertFalse(
                    "Assert order not present",
                    actual.getOrder().isPresent());
        }
        Assert.assertEquals("Count", count, actual.getCount());
    }
    
    private static void assertInfoFormat(
            final Config actual,
            final String expected,
            final Info info) throws IOException {
        
        Assert.assertEquals(
                "Info format",
                expected,
                actual.getInfoFormat().toString(info));
    }

}
