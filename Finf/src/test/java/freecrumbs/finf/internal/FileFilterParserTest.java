package freecrumbs.finf.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import freecrumbs.finf.MockInfo;

public class FileFilterParserTest {
    
    private static final int REGEX_FLAGS = 0;

    public FileFilterParserTest() {
    }
    
    @Test
    public void testRegex() throws IOException {
        final String setting = "a\\d*";
        assertFileFilterParser(setting, false, "abc");
        assertFileFilterParser(setting, true, "a23");
    }
    
    @Test(expected = IOException.class)
    public void testInvalidRegex() throws IOException {
        assertFileFilterParser(".{@,", false, "");
    }
    
    @Test
    public void testFormatPattern() throws IOException {
        final String setting
            = "${filename}"
            + "--^.{14,}$++(.*\\.html?$|.*\\.php$)--^index\\..{3,4}$";
        assertFileFilterParser(setting, false, "ununpentium.txt");
        assertFileFilterParser(setting, false, "moscovium.txt");
        assertFileFilterParser(setting, false, "index.html");
        assertFileFilterParser(setting, false, "index.php");
        assertFileFilterParser(setting, true, "download.html");
        assertFileFilterParser(setting, true, "download.php");
        assertFileFilterParser(setting, true, "download.htm");
        assertFileFilterParser(setting, false, "index.htm");
    }
    
    @Test
    public void testFormatPattern_EmptyFormat() throws IOException {
        final String filename1 = "element115.txt";
        final String filename2 = "";
        final String setting = "++.*";
        final String setting2 = "++.+";
        assertFileFilterParser(setting, true, filename1);
        assertFileFilterParser(setting, true, filename2);
        assertFileFilterParser(setting2, false, filename1);
        assertFileFilterParser(setting2, false, filename2);
    }
    
    @Test
    public void testFormatPattern_PatternWithTrailingDelimChar()
            throws IOException {
        
        final String setting = "${filename}++\\d+++\\w+";
        assertFileFilterParser(setting, true, "123");
        assertFileFilterParser(setting, false, "abc");
    }
    
    @Test(expected = IOException.class)
    public void testInvalidFormatPattern() throws IOException {
        assertFileFilterParser("${filename}++.{@,", false, "");
    }
    
    @Test
    public void testFormatPattern_TrailingEmptyPattern() throws IOException {
        final String setting = "${filename}++.?++";
        assertFileFilterParser(setting, true, "");
        assertFileFilterParser(setting, false, "a");
    }
    
    private static void assertFileFilterParser(
            final String setting,
            final boolean includes,
            final String filename) throws IOException {
        
        final FileFilterParser parser = new FileFilterParser(
                REGEX_FLAGS,
                file -> MockInfo.getInfo("", filename, "", "", ""));
        final FileFilter filter = parser.parse(setting);
        final File file = new File(filename);
        if (includes) {
            Assert.assertTrue(
                    "Assert file filter includes " + filename,
                    filter.accept(file));
        } else {
            Assert.assertFalse(
                    "Assert file filter excludes " + filename,
                    filter.accept(file));
        }
    }

}
