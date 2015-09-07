package com.team2052.frckrawler.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.database.CompiledMetricValue;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.tba.JSON;

import java.util.ArrayList;
import java.util.List;

import static com.team2052.frckrawler.database.MetricHelper.MetricType;

/**
 * Created by Adam on 6/11/2015.
 */
//TODO
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
        switch (MetricType.values()[metric.getType()]) {
            case BOOLEAN:
                items.add("YES ASC");
                items.add("YES DESC");
                items.add("NO ASC");
                items.add("NO DESC");
                break;
            case COUNTER:
            case SLIDER:
                items.add("VALUE ASC");
                items.add("VALUE DESC");
                break;
            case CHOOSER:
            case CHECK_BOX:
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
