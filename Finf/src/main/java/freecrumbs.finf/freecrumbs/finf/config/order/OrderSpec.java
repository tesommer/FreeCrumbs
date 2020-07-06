package freecrumbs.finf.config.order;

import static java.util.Objects.requireNonNull;

/**
 * Specifies ordering by an info field.
 * 
 * @author Tone Sommerland
 */
public final class OrderSpec
{
    private final String fieldName;
    private final int precedence;
    private final boolean desc;
    
    /**
     * Creates an info-field order-specification.
     * @param fieldName the field to order by
     * @param precedence the priority of this order spec relative to others
     * @param desc descending or not
     */
    public OrderSpec(
        final String fieldName, final int precedence, final boolean desc)
    {
        this.fieldName = requireNonNull(fieldName, "fieldName");
        this.precedence = precedence;
        this.desc = desc;
    }
    
    public String fieldName()
    {
        return fieldName;
    }

    public int precedence()
    {
        return precedence;
    }
    
    public boolean isDesc()
    {
        return desc;
    }
}
