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
    private final OrderSpecInfoSorter order;

    /**
     * Parses the order setting.
     * @param setting the order setting (nullable)
     * @param availableFieldNames names of all available fields
     */
    public OrderParser(
            final String setting, final String[] availableFieldNames) {
        
        if (setting == null) {
            this.order = null;
        } else {
            this.order = new OrderSpecInfoSorter(
                    getOrderSpecs(setting, availableFieldNames));
        }
    }
    
    /**
     * Returns the field names used by the order setting.
     */
    public String[] getUsedFieldNames() {
        return order == null ? new String[0] : order.getUsedFieldNames();
    }
    
    /**
     * Return the info sorter.
     * @return null if the setting is null
     */
    public Comparator<Info> getOrder() {
        return order;
    }
    
    private OrderSpec[] getOrderSpecs(
            final String setting, final String[] availableFieldNames) {
        
        final var orderSpecs = new ArrayList<OrderSpec>(
                availableFieldNames.length);
        for (final String fieldName : availableFieldNames) {
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
