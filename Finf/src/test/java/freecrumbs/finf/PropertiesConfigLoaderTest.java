package freecrumbs.finf;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesConfigLoaderTest {
    
    private static final Locale LOCALE = Locale.getDefault();
    private static final String MD5 = "md5";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    private static final Info I1 = new Info("p1", "f1", 1, 100, "h1");
    private static final Info I2 = new Info("p1", "f1", 2, 102, "h2");
    private static final Info I3 = new Info("p1", "f2", 3, 102, "h3");

    public PropertiesConfigLoaderTest() {
    }
    
    @Test
    public void testEmptyConfig() throws IOException {
        final Config config = getConfig("");
        assertConfig(config, MD5, false, -1);
        assertInfoFormat(config, "f1", I1, DEFAULT_DATE_FORMAT);
    }
    
    @Test
    public void testOrder() throws IOException {
        final String prop = "order=path filename desc size asc";
        final Config config = getConfig(prop);
        assertConfig(config, MD5, false, -1, I3, I1, I2);
        final String prop2 = "order=modified hash desc";
        final Config config2 = getConfig(prop2);
        assertConfig(config2, MD5, false, -1, I1, I3, I2);
    }
    
    @Test
    public void testInvalidOrder() throws IOException {
        final Config config = getConfig("order=wtf hash");
        assertConfig(config, MD5, false, -1, I1, I2, I3);
    }
    
    @Test
    public void testHashAlgorithm() throws IOException {
        final Config config = getConfig("hash.algorithm=sha-256");
        assertConfig(config, "sha-256", false, -1);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidHashAlgorithm() throws IOException {
        getConfig("hash.algorithm=Wee-d4U");
    }
    
    @Test
    public void testCount() throws IOException {
        final Config config = getConfig("count = 2");
        assertConfig(config, MD5, false, 2);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidCount() throws IOException {
        getConfig("count=THIS!");
    }
    
    @Test
    public void testFileFilter() throws IOException {
        final String prop = "file.filter= a\\\\d*";
        final Config config = getConfig(prop);
        assertConfig(config, MD5, true, -1);
        final File exclude = new File("abc");
        final File include = new File("a23");
        Assert.assertFalse(
                "Assert filter excludes file",
                config.getFileFilter().get().accept(exclude));
        Assert.assertTrue(
                "Assert filter includes file",
                config.getFileFilter().get().accept(include));
    }
    
    @Test(expected = IOException.class)
    public void testInvalidFileFilter() throws IOException {
        getConfig("file.filter=.{@,");
    }
    
    @Test
    public void testInvalidProperty() throws IOException {
        final Config config = getConfig("count=22\nabc=xyz");
        assertConfig(config, MD5, false, 22);
    }
    
    @Test
    public void testInfoFormat() throws IOException {
        final String prop
            = "info.format= ${size}|${path}${hash}§${modified}$${filename}";
        final Config config = getConfig(prop);
        assertConfig(config, MD5, false, -1);
        assertInfoFormat(
                config, "2|p1h2§${modified}$f1", I2, DEFAULT_DATE_FORMAT);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidDateFormat() throws IOException {
        getConfig("date.format=j");
    }
    
    @Test
    public void testOverride() throws IOException {
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("hash.algorithm", "SHa-512");
        overrides.put("count", "22");
        final Config config = getConfig("hash.algorithm=shA-256", overrides);
        assertConfig(config, "SHA-512", false, 22);
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
    
    /**
     * If order's length is zero,
     * it's asserted that the config's order is not present.
     */
    private static void assertConfig(
            final Config actual,
            final String hashAlgorithm,
            final boolean fileFilterPresent,
            final int count,
            final Info... order) {
        
        Assert.assertEquals(
                "Hash algorithm",
                hashAlgorithm.toLowerCase(LOCALE),
                actual.getMessageDigest().getAlgorithm().toLowerCase(LOCALE));
        if (fileFilterPresent) {
            Assert.assertTrue(
                    "Assert file filter present",
                    actual.getFileFilter().isPresent());
        } else {
            Assert.assertFalse(
                    "Assert file filter not present",
                    actual.getFileFilter().isPresent());
        }
        Assert.assertEquals("Count", count, actual.getCount());
        if (order.length == 0) {
            Assert.assertFalse(
                    "Assert order is not present",
                    actual.getOrder().isPresent());
        } else {
            Assert.assertTrue(
                    "Assert order is present",
                    actual.getOrder().isPresent());
            assertOrder(actual, order);
        }
    }
    
    private static void assertOrder(
            final Config actual, final Info... expected) {
        
        final List<Info> actualOrder = new ArrayList<>(Arrays.asList(expected));
        Collections.reverse(actualOrder);
        actualOrder.sort(actual.getOrder().get());
        for (int i = 0; i < expected.length; i++) {
            Assert.assertSame(
                    "Oder: index " + i, expected[i], actualOrder.get(i));
        }
    }
    
    /**
     * The expected formatted info should contain "${modified}".
     * It will be replaced with a formatted date.
     */
    private static void assertInfoFormat(
            final Config actual,
            final String expected,
            final Info info,
            final String dateFormat) {
        
        final String modified = new SimpleDateFormat(dateFormat)
            .format(new Date(info.getModified()));
        final String expectedWithModified
            = expected.replace("${modified}", modified);
        Assert.assertEquals(
                "Info format",
                expectedWithModified,
                actual.getInfoFormat().toString(info));
    }

}
