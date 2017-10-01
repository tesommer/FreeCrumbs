package freecrumbs.finf.internal;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;

import freecrumbs.finf.Info;

/**
 * Parses the order config setting.
 * 
 * @author Tone Sommerland
 */
public class OrderParser {
    private final Locale locale;

    public OrderParser(final Locale locale) {
        this.locale = requireNonNull(locale, "locale");
    }
    
    public Comparator<Info> parse(final String setting) {
        return new OrderSpecInfoSorter(getOrderSpecs(setting, locale));
    }
    
    private static OrderSpec[] getOrderSpecs(
            final String setting, final Locale locale) {
        
        final String settingTLC = setting.toLowerCase(locale);
        final Collection<OrderSpec> orderSpecs
            = new ArrayList<>(InfoField.values().length);
        for (final InfoField field : InfoField.values()) {
            boolean desc = false;
            final String name = field.name().toLowerCase(locale);
            int precedence = settingTLC.indexOf(name + " asc");
            if (precedence < 0) {
                precedence = settingTLC.indexOf(name + " desc");
                if (precedence > -1) {
                    desc = true;
                } else {
                    precedence = settingTLC.indexOf(name);
                }
            }
            if (precedence > -1) {
                orderSpecs.add(new OrderSpec(field, precedence, desc));
            }
        }
        return orderSpecs.stream().toArray(OrderSpec[]::new);
    }

}
