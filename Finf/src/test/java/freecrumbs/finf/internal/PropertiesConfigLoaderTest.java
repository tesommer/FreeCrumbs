package freecrumbs.finf.internal;

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

import freecrumbs.finf.Config;
import freecrumbs.finf.Info;

public class PropertiesConfigLoaderTest {
    
    private static final Locale LOCALE = Locale.getDefault();
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    private static final Info I1 = new Info("p1", "f1", 1, 100, "h1");
    private static final Info I2 = new Info("p1", "f1", 2, 102, "h2");
    private static final Info I3 = new Info("p1", "f2", 3, 102, "h3");

    public PropertiesConfigLoaderTest() {
    }
    
    @Test
    public void testEmptyConfig() throws IOException {
        final Config config = getConfig("");
        assertConfig(config, false, -1);
        assertInfoFormat(config, "f1", I1, DEFAULT_DATE_FORMAT);
    }
    
    @Test
    public void testOrder() throws IOException {
        final String prop = "order=path filename desc size asc";
        final Config config = getConfig(prop);
        assertConfig(config, false, -1, I3, I1, I2);
        final String prop2 = "order=modified hash desc";
        final Config config2 = getConfig(prop2);
        assertConfig(config2, false, -1, I1, I3, I2);
    }
    
    @Test
    public void testInvalidOrder() throws IOException {
        final Config config = getConfig("order=wtf hash");
        assertConfig(config, false, -1, I1, I2, I3);
    }
    
    @Test
    public void testUnusedHash() throws IOException {
        final Config config = getConfig("hash.algorithm=sha-256");
        final byte[] actual = config.getHashGenerator().digest(new File(""));
        Assert.assertArrayEquals("Assert empty hash", new byte[0], actual);
    }
    
    @Test
    public void testCount() throws IOException {
        final Config config = getConfig("count = 2");
        assertConfig(config, false, 2);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidCount() throws IOException {
        getConfig("count=THIS!");
    }
    
    @Test
    public void testRegexFileFilter() throws IOException {
        final String prop = "file.filter= a\\\\d*";
        final Config config = getConfig(prop);
        assertConfig(config, true, -1);
        assertFileFilter(config, new File("abc"), false);
        assertFileFilter(config, new File("a23"), true);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidRegexFileFilter() throws IOException {
        getConfig("file.filter=.{@,");
    }
    
    @Test
    public void testFormatPatternFileFilter() throws IOException {
        final String prop
            = "file.filter="
            + "${filename}"
            + "--^.{14,}$++(.*\\\\.html?$|.*\\\\.php$)--^index\\\\..{3,4}$";
        final Config config = getConfig(prop);
        assertConfig(config, true, -1);
        assertFileFilter(config, new File("ununpentium.txt"), false);
        assertFileFilter(config, new File("moscovium.txt"),   false);
        assertFileFilter(config, new File("index.html"),      false);
        assertFileFilter(config, new File("index.php"),       false);
        assertFileFilter(config, new File("download.html"),   true);
        assertFileFilter(config, new File("download.php"),    true);
        assertFileFilter(config, new File("download.htm"),    true);
        assertFileFilter(config, new File("index.htm"),       false);
    }
    
    @Test
    public void testFormatPatternFileFilter_EmptyFormat() throws IOException {
        final File file1 = new File("element115.txt");
        final File file2 = new File("");
        final Config config = getConfig("file.filter=++.*");
        final Config config2 = getConfig("file.filter=++.+");
        assertFileFilter(config, file1, true);
        assertFileFilter(config, file2, true);
        assertFileFilter(config2, file1, false);
        assertFileFilter(config2, file2, false);
    }
    
    @Test
    public void testFormatPatternFileFilter_PatternWithTrailingDelimChar()
            throws IOException {
        
        final String prop = "file.filter=${filename}++\\\\d+++\\\\w+";
        final Config config = getConfig(prop);
        assertFileFilter(config, new File("123"), true);
        assertFileFilter(config, new File("abc"), false);
    }
    
    @Test(expected = IOException.class)
    public void testInvalidFormatPatternFileFilter() throws IOException {
        getConfig("file.filter=${filename}++.{@,");
    }
    
    @Test
    public void testFormatPatternFileFilter_TrailingEmptyPattern()
            throws IOException {
        
        final String prop = "file.filter=${filename}++.?++";
        final Config config = getConfig(prop);
        assertFileFilter(config, new File(""), true);
        assertFileFilter(config, new File("a"), false);
    }
    
    @Test
    public void testInvalidProperty() throws IOException {
        final Config config = getConfig("count=22\nabc=xyz");
        assertConfig(config, false, 22);
    }
    
    @Test
    public void testInfoFormat() throws IOException {
        final String prop
            = "info.format= ${size}|${path}${hash}§${modified}$${filename}";
        final Config config = getConfig(prop);
        assertConfig(config, false, -1);
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
        overrides.put("count", "21");
        final Config config = getConfig("count=22", overrides);
        assertConfig(config, false, 21);
    }
    
    @Test
    public void testOverrideToDefault() throws IOException {
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("count", null);
        final Config config = getConfig("count=22", overrides);
        assertConfig(config, false, -1);
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
            final boolean fileFilterPresent,
            final int count,
            final Info... order) {
        
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
                    "Order: index " + i, expected[i], actualOrder.get(i));
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
    
    private static void assertFileFilter(
            final Config actual, final File file, final boolean includes) {
        
        if (includes) {
            Assert.assertTrue(
                    "Assert file filter includes " + file.getName(),
                    actual.getFileFilter().get().accept(file));
        } else {
            Assert.assertFalse(
                    "Assert file filter excludes " + file.getName(),
                    actual.getFileFilter().get().accept(file));
        }
    }

}
