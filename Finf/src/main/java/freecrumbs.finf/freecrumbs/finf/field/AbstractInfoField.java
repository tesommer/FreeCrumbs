package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;

import freecrumbs.finf.InfoField;

public abstract class AbstractInfoField implements InfoField {
    private final String name;

    protected AbstractInfoField(final String name) {
        this.name = requireNonNull(name, "name");
    }

    @Override
    public final String getName() {
        return name;
    }

}
