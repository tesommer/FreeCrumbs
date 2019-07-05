package test.freecrumbs.finf;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static test.freecrumbs.finf.MockInfoGenerator.getInfo;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import freecrumbs.finf.Config;
import freecrumbs.finf.ConfigLoader;
import freecrumbs.finf.FieldReader;
import freecrumbs.finf.Info;

@DisplayName("ConfigLoader")
public final class ConfigLoaderTest {

    public ConfigLoaderTest() {
    }
    
    @Test
    @DisplayName("getDefault(Map): Empty config")
    public void test1() throws IOException {
        final Config config = loadConfig("");
        assertConfig(config, false, false, -1);
        final Info info = getInfo("a", "b", "2", "3", "c");
        assertInfoFormat(config, info, "b${eol}");
        assertInfoGenerator(config, "filename", "eol");
    }
    
    @Test
    @DisplayName("getDefault(Map): Invalid property")
    public void test2() throws IOException {
        final Config config = loadConfig("count=22\nabc=xyz");
        assertConfig(config, false, false, 22);
    }
    
    @Test
    @DisplayName("getDefault(Map): Invalid date format")
    public void test3() {
        assertThrows(IOException.class, () -> loadConfig("date.format=j"));
    }
    
    @Test
    @DisplayName("getDefault(Map): Override")
    public void test4() throws IOException {
        final var overrides = Map.of("count", "21");
        final Config config = loadConfig("count=22", overrides);
        assertConfig(config, false, false, 21);
    }
    
    @Test
    @DisplayName("getDefault(Map): Override to default")
    public void test5() throws IOException {
        final var overrides = new HashMap<String, String>();
        overrides.put("count", null);
        final Config config = loadConfig("count=22", overrides);
        assertConfig(config, false, false, -1);
    }
    
    @Test
    @DisplayName("getDefault(Map): Used fields, prefilter off")
    public void test6() throws IOException {
        final Config config = loadConfig(
                  "prefilter=0\n"
                + "output=${filename}\n"
                + "filter=${path}++\n"
                + "filter.=${size}--\n"
                + "order=md5 desc");
        assertInfoGenerator(config, "path", "filename", "size", "md5");
    }
    
    @Test
    @DisplayName("getDefault(Map): Used fields, prefilter on")
    public void test7() throws IOException {
        final Config config = loadConfig(
                "prefilter=1\n"
              + "output=${filename}\n"
              + "filter=${path}--\n"
              + "filter.a=${size}++\n"
              + "order=md5 desc");
        assertInfoGenerator(config, "filename", "md5");
    }
    
    @Test
    @DisplayName("getDefault(Map): hash.algorithms")
    public void test8() throws IOException {
        final String setting
            = "hash.algorithms= md5  sha-512, \tnothanx md5 MD5 \n"
            + "output="
            + "${md5} ${sha-512,} ${nothanx} ${} ${ }  ${MD5}${\t}";
        final Config config = loadConfig(setting);
        assertInfoGenerator(config, "md5", "sha-512,", "nothanx");
        final String setting2
            = "hash.algorithms= \n"
            + "output=${md5} ${sha-512,} ${nothanx} ${} ${ } ${\t}";
        final Config config2 = loadConfig(setting2);
        assertInfoGenerator(config2);
    }
    
    @Nested
    @DisplayName("ConfigLoader.getDefault(Map): filter")
    public static final class GetDefaultTest_FileFilter {

        public GetDefaultTest_FileFilter() {
        }
        
        @Test
        @DisplayName("Regex")
        public void test1() throws IOException {
            final Config config = loadConfig("filter=a\\\\d*");
            assertFileFilter(config, "abc", false);
            assertFileFilter(config, "a23", true);
        }
        
        @Test
        @DisplayName("Regex: invalid regex")
        public void test2() {
            assertThrows(
                    IOException.class,
                    () -> loadConfig("filter=.{@,"));
        }
        
