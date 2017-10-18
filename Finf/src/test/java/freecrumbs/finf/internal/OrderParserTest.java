package freecrumbs.finf.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import freecrumbs.finf.Info;
import freecrumbs.finf.MockInfo;

public class OrderParserTest {
    
    private static final Info
    I1 = MockInfo.getInfo("p1", "f1", "1", "100", "h1");
    
    private static final Info
    I2 = MockInfo.getInfo("p1", "f1", "2", "102", "h2");
    
    private static final Info
    I3 = MockInfo.getInfo("p1", "f2", "3", "102", "h3");

    public OrderParserTest() {
    }
    
    @Test
    public void testOrderParser() throws IOException {
        final String setting = "path filename desc size asc";
        assertOrderParser(setting, I3, I1, I2);
        final String setting2 = "modified hash desc";
        assertOrderParser(setting2, I1, I3, I2);
    }
    
    @Test
    public void testInvalidOrder() throws IOException {
        final String setting = "wtf hash";
        assertOrderParser(setting, I1, I2, I3);
    }
    
    private static void assertOrderParser(
            final String setting, final Info... expectedOrder) {
        
        final OrderParser parser = new OrderParser(I1.getFieldNames());
        final Comparator<Info> sorter = parser.parse(setting);
        final List<Info> actualOrder
            = new ArrayList<>(Arrays.asList(expectedOrder));
        Collections.reverse(actualOrder);
        actualOrder.sort(sorter);
        for (int i = 0; i < expectedOrder.length; i++) {
            Assert.assertSame(
                    "Order: index " + i, expectedOrder[i], actualOrder.get(i));
        }
    }

}
