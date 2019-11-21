package test.freecrumbs.macro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freecrumbs.macro.Scanner;

@DisplayName("Scanner")
public final class ScannerTest
{
    public ScannerTest()
    {
    }
    
    @Test
    @DisplayName("Scan with limits spanning entire image")
    public void test1()
    {
        final BufferedImage image = createImage(26, 26, 21, 22, 22, 23, 25, 25);
        final BufferedImage subImage = createImage(2, 1, 1, 0);
        final BufferedImage nonSubImage
            = createImage(2, 2, 0, 0, 1, 0, 0, 1, 1, 1);
        final Scanner scanner = new Scanner(image, 0, 0, 26, 26);
        assertXY(20, 22, scanner.xyOf(subImage, 1));
        assertXY(21, 23, scanner.xyOf(subImage, 2));
        assertXY(24, 25, scanner.xyOf(subImage, 3));
        assertNotFound(scanner.xyOf(subImage, 4));
        assertNotFound(scanner.xyOf(nonSubImage, 1));
        assertNotFound(scanner.xyOf(nonSubImage, 2));
    }
    
    @Test
    @DisplayName("Negative occurrence")
    public void test2()
    {
        final BufferedImage image = createImage(26, 26);
        final Scanner scanner = new Scanner(image, 0, 0, 26, 26);
        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.xyOf(createImage(2, 1), -1));
    }
    
    @Test
    @DisplayName("Negative limits")
    public void test3()
    {
        final BufferedImage image
            = createImage(22, 22, 0, 0, 0, 21, 11, 11, 21, 21);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        assertXY(0, 0, new Scanner(image, -1, 0, 22, 22).xyOf(subImage, 1));
        assertXY(0, 0, new Scanner(image, 0, -1, 22, 22).xyOf(subImage, 1));
        assertXY(21, 21, new Scanner(image, 0, 0, -1, 22).xyOf(subImage, 4));
        assertXY(21, 21, new Scanner(image, 2, 0, 22, -1).xyOf(subImage, 2));
    }
    
    @Test
    @DisplayName("X limits out of bounds")
    public void test4()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |>|<
        assertXY(
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
        // |<>|
        assertNotFound(new Scanner(
                image,
                13,
                0,
                2,
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
        // |X|
        assertNotFound(new Scanner(
                image,
                11,
                0,
                11,
                23).xyOf(subImage, 1));
    }
    
    @Test
    @DisplayName("Y limits out of bounds")
    public void test5()
    {
        final BufferedImage image = createImage(23, 23, 7, 7, 11, 11, 22, 22);
        final BufferedImage subImage = createImage(1, 1, 0, 0);
        // |>|<
        assertXY(
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
        // |<>|
        assertNotFound(new Scanner(
                image,
                0,
                13,
                23,
                2).xyOf(subImage, 1));
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
        // |X|
        assertNotFound(new Scanner(
                image,
                0,
                11,
                23,
                11).xyOf(subImage, 1));
    }
    
    private static void assertXY(
            final int expectedX, final int expectedY, final int[] actualXY)
    {
        assertEquals(2, actualXY.length, "xy length");
        assertEquals(expectedX, actualXY[0], "x");
        assertEquals(expectedY, actualXY[1], "y");
    }
    
    private static void assertNotFound(final int[] actualXY)
    {
        assertEquals(0, actualXY.length, "xy not empty");
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
