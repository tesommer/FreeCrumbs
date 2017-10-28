package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

/**
 * Specifies ordering by an info field.
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
    
    private final String fieldName;
    private final int precedence;
    private final boolean desc;
    
    /**
     * Creates an info field order-by specification.
     * @param fieldName the field to order by
     * @param precedence the priority of this order spec relative to others
     * @param desc descending or not
     */
    public OrderSpec(
        final String fieldName, final int precedence, final boolean desc) {
        
        this.fieldName = requireNonNull(fieldName, "fieldName");
        this.precedence = precedence;
        this.desc = desc;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public int getPrecedence() {
        return precedence;
    }
    
    public boolean isDesc() {
        return desc;
    }
}
