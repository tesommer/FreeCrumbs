package test.freecrumbs.macro;

import freecrumbs.macro.MacroException;
import freecrumbs.macro.RecursionGuard;

public final class MockRecursionGuard implements RecursionGuard {
    
    public static final RecursionGuard LIMIT_2 = new MockRecursionGuard(2);
    
    private final int limit;
    private int count;

    public MockRecursionGuard(final int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit < 0: " + limit);
        }
        this.limit = limit;
    }

    @Override
    public void increment() throws MacroException {
        if (++count >= limit) {
            throw new MacroException("Too many recursions: " + count);
        }
    }

    @Override
    public void decrement() {
        count--;
    }

}
