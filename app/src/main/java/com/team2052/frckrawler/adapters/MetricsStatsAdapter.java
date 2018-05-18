package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.adapters.items.ListItem;
import com.team2052.frckrawler.comparators.SimpleValueCompiledComparator;
import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.core.common.v3.JSON;
import com.team2052.frckrawler.core.data.models.Metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adam on 6/11/2015.
 */
//TODO
public class MetricsStatsAdapter extends ListViewAdapter {

    private Metric metric;
    private List<ListItem> tempSortedItems;

    public MetricsStatsAdapter(Context context, Metric metric, List<ListItem> values) {
        super(context, values);
        this.metric = metric;
    }

    public void sortItems(String sort) {
        sort = sort.toLowerCase();
    }

    public AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        List<String> items = new ArrayList<>();
        items.add("Team Ascending");
        items.add("Team Descending");
        switch (metric.getType()) {
            case MetricHelper.BOOLEAN:
            case MetricHelper.COUNTER:
            case MetricHelper.SLIDER:
                items.add("Value Ascending");
                items.add("Value Descending");
                break;
            case MetricHelper.CHOOSER:
            case MetricHelper.CHECK_BOX:
                JsonObject data_json = JSON.getAsJsonObject(metric.getData());
                JsonArray values = data_json.get("values").getAsJsonArray();

                for (JsonElement value : values) {
                    items.add(String.format("%s %s", value.getAsString(), "Ascending"));
                    items.add(String.format("%s %s", value.getAsString(), "Descending"));
                }

                break;
        }

        builder.setSingleChoiceItems(items.toArray(new String[items.size()]), 0, (dialog, which) -> {
            tempSortedItems = values;
            if (which <= 2) {
            } else {
                switch (metric.getType()) {
                    case MetricHelper.COUNTER:
                    case MetricHelper.SLIDER:
                    case MetricHelper.BOOLEAN:
                        Collections.sort(tempSortedItems, new SimpleValueCompiledComparator(which % 2 == 0));
                        break;
                    case MetricHelper.CHOOSER:
                    case MetricHelper.CHECK_BOX:
                        int index = which % 2 == 0 ? which / 2 : (which + 1) / 2;
                        index -= 1;

                        break;

                }
            }
        });
        builder.setTitle("Sort");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            values = tempSortedItems;
            tempSortedItems = null;
            notifyDataSetChanged();
        });
        return builder.create();
    }
}
