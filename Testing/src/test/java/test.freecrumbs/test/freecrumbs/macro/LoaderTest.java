package test.freecrumbs.macro;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import freecrumbs.macro.Gesture;
import freecrumbs.macro.GestureParser;
import freecrumbs.macro.Loader;
import freecrumbs.macro.Macro;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.Script;

public final class LoaderTest
{
    private static final String ADD_ONE_GESTURE_NAME = "one";
    private static final String ADD_TWO_GESTURE_NAME = "two";

    public LoaderTest()
    {
    }
    
    @Nested
    public static final class
    DEFAULT_LOADER_WITH_ADD_ONE_AND_TWO_GESTURES
    {
        @Test
        public void
        empty_script_causes_no_side_effects
        () throws MacroException
        {
            final var sideEffects = new SideEffects();
            final Macro[] macros = load("", sideEffects);
            playFirst(1, macros);
            sideEffects.assertState();
        }
        
        @Test
        public void
        script_with_only_white_space_causes_no_side_effects
        () throws MacroException
        {
            final String scriptContent = "\t \n \r\n   \t \r";
            final var sideEffects = new SideEffects();
            final Macro[] macros = load(scriptContent, sideEffects);
            playFirst(1, macros);
            sideEffects.assertState();
        }
        
        @Test
        public void
        script_with_only_comment_causes_no_side_effects
        () throws MacroException
        {
            final String scriptContent = "#one";
            final var sideEffects = new SideEffects();
            final Macro[] macros = load(scriptContent, sideEffects);
            playFirst(1, macros);
            sideEffects.assertState();
        }
        
        @Test
        public void
        macro_without_gestures_causes_no_side_effects
        () throws MacroException
        {
            final String scriptContent = "name Spencer\n";
            final var sideEffects = new SideEffects();
            final Macro[] macros = load(scriptContent, sideEffects);
            playFirst(1, macros);
            sideEffects.assertState();
        }
        
        @Test
        public void
        a_script_can_contain_nameless_macros
        () throws MacroException
        {
            final String scriptContent = "two\n\none";
            final var sideEffects = new SideEffects();
            final Macro[] macros = load(scriptContent, sideEffects);
            playFirst(1, macros);
            sideEffects.assertState(2);
            play("", 1, macros);
            sideEffects.assertState(2, 2);
        }
        
        @Test
        public void
        a_script_can_contain_named_macros
        () throws MacroException
        {
            final String scriptContent
                = "name Abc\none\ntwo\n\nname Xyz\ntwo\none\n";
            final var sideEffects = new SideEffects();
            final Macro[] macros = load(scriptContent, sideEffects);
            playFirst(1, macros);
            sideEffects.assertState(1, 2);
            play("Xyz", 2, macros);
            sideEffects.assertState(1, 2, 2, 1, 2, 1);
        }
        
        @Test
        public void
        comments_are_lines_starting_with_pound_sign_and_are_ignored
        () throws MacroException
        {
            final String scriptContent
                = "#Comment1\nname JJ\none\n# Another comment\ntwo\n\n"
                + "#Yet another comment\nname Garcia\ntwo\n#one\n";
            final var sideEffects = new SideEffects();
            final Macro[] macros = load(scriptContent, sideEffects);
            playFirst(1, macros);
            sideEffects.assertState(1, 2);
            play("Garcia", 1, macros);
            sideEffects.assertState(1, 2, 2);
        }
        
        @Test
        public void
        invalid_gesture_causes_exception
        ()
        {
            final String scriptContent = "name Hotch\nsiete";
            final var sideEffects = new SideEffects();
            assertThrows(
                    MacroException.class,
                    () -> load(scriptContent, sideEffects));
        }
        
        @Test
        public void
        macros_can_be_separated_by_multiple_lines_containing_only_white_space
        () throws MacroException
        {
            final String scriptContent = "name x\ntwo\n\t\n\r\nname y\none\n";
            final var sideEffects = new SideEffects();
            final Macro[] macros = load(scriptContent, sideEffects);
            playFirst(1, macros);
            sideEffects.assertState(2);
            play("y", 1, macros);
            sideEffects.assertState(2, 1);
        }
        
        private static Loader underTest(final SideEffects sideEffects)
        {
            return Loader.supporting(new AddOneOrTwoGestureParser(sideEffects));
        }
        
        private static Macro[] load(
                final String scriptContent,
                final SideEffects sideEffects) throws MacroException
        {
            return underTest(sideEffects).load(
                    new MockLocation(scriptContent).open());
        }
    }
    
    private static void play(
            final String macroName,
            final int times,
            final Macro... macros) throws MacroException
    {
        try
        {
            final var robot = new Robot();
            final Script script = Script.load(
                    MockLocation.DUMMY, new MockLoader(macros));
            if (macroName == null)
            {
                script.playFirst(robot, times);
            }
            else
            {
                script.play(macroName, robot, times);
            }
        }
        catch (final AWTException ex)
        {
            throw new MacroException(ex);
        }
    }
    
    private static void playFirst(final int times, final Macro... macros)
            throws MacroException
    {
        play(null, times, macros);
    }
    
    private static final class SideEffects
    {
        private final List<Integer> integers = new ArrayList<>();

        private SideEffects()
        {
        }
        
        private void add(final int integer)
        {
            integers.add(integer);
        }
        
        private void assertState(final int... expecteds)
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
        
        private SideEffectsGesture(
                final SideEffects sideEffects,
                final Consumer<? super SideEffects> action)
        {
            assert sideEffects != null;
            assert action      != null;
            this.sideEffects = sideEffects;
            this.action      = action;
        }

        @Override
        public void play(final Script script, final Robot robot)
                throws MacroException
        {
            action.accept(sideEffects);
        }
    }
    
    private static final class AddOneOrTwoGestureParser implements GestureParser
    {
        private final SideEffects sideEffects;

        private AddOneOrTwoGestureParser(final SideEffects sideEffects)
        {
            assert sideEffects != null;
            this.sideEffects = sideEffects;
        }

        @Override
        public boolean supports(final String line)
        {
            return
                       ADD_ONE_GESTURE_NAME.equals(line)
                    || ADD_TWO_GESTURE_NAME.equals(line);
        }

        @Override
        public Gesture parse(final String line) throws MacroException
        {
            if (ADD_ONE_GESTURE_NAME.equals(line))
            {
                return new SideEffectsGesture(sideEffects, se -> se.add(1));
            }
            else if (ADD_TWO_GESTURE_NAME.equals(line))
            {
                return new SideEffectsGesture(sideEffects, se -> se.add(2));
            }
            throw new MacroException(line);
        }
        
    }

}
