package test.freecrumbs.finf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
                    "order=modified hash desc");
            assertOrder(config2, I1, I3, I2);
        }
        
        @Test
        @DisplayName("Order: invalid order")
        public void test2() throws IOException {
            assertOrder(loadConfig("order=wtf hash"), I1, I2, I3);
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
            assertEquals(
                    "a|al: cat -- ey",
                    config.getInfoFormat().toString(info),
                    "Formatted info");
            assertTrue(
                    info.isValueRead(MockInfo.PATH_FIELD_NAME),
                    "Path read");
            assertTrue(
                    info.isValueRead(MockInfo.FILENAME_FIELD_NAME),
                    "Filename read");
            assertTrue(
                    info.isValueRead(MockInfo.SIZE_FIELD_NAME),
                    "Size read");
            assertTrue(
                    info.isValueRead(MockInfo.MODIFIED_FIELD_NAME),
                    "Modified read");
            assertFalse(
                    info.isValueRead(MockInfo.HASH_FIELD_NAME),
                    "Hash read");
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
    
    private static void assertFileFilter(
            final Config config,
            final String filename,
            final boolean includes) {
        
        if (includes) {
            assertTrue(
                    config.getFileFilter().get().accept(new File(filename)),
                    "File filter includes " + filename);
        } else {
            assertFalse(
                    config.getFileFilter().get().accept(new File(filename)),
                    "File filter includes " + filename);
        }
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
