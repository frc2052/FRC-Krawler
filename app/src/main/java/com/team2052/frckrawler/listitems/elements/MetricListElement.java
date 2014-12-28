package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.util.Utilities;

/**
 * @author Adam
 */
public class MetricListElement extends ListElement {
    private final String descriptionString;
    private final Metric metric;
    private String typeString = "";
    private String rangeString = "";

    public MetricListElement(Metric metric) {
        super(Long.toString(metric.getId()));
        this.metric = metric;
        String[] rangeArr = StringArrayDeserializer.deserialize(metric.getRange());
        if (metric.getDescription().equals("")) {
            descriptionString = "No Description";
        } else {
            descriptionString = metric.getDescription();
        }
        switch (metric.getType()) {
            case Utilities.MetricUtil.BOOLEAN:
                typeString = "Boolean";
                rangeString = "Not Applicable";
                break;
            case Utilities.MetricUtil.COUNTER:
                typeString = "Counter";
                rangeString = rangeArr[0] + " to " + rangeArr[1] + " Incrementing by " + rangeArr[2];
                break;
            case Utilities.MetricUtil.CHOOSER:
                boolean isFirst = true;
                for (Object o : rangeArr) {
                    if (!isFirst) {
                        rangeString += ", ";
                    }
                    isFirst = false;
                    rangeString += o;
                }
                typeString = "Chooser";
                break;
            case Utilities.MetricUtil.SLIDER:
                rangeString = rangeArr[0] + " to " + rangeArr[1];
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
