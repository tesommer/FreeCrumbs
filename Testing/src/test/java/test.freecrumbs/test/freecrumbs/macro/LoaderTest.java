package test.freecrumbs.macro;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.Loader;
import freecrumbs.macro.Macro;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

@DisplayName("Loader")
public final class LoaderTest
{
    private static final String ONE = "one";
    private static final String TWO = "two";

    public LoaderTest()
    {
    }
    
    @DisplayName("Loader.getDefault(GestureParser[])")
    public static final class GetDefaultGestureParserArrayTest
    {
        public GetDefaultGestureParserArrayTest()
        {
        }
        
        @Test
        @DisplayName("Empty script")
        public void test1() throws MacroException
        {
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, "");
            play(1, macros);
            sideEffects.assertState();
        }
        
        @Test
        @DisplayName("Script with only whitespace")
        public void test2() throws MacroException
        {
            final String scriptContent = "\t \n \r\n   \t \r";
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, scriptContent);
            play(1, macros);
            sideEffects.assertState();
        }
        
        @Test
        @DisplayName("One nameless macro")
        public void test3() throws MacroException
        {
            final String scriptContent = "two";
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, scriptContent);
            play(1, macros);
            sideEffects.assertState(2);
            play(1, "", macros);
            sideEffects.assertState(2, 2);
        }
        
        @Test
        @DisplayName("Two named macros")
        public void test4() throws MacroException
        {
            final String scriptContent
                = "name Abc\none\ntwo\n\nname Xyz\ntwo\none\n";
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, scriptContent);
            play(1, macros);
            sideEffects.assertState(1, 2);
            play(2, "Xyz", macros);
            sideEffects.assertState(1, 2, 2, 1, 2, 1);
        }
        
        @Test
        @DisplayName("Commented script")
        public void test5() throws MacroException
        {
            final String scriptContent
                = "#Comment1\nname JJ\none\n# Another comment\ntwo\n\n"
                + "#Yet another comment\nname Garcia\ntwo\n#one\n";
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, scriptContent);
            play(1, macros);
            sideEffects.assertState(1, 2);
            play(1, "Garcia", macros);
            sideEffects.assertState(1, 2, 2);
        }
        
        @Test
        @DisplayName("Only comment")
        public void test6() throws MacroException
        {
            final String scriptContent = "#one";
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, scriptContent);
            play(1, macros);
            sideEffects.assertState();
        }
        
        @Test
        @DisplayName("Invalid gesture")
        public void test7()
        {
            final String scriptContent = "name Hotch\nsiete";
            final SideEffects sideEffects = new SideEffects();
            assertThrows(
                    MacroException.class,
                    () -> load(sideEffects, scriptContent));
        }
        
        @Test
        @DisplayName("Multiple WS lines separating macros")
        public void test8() throws MacroException
        {
            final String scriptContent = "name x\ntwo\n\t\n\r\nname y\none\n";
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, scriptContent);
            play(1, macros);
            sideEffects.assertState(2);
            play(1, "y", macros);
            sideEffects.assertState(2, 1);
        }
        
        @Test
        @DisplayName("Name with no macro")
        public void test9() throws MacroException
        {
            final String scriptContent = "name Spencer\n";
            final SideEffects sideEffects = new SideEffects();
            final Macro[] macros = load(sideEffects, scriptContent);
            play(1, macros);
            sideEffects.assertState();
        }
        
        private static Loader getInstance(final SideEffects sideEffects)
        {
            return Loader.getDefault(new GestureOneTwoParser(sideEffects));
        }
        
        private static Macro[] load(
                final SideEffects sideEffects,
                final String scriptContent) throws MacroException
        {
            return getInstance(sideEffects).load(
                    new MockLocation(scriptContent).open());
        }
    }
    
    private static void play(
            final int times,
            final String macroName,
            final Macro... macros) throws MacroException
    {
        try
        {
            final Robot robot = new Robot();
            final Script script = Script.load(
                    MockLocation.DUMMY, new MockLoader(macros));
            if (macroName == null)
            {
                script.play(robot, times);
            }
            else
            {
                script.play(robot, times, macroName);
            }
        }
        catch (final AWTException ex)
        {
            throw new MacroException(ex);
        }
    }
    
    private static void play(final int times, final Macro... macros)
            throws MacroException
    {
        play(times, null, macros);
    }
    
    private static final class SideEffects
    {
        private final List<Integer> integers = new ArrayList<>();

        SideEffects()
        {
        }
        
        void add(final int integer)
        {
            integers.add(integer);
        }
        
        void assertState(final int... expecteds)
        {
            assertArrayEquals(
                    expecteds,
                    integers.stream().mapToInt(Integer::intValue).toArray(),
                    "Side effects");
        }
    }
    
    private static final class SideEffectsGesture implements Gesture
    {
        private final SideEffects sideEffects;
        private final Consumer<? super SideEffects> action;
        
        SideEffectsGesture(
                final SideEffects sideEffects,
                final Consumer<? super SideEffects> action)
        {
            assert sideEffects != null;
            assert action != null;
            this.sideEffects = sideEffects;
            this.action = action;
        }

        @Override
        public void play(final Script script, final Robot robot)
                throws MacroException
        {
            action.accept(sideEffects);
        }
    }
    
    private static final class GestureOneTwoParser implements GestureParser
    {
        private final SideEffects sideEffects;

        GestureOneTwoParser(final SideEffects sideEffects)
        {
            assert sideEffects != null;
            this.sideEffects = sideEffects;
        }

        @Override
        public boolean supports(final String line)
        {
            return ONE.equals(line) || TWO.equals(line);
        }

        @Override
        public Gesture parse(final String line) throws MacroException
        {
            if (ONE.equals(line))
            {
                return new SideEffectsGesture(sideEffects, se -> se.add(1));
            }
            else if (TWO.equals(line))
            {
                return new SideEffectsGesture(sideEffects, se -> se.add(2));
            }
            throw new MacroException(line);
        }
        
    }

}
