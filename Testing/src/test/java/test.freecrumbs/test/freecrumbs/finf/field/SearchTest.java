package test.freecrumbs.finf.field;

import static test.freecrumbs.finf.FieldTesting.assertFieldValues;
import static test.freecrumbs.finf.FieldTesting.resetComputations;
import static test.freecrumbs.finf.FieldTesting.updateComputations;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freecrumbs.finf.Field;
import freecrumbs.finf.field.DynamicValue;
import freecrumbs.finf.field.Search;

@DisplayName("Search")
public class SearchTest {

    public SearchTest() {
    }
    
    @Test
    @DisplayName("Single-char file, regex is same char")
    public void test1() throws IOException {
        final Field[] fields = search("a", "a");
        assertFieldValues(Map.of(
                "found",    "1",
                "groupcount", "0",
                "line",       "1",
                "input",      "a",
                "start",      "0",
                "end",        "1"),
                fields);
    }
    
    @Test
    @DisplayName("Single-char file, group count 1, regex not found")
    public void test2() throws IOException {
        final Field[] fields = search("a", "(b)");
        assertFieldValues(Map.of(
                "found",    "0",
                "groupcount", "1",
                "line",       "-1",
                "input",      "",
                "start",      "-1",
                "end",        "-1"),
                fields);
    }
    
    @Test
    @DisplayName("Empty file, empty regex")
    public void test3() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Empty file, non-empty regex")
    public void test4() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Single-line file, find 1st occurrence")
    public void test5() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Single-line file, find 2nd occurrence")
    public void test6() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Multi-line file, find 1st occurrence")
    public void test7() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Multi-line file, find 2nd occurrence")
    public void test8() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Single-line file, Find last occurrence")
    public void test9() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Multi-line file, Find last occurrence")
    public void test10() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Multi-line file, occurrence=0")
    public void test11() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Multi-line file, positive non-existing occurrence")
    public void test12() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Multi-line file, negative non-existing occurrence")
    public void test13() {
        Assertions.fail("Not implemented yet.");
    }
    
    @Test
    @DisplayName("Field-name prefix")
    public void test14() {
        Assertions.fail("Not implemented yet.");
    }
    
    private static Search.Params getParams(final String regex) {
        return new Search.Params(DynamicValue.of(regex));
    }
    
    private static Field[] search(
            final String input,
            final Search.Params params) throws IOException {
        
        final Field[] fields = Search.getFields(params);
        resetComputations(fields);
        updateComputations(input.getBytes("UTF-8"), 1024, fields);
        return fields;
    }
    
    private static Field[] search(
            final String input,
            final String regex) throws IOException {
        
        return search(input, getParams(regex));
    }

}
