package test.freecrumbs.finf;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        final MockInfo info = MockInfo.getInstance("a", "b", "2", "3", "c");
        assertInfoFormat(
                config,
                info,
                "b${eol}",
                fieldReadAssertion(MockInfo.PATH_FIELD_NAME,     true),
                fieldReadAssertion(MockInfo.FILENAME_FIELD_NAME, true),
                fieldReadAssertion(MockInfo.SIZE_FIELD_NAME,     true),
                fieldReadAssertion(MockInfo.MODIFIED_FIELD_NAME, true),
                fieldReadAssertion(MockInfo.MD5_FIELD_NAME,      true));
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
    @DisplayName("getDefault(Map): Used fields")
    public void test6() throws IOException {
        final Config config = loadConfig(
                "info.format=${filename}\n"
                + "file.filter=${path}++\n"
                + "order=md5 size desc");
        final String filename = "з|@#гд$%&/{([)]=}?+`┤";
        assertInfoGenerator(
                config,
                filename,
                MockInfo.PATH_FIELD_NAME,
                MockInfo.FILENAME_FIELD_NAME,
                MockInfo.SIZE_FIELD_NAME,
                MockInfo.MD5_FIELD_NAME);
    }
    
    @Test
    @DisplayName("getDefault(Map): Each Config has its own info cache")
    public void test7() throws IOException {
        final String filename = "thecommonfilename.txt";
        final Config config1 = loadConfig("info.format=${filename}");
        assertInfoGenerator(
                config1,
                filename,
                MockInfo.FILENAME_FIELD_NAME);
        final Config config2 = loadConfig("info.format=${path} ${filename}");
        assertInfoGenerator(
                config2,
                filename,
                MockInfo.FILENAME_FIELD_NAME,
                MockInfo.PATH_FIELD_NAME);
    }
    
    @Test
    @DisplayName("getDefault(Map): hash.algorithms")
    public void test8() throws IOException {
        final String setting
            = "hash.algorithms= md5  sha-512, \tnothanx md5 MD5 \n"
            + "info.format="
            + "${md5} ${sha-512,} ${nothanx} ${} ${ }  ${MD5}${\t}";
        final Config config = loadConfig(setting);
        assertInfoGenerator(config, "", "md5", "sha-512,", "nothanx");
        final String setting2
            = "hash.algorithms= \n"
            + "info.format=${md5} ${sha-512,} ${nothanx} ${} ${ } ${\t}";
        final Config config2 = loadConfig(setting2);
        assertInfoGenerator(config2, "");
    }
    
    @Nested
    @DisplayName("ConfigLoader.getDefault(Map): file.filter")
    public static final class GetDefaultTest_FileFilter {

        public GetDefaultTest_FileFilter() {
        }
        
        @Test
        @DisplayName("Regex")
        public void test1() throws IOException {
            final Config config = loadConfig("file.filter=a\\\\d*");
            assertFileFilter(config, "abc", false);
            assertFileFilter(config, "a23", true);
        }
        
        @Test
        @DisplayName("Regex: invalid regex")
        public void test2() {
            assertThrows(
                    IOException.class,
                    () -> loadConfig("file.filter=.{@,"));
        }
        
        @Test
        @DisplayName("Format pattern")
        public void test3() throws IOException {
            final String setting
                = "file.filter=${filename}"
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
            final Config config1 = loadConfig("file.filter=++.*");
            final Config config2 = loadConfig("file.filter=++.+");
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
                    "file.filter=${filename}++\\\\d+++\\\\w+");
            assertFileFilter(config, "123", true);
            assertFileFilter(config, "abc", false);
        }
        
        @Test
        @DisplayName("Format pattern: trailing empty pattern")
        public void test6() throws IOException {
            final Config config = loadConfig("file.filter=${filename}++.?++");
            assertFileFilter(config, "", true);
            assertFileFilter(config, "a", false);
        }
        
        @Test
        @DisplayName("Format pattern: invalid pattern")
        public void test7() {
            assertThrows(
                    IOException.class,
                    () -> loadConfig("file.filter=${filename}++.{@,"));
        }
    }
    
    @Nested
    @DisplayName("ConfigLoader.getDefault(Map): order")
    public static final class GetDefaultTest_Order {
        
        private static final Info
        I1 = MockInfo.getInstance("p1", "f1", "1", "100", "h1");
        
        private static final Info
        I2 = MockInfo.getInstance("p1", "f1", "2", "102", "h2");
        
        private static final Info
        I3 = MockInfo.getInstance("p1", "f2", "3", "102", "h3");

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
    @DisplayName("ConfigLoader.getDefault(Map): info.format")
    public static final class GetDefaultTest_InfoFormat {

        public GetDefaultTest_InfoFormat() {
        }
        
        @Test
        @DisplayName("Info format")
        public void test1() throws IOException {
            final Config config = loadConfig(
                    "info.format=${modified}|${filename}: ${path} -- ${size}");
            final MockInfo info = MockInfo.getInstance(
                    "cat", "al", "ey", "a", "Z");
            assertInfoFormat(
                    config,
                    info,
                    "a|al: cat -- ey",
                    fieldReadAssertion(MockInfo.PATH_FIELD_NAME,     true),
                    fieldReadAssertion(MockInfo.FILENAME_FIELD_NAME, true),
                    fieldReadAssertion(MockInfo.SIZE_FIELD_NAME,     true),
                    fieldReadAssertion(MockInfo.MODIFIED_FIELD_NAME, true),
                    fieldReadAssertion(MockInfo.MD5_FIELD_NAME,      true));
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
    
    private static FieldReadAssertion fieldReadAssertion(
            final String fieldName, final boolean expectedRead) {
        
        return new FieldReadAssertion(fieldName, expectedRead);
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
            final String filename,
            final String... expectedFieldNames) {
        
        final Info info = config.getInfoGenerator().apply(new File(filename));
        final var expected = Stream.of(expectedFieldNames)
                .sorted()
                .collect(toList());
        final var actual = Stream.of(info.getFieldNames())
                .sorted()
                .collect(toList());
        assertEquals(expected, actual, "Field names");
    }
    
    private static void assertInfoFormat(
            final Config config,
            final MockInfo info,
            final String expectedFormattedInfo,
            final FieldReadAssertion... fieldReadAssertions)
                    throws IOException {
        
        final String actualFormattedInfo
            = config.getInfoFormat().toString(info);
        assertEquals(
                expectedFormattedInfo, actualFormattedInfo, "Formatted info");
        Stream.of(fieldReadAssertions).forEach(
                assertion -> assertion.test(info));
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
    
    private static final class FieldReadAssertion {
        private final String fieldName;
        private final boolean expectedRead;
        
        private FieldReadAssertion(
                final String fieldName, final boolean expectedRead) {
            
            assert fieldName != null;
            this.fieldName = fieldName;
            this.expectedRead = expectedRead;
        }
        
        private void test(final MockInfo info) {
            assertEquals(
                    expectedRead,
                    info.isValueRead(fieldName),
                    "Is " + fieldName + " read");
        }
    }

}
