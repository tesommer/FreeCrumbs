package test.freecrumbs.macro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import freecrumbs.macro.Scanner;

public final class ScannerTest
{
    public ScannerTest()
    {
    }
    
    @Nested
    public static final class
    LIMITS_SPANNING_ENTIRE_IMAGE
    {
        @Test
        public void
        scanning_for_nth_occurrence_of_existing_subimage_returns_its_xy
        ()
        {
            final BufferedImage image
                = createImage(26, 26, 21, 22, 22, 23, 25, 25);
            final BufferedImage subImage = createImage(2, 1, 1, 0);
            final var scanner = new Scanner(image, 0, 0, 26, 26);
            assertXy(20, 22, scanner.xyOf(subImage, 1));
            assertXy(21, 23, scanner.xyOf(subImage, 2));
            assertXy(24, 25, scanner.xyOf(subImage, 3));
        }
        
        @Test
        public void
        scanning_for_nonexistent_subimage_returns_empty
        ()
        {
            final BufferedImage image
                = createImage(26, 26, 21, 22, 22, 23, 25, 25);
            final BufferedImage nonSubImage
                = createImage(2, 2, 0, 0, 1, 0, 0, 1, 1, 1);
            final var scanner = new Scanner(image, 0, 0, 26, 26);
            assertNotFound(scanner.xyOf(nonSubImage, 1));
            assertNotFound(scanner.xyOf(nonSubImage, 2));
        }
        
        @Test
        public void
        scanning_for_nonexistent_occurrence_of_existing_subimage_returns_empty
        ()
        {
            final BufferedImage image
                = createImage(26, 26, 21, 22, 22, 23, 25, 25);
            final BufferedImage subImage = createImage(2, 1, 1, 0);
            final var scanner = new Scanner(image, 0, 0, 26, 26);
            assertNotFound(scanner.xyOf(subImage, 4));
        }
    }
    
    @Test
    public void
    negative_occurrence_is_illegal_argument
    ()
    {
        final BufferedImage image = createImage(26, 26);
        final Scanner scanner = new Scanner(image, 0, 0, 26, 26);
        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.xyOf(createImage(2, 1), -1));
    }
    
    @Test
    public void
    negative_limits_are_automatically_restricted_to_image_bounds
    ()
    {
        final BufferedImage image
            = createImage(22, 22, 0, 0, 0, 21, 11, 11, 21, 21);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        assertXy(0,   0, new Scanner(image, -1,  0, 22, 22).xyOf(subImage, 1));
        assertXy(0,   0, new Scanner(image,  0, -1, 22, 22).xyOf(subImage, 1));
        assertXy(21, 21, new Scanner(image,  0,  0, -1, 22).xyOf(subImage, 4));
        assertXy(21, 21, new Scanner(image,  2,  0, 22, -1).xyOf(subImage, 2));
    }
    
    @Test
    public void
    in_bounds_x_limits_specifying_empty_region_results_in_not_found
    ()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |X|
        assertNotFound(new Scanner(
                image,
                11,
                0,
                11,
                23).xyOf(subImage, 1));
    }
    
    @Test
    public void
    in_bounds_x_limits_specifying_negative_region_results_in_not_found
    ()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |<>|
        assertNotFound(new Scanner(
                image,
                13,
                0,
                2,
                23).xyOf(subImage, 1));
    }
    
    @Test
    public void
    out_of_bounds_x_limits_are_automatically_restricted
    ()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |>|<
        assertXy(
                22,
                22,
                new Scanner(
                        image,
                        2,
                        0,
                        99,
                        23).xyOf(subImage, 3));
        // ||><
        assertNotFound(new Scanner(
                image,
                23,
                0,
                99,
                23).xyOf(subImage, 1));
        // |<|>
        assertNotFound(new Scanner(
                image,
                99,
                0,
                2,
                23).xyOf(subImage, 1));
        // ||<>
        assertNotFound(new Scanner(
                image,
                99,
                0,
                73,
                23).xyOf(subImage, 1));
    }
    
    @Test
    public void
    in_bounds_y_limits_specifying_empty_region_results_in_not_found
    ()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |X|
        assertNotFound(new Scanner(
                image,
                0,
                11,
                23,
                11).xyOf(subImage, 1));
    }
    
    @Test
    public void
    in_bounds_y_limits_specifying_negative_region_results_in_not_found
    ()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |<>|
        assertNotFound(new Scanner(
                image,
                0,
                13,
                23,
                2).xyOf(subImage, 1));
    }
    
    @Test
    public void
    out_of_bounds_y_limits_are_automatically_restricted
    ()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |>|<
        assertXy(
                22,
                22,
                new Scanner(
                        image,
                        0,
                        2,
                        23,
                        99).xyOf(subImage, 3));
        // ||><
        assertNotFound(new Scanner(
                image,
                0,
                23,
                23,
                99).xyOf(subImage, 1));
        // |<|>
        assertNotFound(new Scanner(
                image,
                0,
                99,
                23,
                2).xyOf(subImage, 1));
        // ||<>
        assertNotFound(new Scanner(
                image,
                0,
                99,
                23,
                73).xyOf(subImage, 1));
    }
    
    private static void assertXy(
            final int expectedX, final int expectedY, final int[] actualXy)
    {
        assertEquals(2, actualXy.length, "xy length");
        assertEquals(expectedX, actualXy[0], "x");
        assertEquals(expectedY, actualXy[1], "y");
    }
    
    private static void assertNotFound(final int[] actualXy)
    {
        assertEquals(0, actualXy.length, "xy not empty");
    }
    
    private static BufferedImage createImage(
            final int width, final int height, final int... xyDots)
    {
        assert xyDots.length % 2 == 0;
        final BufferedImage image
            = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics g = image.getGraphics();
        for (int i = 0; i < xyDots.length - 1; i += 2)
        {
            g.drawLine(xyDots[i], xyDots[i + 1], xyDots[i], xyDots[i + 1]);
        }
        g.dispose();
        return image;
    }

}
