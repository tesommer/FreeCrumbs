package freecrumbs.finf.internal;

import java.util.Comparator;
import java.util.stream.Stream;

import freecrumbs.finf.Info;

/**
 * Orders file info units based on a collection of order specs.
 * 
 * @author Tone Sommerland
 */
public class OrderSpecInfoSorter implements Comparator<Info> {
    private final OrderSpec[] orderSpecs;
    
    public OrderSpecInfoSorter(final OrderSpec... orderSpecs) {
        this.orderSpecs = Stream.of(orderSpecs)
                .sorted(OrderSpec.COMPARATOR)
                .toArray(OrderSpec[]::new);
    }
    
    @Override
    public int compare(final Info info1, final Info info2) {
        int order = 0;
        for (final OrderSpec orderSpec : orderSpecs) {
            switch (orderSpec.getField()) {
            case PATH:
                order = info1.getPath().compareTo(info2.getPath());
                break;
            case FILENAME:
                order = info1.getFilename().compareTo(info2.getFilename());
                break;
            case SIZE:
                order = Long.valueOf(info1.getSize()).compareTo(
                    Long.valueOf(info2.getSize()));
                break;
            case MODIFIED:
                order = Long.valueOf(info1.getModified()).compareTo(
                    Long.valueOf(info2.getModified()));
                break;
            case HASH:
            default:
                order = info1.getHash().compareTo(info2.getHash());
            }
            if (order != 0) {
                if (orderSpec.isDesc()) {
                    order = -order;
                }
                break;
            }
        }
        return order;
    }
}
