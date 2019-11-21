package test.freecrumbs.finf.field;

import static test.freecrumbs.finf.FieldTesting.assertFieldValues;
import static test.freecrumbs.finf.FieldTesting.resetComputations;
import static test.freecrumbs.finf.FieldTesting.updateComputations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freecrumbs.finf.Field;
import freecrumbs.finf.field.DynamicValue;
import freecrumbs.finf.field.Search;

@DisplayName("Search")
public final class SearchTest
{
    public SearchTest()
    {
    }
    
    @Test
    @DisplayName("Single-char file, regex is same char")
    public void test1() throws IOException
    {
        final Field[] fields = search("a", "a");
        assertFieldValues(Map.of(
                "found",      "1",
                "groupcount", "0",
                "line",       "1",
                "input",      "a",
                "start",      "0",
                "end",        "1"),
                fields);
    }
    
    @Test
    @DisplayName("Single-char file, group count 1, not found")
    public void test2() throws IOException
    {
        final Field[] fields = search("a", "(b)");
        assertFieldValues(Map.of(
                "found",      "0",
                "groupcount", "1",
                "line",       "-1",
                "input",      "",
                "start",      "-1",
                "end",        "-1"),
                fields);
    }
    
    @Test
    @DisplayName("Empty file, empty regex")
    public void test3() throws IOException
    {
        final Field[] fields = search("", "");
        assertFieldValues(Map.of(
                "found",      "0",
                "groupcount", "0",
                "line",       "-1",
                "input",      "",
                "start",      "-1",
                "end",        "-1"),
                fields);
    }
    
    @Test
    @DisplayName("Empty file, non-empty regex")
    public void test4() throws IOException
    {
        final Field[] fields = search("", "c");
        assertFieldValues(Map.of(
                "found",      "0",
                "groupcount", "0",
                "line",       "-1",
                "input",      "",
                "start",      "-1",
                "end",        "-1"),
                fields);
    }
    
    @Test
    @DisplayName("Single-line file, find 1st occurrence")
    public void test5() throws IOException
    {
        final Field[] fields = search("abcdbe", "b");
        assertFieldValues(Map.of(
                "found",      "1",
                "groupcount", "0",
                "line",       "1",
                "input",      "b",
                "start",      "1",
                "end",        "2"),
                fields);
    }
    
    @Test
    @DisplayName("Single-line file, find 2nd occurrence")
    public void test6() throws IOException
    {
        final Field[] fields = search(
                "Abcdbe", getParams("b").withOccurrence(2));
        assertFieldValues(Map.of(
                "found",      "1",
                "groupcount", "0",
                "line",       "1",
                "input",      "b",
                "start",      "4",
                "end",        "5"),
                fields);
    }
    
    @Test
    @DisplayName("Multi-line file, group count 2, find 1st occurrence")
    public void test7() throws IOException
    {
        final Field[] fields = search("abcdbe\nxybc\n", "((b))");
        assertFieldValues(Map.of(
                "found",      "1",
                "groupcount", "2",
                "line",       "1",
                "input",      "b",
                "start",      "1",
                "end",        "2"),
                fields);
    }
    
    @Test
    @DisplayName("Multi-line file, find 2nd occurrence, line 2")
    public void test8() throws IOException
    {
        final Field[] fields = search(
                "abcdE\nxybc\n", getParams("b").withOccurrence(2));
        assertFieldValues(Map.of(
                "found",      "1",
                "groupcount", "0",
                "line",       "2",
                "input",      "b",
                "start",      "2",
                "end",        "3"),
                fields);
    }
    
    @Test
    @DisplayName("Single-line file, Find last occurrence")
    public void test9() throws IOException
    {
        final Field[] fields = search(
                "xyzyxzy", getParams("x").withOccurrence(-1));
        assertFieldValues(Map.of(
                "found",      "1",
                "groupcount", "0",
                "line",       "1",
                "input",      "x",
                "start",      "4",
                "end",        "5"),
                fields);
    }
    
    @Test
    @DisplayName("Multi-line file, Find last occurrence")
    public void test10() throws IOException
    {
        final Field[] fields = search(
                "xyxz\nabc\nyxzy\n", getParams("x").withOccurrence(-1));
        assertFieldValues(Map.of(
                "found",      "1",
                "groupcount", "0",
                "line",       "3",
                "input",      "x",
                "start",      "1",
                "end",        "2"),
                fields);
    }
    
    @Test
    @DisplayName("Multi-line file, occurrence=0")
    public void test11() throws IOException
    {
        final Field[] fields = search(
                "Xyxz\nabc\nyxzy\n", getParams("x").withOccurrence(0));
        assertFieldValues(Map.of(
                "found",      "0",
                "groupcount", "0",
                "line",       "-1",
                "input",      "",
                "start",      "-1",
                "end",        "-1"),
                fields);
    }
    
    @Test
    @DisplayName("Multi-line file, positive non-existing occurrence")
    public void test12() throws IOException
    {
        final Field[] fields = search(
                "xYxz\nabc\nyxzy\n", getParams("x").withOccurrence(42));
        assertFieldValues(Map.of(
                "found",      "0",
                "groupcount", "0",
                "line",       "-1",
                "input",      "",
                "start",      "-1",
                "end",        "-1"),
                fields);
    }
    
    @Test
    @DisplayName("Multi-line file, negative non-existing occurrence")
    public void test13() throws IOException
    {
        final Field[] fields = search(
                "xyxZ\nabc\nyxzy\n", getParams("x").withOccurrence(-42));
        assertFieldValues(Map.of(
                "found",      "0",
                "groupcount", "0",
                "line",       "-1",
                "input",      "",
                "start",      "-1",
                "end",        "-1"),
                fields);
    }
    
    @Test
    @DisplayName("Field-name prefix, groups 2, group count 1")
    public void test14() throws IOException
    {
        final Field[] fields = search(
                "2", getParams("(2)").withFieldNamePrefix("T").withGroups(2));
        final var expecteds = new HashMap<String, String>();
        expecteds.put("Tfound",      "1");
        expecteds.put("Tgroupcount", "1");
        expecteds.put("Tline",       "1");
        expecteds.put("Tinput",      "2");
        expecteds.put("Tstart",      "0");
        expecteds.put("Tend",        "1");
        expecteds.put("T1-input",    "2");
        expecteds.put("T1-start",    "0");
        expecteds.put("T1-end",      "1");
        expecteds.put("T2-input",    "");
        expecteds.put("T2-start",    "-1");
        expecteds.put("T2-end",      "-1");
        assertFieldValues(expecteds, fields);
    }
    
    private static Search.Params getParams(final String regex)
    {
        return new Search.Params(DynamicValue.of(regex));
    }
    
    private static Field[] search(
            final String input,
            final Search.Params params) throws IOException
    {
        final Field[] fields = Search.getFields(params);
        resetComputations(fields);
        updateComputations(input.getBytes("UTF-8"), 1024, fields);
        return fields;
    }
    
    private static Field[] search(
            final String input,
            final String regex) throws IOException
    {
        return search(input, getParams(regex));
    }

}
