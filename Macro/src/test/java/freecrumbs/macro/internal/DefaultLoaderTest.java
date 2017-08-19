package freecrumbs.macro.internal;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.Macro;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.MockLocation;
import freecrumbs.macro.Script;
import freecrumbs.macro.Util;
import freecrumbstesting.TestUtil;

public class DefaultLoaderTest {
    
    private static final String ONE = "one";
    private static final String TWO = "two";

    public DefaultLoaderTest() {
    }
    
    @Test
    public void testEmptyScript() throws MacroException {
        final String script = "";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros);
        play(macros);
        assertSideEffects(sideEffects);
    }
    
    @Test
    public void testScriptWithOnlyWhiteSpace() throws MacroException {
        final String script = "\t \n \r\n   \t \r";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros);
        play(macros);
        assertSideEffects(sideEffects);
    }
    
    @Test
    public void testOneNamelessMacro() throws MacroException {
        final String script = "two";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros, "");
        play(macros);
        assertSideEffects(sideEffects, 2);
    }
    
    @Test
    public void testTwoNamedMacros() throws MacroException {
        final String script = "name Abc\none\ntwo\n\nname Xyz\ntwo\none\n";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros, "Abc", "Xyz");
        play(macros);
        assertSideEffects(sideEffects, 1, 2, 2, 1);
    }
    
    @Test
    public void testCommentedScript() throws MacroException {
        final String script
            = "#Comment1\nname JJ\none\n# Another comment\ntwo\n\n"
            + "#Yet another comment\nname Garcia\ntwo\n#one\n";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros, "JJ", "Garcia");
        play(macros);
        assertSideEffects(sideEffects, 1, 2, 2);
    }
    
    @Test
    public void testOnlyComment() throws MacroException {
        final String script = "#one";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros);
        play(macros);
        assertSideEffects(sideEffects);
    }
    
    @Test
    public void testInvalidGesture() {
        final String script = "name Hotch\nsiete";
        final SideEffects sideEffects = new SideEffects();
        TestUtil.assertThrows(
                "Expected exception from load",
                MacroException.class, () -> load(sideEffects, script));
    }
    
    @Test
    public void testMultipleWSLinesSeparatingMacros() throws MacroException {
        final String script = "name x\ntwo\n\t\n\r\nname y\none\n";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros, "x", "y");
        play(macros);
        assertSideEffects(sideEffects, 2, 1);
    }
    
    @Test
    public void testNameWithNoMacro() throws MacroException {
        final String script = "name Spencer\n";
        final SideEffects sideEffects = new SideEffects();
        final Macro[] macros = load(sideEffects, script);
        assertMacroNames(macros);
        play(macros);
        assertSideEffects(sideEffects);
    }

    private static Macro[] load(
            final SideEffects sideEffects,
            final String script) throws MacroException {
        
        final GestureParser parser = new GestureOneTwoParser(sideEffects);
        final InputStream in = new MockLocation(script).open();
        return new DefaultLoader(parser).load(in);
    }
    
    private static void play(final Macro... macros) throws MacroException {
        try {
            final Robot robot = new Robot();
            final Script script = Util.createEmptyScript();
            for (final Macro macro : macros) {
                macro.play(script, robot);
            }
        } catch (final AWTException ex) {
            throw new MacroException(ex);
        }
    }
    
    private static void assertSideEffects(
            final SideEffects actual, final int... expected) {
        
        Assert.assertArrayEquals("Side effects", expected, actual.get());
    }
    
    private static void assertMacroNames(
            final Macro[] actualMacros, final String... expectedNames) {
        
        final String[] actualNames = Stream.of(actualMacros)
                .map(Macro::getName)
                .toArray(String[]::new);
        Assert.assertArrayEquals("Macro names", expectedNames, actualNames);
    }
    
    private static final class SideEffects {
        private final List<Integer> integers = new ArrayList<>();

        public SideEffects() {
        }
        
        public void add(final int integer) {
            integers.add(integer);
        }
        
        public int[] get() {
            return integers.stream().mapToInt(Integer::intValue).toArray();
        }
    }
    
    private static final class GestureOne implements Gesture {
        private final SideEffects sideEffects;

        public GestureOne(final SideEffects sideEffects) {
            this.sideEffects = sideEffects;
        }

        @Override
        public void play(final Script script, final Robot robot)
                throws MacroException {
            
            sideEffects.add(1);
        }
        
    }
    
    private static final class GestureTwo implements Gesture {
        private final SideEffects sideEffects;

        public GestureTwo(final SideEffects sideEffects) {
            this.sideEffects = sideEffects;
        }

        @Override
        public void play(final Script script, final Robot robot)
                throws MacroException {
            
            sideEffects.add(2);
        }
        
    }
    
    private static final class GestureOneTwoParser implements GestureParser {
        private final SideEffects sideEffects;

        public GestureOneTwoParser(final SideEffects sideEffects) {
            this.sideEffects = sideEffects;
        }

        @Override
        public boolean supports(final String line) {
            return ONE.equals(line) || TWO.equals(line);
        }

        @Override
        public Gesture parse(final String line) throws MacroException {
            if (ONE.equals(line)) {
                return new GestureOne(sideEffects);
            } else if (TWO.equals(line)) {
                return new GestureTwo(sideEffects);
            }
            throw new MacroException(line);
        }
        
    }

}
