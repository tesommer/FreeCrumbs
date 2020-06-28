package freecrumbs.finf.config;

import java.util.Comparator;
import java.util.stream.Stream;

import freecrumbs.finf.Info;

/**
 * Orders info based on a collection of order specs.
 * 
 * @author Tone Sommerland
 */
public final class OrderSpecInfoSorter implements Comparator<Info>
{
    private final OrderSpec[] orderSpecs;
    
    public OrderSpecInfoSorter(final OrderSpec... orderSpecs)
    {
        this.orderSpecs = Stream.of(orderSpecs)
                .sorted(OrderSpec.COMPARATOR)
                .toArray(OrderSpec[]::new);
    }
    
    /**
     * Returns the field names used by this sorter.
     */
    public String[] usedFieldNames()
    {
        return Stream.of(orderSpecs)
                .map(OrderSpec::fieldName)
                .toArray(String[]::new);
    }
    
    @Override
    public int compare(final Info info1, final Info info2)
    {
        int order = 0;
        for (final OrderSpec orderSpec : orderSpecs)
        {
            order = info1.compare(orderSpec.fieldName(), info2).orElse(0);
            if (order != 0)
            {
                if (orderSpec.isDesc())
                {
                    order = -order;
                }
                break;
            }
        }
        return order;
    }
}
