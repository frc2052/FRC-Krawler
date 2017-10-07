package com.team2052.frckrawler.comparators;

import com.team2052.frckrawler.adapters.items.ListItem;
import com.team2052.frckrawler.adapters.items.elements.CompiledMetricListElement;

import java.util.Comparator;

public class SimpleValueCompiledComparator implements Comparator<ListItem> {
    private boolean desc;

    public SimpleValueCompiledComparator(boolean desc) {
        this.desc = desc;
    }

    public SimpleValueCompiledComparator() {
        this(false);
    }

    @Override
    public int compare(ListItem lhs, ListItem rhs) {
        double lhsVal = ((CompiledMetricListElement) lhs).compiledMetricValue.getJsonValue().get("value").getAsDouble();
        double rhsVal = ((CompiledMetricListElement) rhs).compiledMetricValue.getJsonValue().get("value").getAsDouble();
        return desc ? Double.compare(lhsVal, rhsVal) : Double.compare(rhsVal, lhsVal);
    }
}
