package freecrumbstesting;

@FunctionalInterface
public interface ThrowingBlock {
    public abstract void run() throws Exception;
}
