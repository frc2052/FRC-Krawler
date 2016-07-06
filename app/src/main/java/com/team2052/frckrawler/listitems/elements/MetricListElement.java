package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.tba.JSON;

/**
 * @author Adam
 */
public class MetricListElement extends ListElement {
    private final Metric metric;
    private String descriptionString = "No Description";
    private String typeString = "";
    private String rangeString = "";

    public MetricListElement(Metric metric) {
        super(Long.toString(metric.getId()));
        this.metric = metric;
        JsonObject data = JSON.getAsJsonObject(metric.getData());

        if (!Strings.isNullOrEmpty(data.get("description").getAsString())) {
            descriptionString = data.get("description").getAsString();
        }

        switch (metric.getType()) {
            case MetricHelper.BOOLEAN:
                typeString = "Boolean";
                rangeString = "Not Applicable";
                break;
            case MetricHelper.COUNTER:
                typeString = "Counter";
                rangeString = data.get("min").getAsString() + " to " + data.get("max").getAsString() + " Incrementing by " + data.get("inc").getAsString();
                break;
            case MetricHelper.CHECK_BOX:
            case MetricHelper.CHOOSER:
                StringBuilder sb = new StringBuilder();
                String comma = "";
                for (JsonElement value : data.get("values").getAsJsonArray()) {
                    sb.append(comma).append(value.getAsString());
                    comma = ", ";
                }
                rangeString = sb.toString();
                typeString = "Chooser";
                break;
            case MetricHelper.SLIDER:
                rangeString = data.get("min").getAsString() + " to " + data.get("max").getAsString();
                typeString = "Slider";
                break;
        }


    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_metric, null);
        ((TextView) convertView.findViewById(R.id.metric_list_name)).setText(metric.getName());
        ((TextView) convertView.findViewById(R.id.metric_list_description)).setText(descriptionString);
        ((TextView) convertView.findViewById(R.id.metric_list_range)).setText(rangeString);
        ((TextView) convertView.findViewById(R.id.metric_list_type)).setText(typeString);
        return convertView;
    }
}
