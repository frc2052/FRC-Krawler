package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.database.CompiledMetricValue;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 6/11/2015.
 */
public class MetricsStatsAdapter extends ListViewAdapter {

    private Metric metric;

    public MetricsStatsAdapter(Context context, Metric metric, List<CompiledMetricValue> values) {
        super(context, null);
        this.metric = metric;
    }

    public void sortItems(String sort) {
        sort = sort.toLowerCase();
    }

    public AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        List<String> items = new ArrayList<>();
        switch (metric.getType()) {
            case MetricUtil.BOOLEAN:
                items.add("YES ASC");
                items.add("YES DESC");
                items.add("NO ASC");
                items.add("NO DESC");
                break;
            case MetricUtil.COUNTER:
            case MetricUtil.SLIDER:
                items.add("VALUE ASC");
                items.add("VALUE DESC");
                break;
            case MetricUtil.CHOOSER:
            case MetricUtil.CHECK_BOX:
                JsonObject data_json = JSON.getAsJsonObject(metric.getData());
                JsonArray values = data_json.get("values").getAsJsonArray();
                for (JsonElement value : values) {
                    items.add(String.format("%s %s", value.getAsString(), "ASC"));
                    items.add(String.format("%s %s", value.getAsString(), "DESC"));
                }
                break;
        }

        builder.setSingleChoiceItems((String[]) items.toArray(), 0, (dialog, which) -> {

        });
        return builder.create();
    }
}