        @Test
        @DisplayName("Format pattern")
        public void test3() throws IOException {
            final String setting
                = "filter=${filename}"
                + "--^.{14,}$++(.*\\\\.html?$|.*\\\\.php$)--^index\\\\..{3,4}$";
            final Config config = loadConfig(setting);
            assertFileFilter(config, "ununpentium.txt", false);
            assertFileFilter(config, "moscovium.txt",   false);
            assertFileFilter(config, "index.html",      false);
            assertFileFilter(config, "index.php",       false);
            assertFileFilter(config, "download.html",   true);
            assertFileFilter(config, "download.php",    true);
            assertFileFilter(config, "download.htm",    true);
            assertFileFilter(config, "index.htm",       false);
        }
        
        @Test
        @DisplayName("Format pattern: empty format")
        public void test4() throws IOException {
            final Config config1 = loadConfig("filter=++.*");
            final Config config2 = loadConfig("filter=++.+");
            final String filename1 = "element115.txt";
            final String filename2 = "";
            assertFileFilter(config1, filename1, true);
            assertFileFilter(config1, filename2, true);
            assertFileFilter(config2, filename1, false);
            assertFileFilter(config2, filename2, false);
        }
        
        @Test
        @DisplayName("Format pattern: pattern with trailing delim char")
        public void test5() throws IOException {
            final Config config = loadConfig(
                    "filter=${filename}++\\\\d+++\\\\w+");
            assertFileFilter(config, "123", true);
            assertFileFilter(config, "abc", false);
        }
        
        @Test
        @DisplayName("Format pattern: trailing empty pattern")
        public void test6() throws IOException {
            final Config config = loadConfig("filter=${filename}++.?++");
            assertFileFilter(config, "", true);
            assertFileFilter(config, "a", false);
        }
        
        @Test
        @DisplayName("Format pattern: invalid pattern")
        public void test7() {
            assertThrows(
                    IOException.class,
                    () -> loadConfig("filter=${filename}++.{@,"));
        }
        
        @Test
        @DisplayName("Multiple filters")
        public void test8() throws IOException {
            final String setting
                = "filter=.{2,}\n"
                + "filter.malt=${filename}--.{7,}\n"
                + "filter.=${filename}++\\\\d+\n";
            final Config config = loadConfig(setting);
            assertFileFilter(config, "2",        false);
            assertFileFilter(config, "12345678", false);
            assertFileFilter(config, "xyz",      false);
            assertFileFilter(config, "123",      true);
        }
    }
    
    @Nested
    @DisplayName("ConfigLoader.getDefault(Map): order")
    public static final class GetDefaultTest_Order {
        
        private static final Info
        I1 = getInfo("p1", "f1", "1", "100", "h1");
        
        private static final Info
        I2 = getInfo("p1", "f1", "2", "102", "h2");
        
        private static final Info
        I3 = getInfo("p1", "f2", "3", "102", "h3");

        public GetDefaultTest_Order() {
        }
        
        @Test
        @DisplayName("Order")
        public void test1() throws IOException {
            final Config config = loadConfig(
                    "order=path filename desc size asc");
            assertOrder(config, I3, I1, I2);
            final Config config2 = loadConfig(
                    "order=modified md5 desc");
            assertOrder(config2, I1, I3, I2);
        }
        
        @Test
        @DisplayName("Order: invalid order")
        public void test2() throws IOException {
            assertOrder(loadConfig("order=wtf md5"), I1, I2, I3);
        }
    }
    
    @Nested
    @DisplayName("ConfigLoader.getDefault(Map): output")
    public static final class GetDefaultTest_InfoFormat {

        public GetDefaultTest_InfoFormat() {
        }
        
        @Test
        @DisplayName("Info format")
        public void test1() throws IOException {
            final Config config = loadConfig(
                    "output=${modified}|${filename}: ${path} -- ${size}");
            final Info info = getInfo("cat", "al", "ey", "a", "Z");
            assertInfoFormat(config, info, "a|al: cat -- ey");
        }
    }
    
    @Nested
    @DisplayName("ConfigLoader.getDefault(Map): count")
    public static final class GetDefaultTest_Count {

