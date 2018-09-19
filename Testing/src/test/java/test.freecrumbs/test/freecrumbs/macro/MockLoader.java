package test.freecrumbs.macro;

import java.io.InputStream;

import freecrumbs.macro.Loader;
import freecrumbs.macro.Macro;
import freecrumbs.macro.MacroException;
import freecrumbs.macro.RecursionGuard;

public final class MockLoader implements Loader {
    
    public static final Loader DUMMY = new MockLoader();
    
    private final Macro[] macros;

    public MockLoader(final Macro... macros) {
        this.macros = macros.clone();
    }

    @Override
    public Macro[] load(final InputStream in) throws MacroException {
        return macros.clone();
    }

    @Override
    public RecursionGuard getRecursionGuard() {
        return MockRecursionGuard.LIMIT_2;
    }

}
