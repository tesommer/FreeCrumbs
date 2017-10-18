package freecrumbs.finf.internal;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import freecrumbs.finf.MockInfo;

public class TokenInfoFormatTest {

    public TokenInfoFormatTest() {
    }
    
    @Test
    public void testInfoFormat() throws IOException {
        final MockInfo info = MockInfo.getInfo("cat", "al", "ey", "a", "Z");
        final String format = "${modified}|${filename}: ${path} -- ${size}";
        final TokenInfoFormat infoFormat = new TokenInfoFormat(format);
        final String expected = "a|al: cat -- ey";
        Assert.assertEquals(
                "Assert info format",
                expected,
                infoFormat.toString(info));
        Assert.assertTrue(
                "Assert path is read",
                info.isRead(MockInfo.PATH_FIELD_NAME));
        Assert.assertTrue(
                "Assert filename is read",
                info.isRead(MockInfo.FILENAME_FIELD_NAME));
        Assert.assertTrue(
                "Assert size is read",
                info.isRead(MockInfo.SIZE_FIELD_NAME));
        Assert.assertTrue(
                "Assert modified is read",
                info.isRead(MockInfo.MODIFIED_FIELD_NAME));
        Assert.assertFalse(
                "Assert hash is not read",
                info.isRead(MockInfo.HASH_FIELD_NAME));
    }

}
