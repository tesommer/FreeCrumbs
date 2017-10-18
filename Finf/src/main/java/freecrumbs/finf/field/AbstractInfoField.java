package freecrumbs.finf.field;

import static java.util.Objects.requireNonNull;
import freecrumbs.finf.InfoField;

public abstract class AbstractInfoField implements InfoField {
    private final String name;

    public AbstractInfoField(final String name) {
        this.name = requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }

}