        public GetDefaultTest_Count() {
        }
        
        @Test
        @DisplayName("Count")
        public void test1() throws IOException {
            assertEquals(2, loadConfig("count=2").getCount(), "Count");
        }
    }
    
    @Nested
    @DisplayName("ConfigLoader.getDefault(Map): search")
    public static final class GetDefaultTest_Search {

        public GetDefaultTest_Search() {
        }
        
        @Test
        @DisplayName("Search")
        public void test1() throws IOException {
            loadConfig("search=/abc/");
            loadConfig("search=/abc/o=1");
            loadConfig("search=/abc/o=1,g=2");
            loadConfig("search=/abc/o=1,g=2,c=UTF-8");
            loadConfig("search=/abc/g=2,");
            loadConfig("search=/abc/,");
            loadConfig("search=/abc/,,,,,,,");
            loadConfig("search=/abc/c=UTF-8,o=1");
            loadConfig("search=/abc/g=0");
            loadConfig("search=//");
            loadConfig("search=/abc/xyz=UTX-8");
            assertThrows(
                    IOException.class,
                    () -> loadConfig("search=abc/"),
                    "abc/");
            assertThrows(
                    IOException.class,
                    () -> loadConfig("search=/abc"),
                    "/abc");
            assertThrows(
                    IOException.class,
                    () -> loadConfig("search=/abc/a"),
                    "/abc/a");
            assertThrows(
                    IOException.class,
                    () -> loadConfig("search=/abc/g=-1"),
                    "/abc/g=-1");
            assertThrows(
                    IOException.class,
                    () -> loadConfig("search=/abc/o=1,g=1,c=abcdefghij"),
                    "/abc/o=1,g=1,c=abcdefghij");
            assertThrows(
                    IOException.class,
                    () -> loadConfig("search=/abc/,o=2"),
                    "/abc/,o=2");
        }
    }
    
    private static Config loadConfig(
            final String properties,
            final Map<String, String> overrides) throws IOException {
        
        return ConfigLoader.getDefault(overrides)
                .loadConfig(new StringReader(properties));
    }
    
    private static Config loadConfig(final String properties)
            throws IOException {
        
        return loadConfig(properties, Map.of());
    }
    
    private static void assertConfig(
            final Config config,
            final boolean expectedFileFilterPresence,
            final boolean expectedOrderPresence,
            final int expectedCount) {
        
        assertEquals(
                expectedFileFilterPresence,
                config.getFileFilter().isPresent(),
                "File filter presence");
        assertEquals(
                expectedOrderPresence,
                config.getOrder().isPresent(),
                "Order presence");
        assertEquals(
                expectedCount,
                config.getCount(),
                "Count");
    }
    
    private static void assertInfoGenerator(
            final Config config,
            final String... expectedFieldNames) throws IOException {
        
        final var reader = (FieldReader)config.getInfoGenerator();
        final var expected = Stream.of(expectedFieldNames)
                .sorted()
                .collect(toList());
        final var actual = Stream.of(reader.getFieldNames())
                .sorted()
                .collect(toList());
        assertEquals(expected, actual, "Field names");
    }
    
    private static void assertInfoFormat(
            final Config config,
            final Info info,
            final String expectedFormattedInfo) throws IOException {
        
        final String actualFormattedInfo
            = config.getInfoFormat().toString(info);
        assertEquals(
                expectedFormattedInfo, actualFormattedInfo, "Formatted info");
    }
    
    private static void assertFileFilter(
            final Config config,
            final String filename,
            final boolean expectedIncludes) {

        assertEquals(
                expectedIncludes,
                config.getFileFilter().get().accept(new File(filename)),
                "File filter includes " + filename);
    }
    
    private static void assertOrder(
            final Config config, final Info... expected) {
        
        final var actual = new ArrayList<Info>(List.of(expected));
        Collections.reverse(actual);
        actual.sort(config.getOrder().get());
        for (int i = 0; i < expected.length; i++) {
            assertSame(expected[i], actual.get(i), "Order: index " + i);
        }
    }

}
