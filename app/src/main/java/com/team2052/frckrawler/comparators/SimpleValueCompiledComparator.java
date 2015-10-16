package com.team2052.frckrawler.comparators;

import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.CompiledMetricListElement;

import java.util.Comparator;

/**
 * Created by Adam on 10/16/2015.
 */
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
        double lhsVal = ((CompiledMetricListElement) lhs).compiledMetricValue.getCompiledValueJson().get("value").getAsDouble();
        double rhsVal = ((CompiledMetricListElement) rhs).compiledMetricValue.getCompiledValueJson().get("value").getAsDouble();
        return desc ? Double.compare(lhsVal, rhsVal) : Double.compare(rhsVal, lhsVal);
    }
}
