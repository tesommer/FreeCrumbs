package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

/**
 * Specifies ordering of an info field.
 * 
 * @author Tone Sommerland
 */
public class OrderSpec {
    
    /**
     * Order by precedence.
     */
    public static final Comparator<OrderSpec>
    COMPARATOR = (os1, os2)
        -> Integer.valueOf(os1.getPrecedence())
            .compareTo(Integer.valueOf(os2.getPrecedence()));
    
    private final InfoField field;
    private final int precedence;
    private final boolean desc;
    
    /**
     * Creates an info field order-by specification.
     * @param field the field to order by
     * @param precedence the priority of this order spec relative to others
     * @param desc descending or not
     */
    public OrderSpec(
        final InfoField field, final int precedence, final boolean desc) {
        
        this.field = requireNonNull(field, "field");
        this.precedence = precedence;
        this.desc = desc;
    }
    
    public InfoField getField() {
        return field;
    }
    
    public int getPrecedence() {
        return precedence;
    }
    
    public boolean isDesc() {
        return desc;
    }
}
