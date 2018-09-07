package freecrumbs.finf.internal;

import java.util.ArrayList;
import java.util.Comparator;

import freecrumbs.finf.Info;

/**
 * Parses the order config-setting.
 * 
 * @author Tone Sommerland
 */
public final class OrderParser {
    private final String[] fieldNames;

    public OrderParser(final String... fieldNames) {
        this.fieldNames = fieldNames.clone();
    }
    
    public Comparator<Info> parse(final String setting) {
        return new OrderSpecInfoSorter(getOrderSpecs(setting));
    }
    
    private OrderSpec[] getOrderSpecs(final String setting) {
        final var orderSpecs = new ArrayList<OrderSpec>(fieldNames.length);
        for (final String fieldName : fieldNames) {
            boolean desc = false;
            int precedence = setting.indexOf(fieldName + " asc");
            if (precedence < 0) {
                precedence = setting.indexOf(fieldName + " desc");
                if (precedence > -1) {
                    desc = true;
                } else {
                    precedence = setting.indexOf(fieldName);
                }
            }
            if (precedence > -1) {
                orderSpecs.add(new OrderSpec(fieldName, precedence, desc));
            }
        }
        return orderSpecs.stream().toArray(OrderSpec[]::new);
    }

}
