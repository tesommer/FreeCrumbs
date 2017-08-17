package freecrumbstesting;

import org.junit.Assert;

public final class TestUtil {

    private TestUtil() {
    }
    
    /**
     * Asserts that a block of code throws exception of the specified type.
     */
    public static void assertThrows(
            final String message,
            final Class<? extends Throwable> exceptionType,
            final ThrowingBlock block) {
        
        try {
            block.run();
            Assert.fail(
                    message + ": Expected exception of type " + exceptionType);
        } catch (final AssertionError ex) {
            throw ex;
        } catch (final Throwable ex) {
            Assert.assertEquals(
                    message + ": Type of thrown exception",
                    exceptionType,
                    ex.getClass());
        }
    }
    
    /**
     * Asserts that a block of code throws exception of the specified type.
     */
    public static void assertThrows(
            final Class<? extends Throwable> exceptionType,
            final ThrowingBlock block) {
        
        assertThrows("", exceptionType, block);
    }

}
