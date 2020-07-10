package freecrumbs.macro.internal;

import java.io.IOException;
import java.io.InputStream;

import freecrumbs.macro.Location;
import freecrumbs.macro.MacroException;

public enum StdinLocation implements Location
{
    INSTANCE;

    @Override
    public Location refer(final String target) throws MacroException
    {
        return new ScriptFile(target);
    }

    @Override
    public InputStream open() throws MacroException
    {
        return new CloseProtector(System.in);
    }
    
    private static final class CloseProtector extends InputStream
    {
        private final InputStream protectee;
        
        private CloseProtector(final InputStream protectee)
        {
            assert protectee != null;
            this.protectee = protectee;
        }

        @Override
        public void close() throws IOException
        {
        }

        @Override
        public int read() throws IOException
        {
            return protectee.read();
        }
        
    }

}
