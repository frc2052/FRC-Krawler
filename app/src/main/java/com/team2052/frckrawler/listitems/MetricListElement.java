package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Metric;

/**
 * @author Adam
 */
public class MetricListElement extends ListElement
{
    private final String descriptionString;
    private final Metric metric;
    private String typeString = "";
    private String rangeString = "";

    public MetricListElement(Metric metric)
    {
        super(Long.toString(metric.getId()));
        this.metric = metric;
        Object[] rangeArr = metric.range;
        if (metric.description.equals("")) {
            descriptionString = "No Description";
        } else {
            descriptionString = metric.description;
        }
        switch (metric.type) {
            case Metric.BOOLEAN:
                typeString = "Boolean";
                rangeString = "Not Applicable";
                break;
            case Metric.COUNTER:
                typeString = "Counter";
                rangeString = rangeArr[0] + " to " + rangeArr[1] + " Incrementing by " + rangeArr[2];
                break;
            case Metric.CHOOSER:
                boolean isFirst = true;
                for (Object o : rangeArr) {
                    if (!isFirst) {
                        isFirst = false;
                        rangeString += ", ";
                    }
                    isFirst = false;
                    rangeString += o;
                }
                typeString = "Chooser";
                break;
            case Metric.SLIDER:
                rangeString = rangeArr[0] + " to " + rangeArr[1];
                typeString = "Slider";
                break;
        }
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView)
    {
        convertView = inflater.inflate(R.layout.list_item_metric, null);
        ((TextView) convertView.findViewById(R.id.metric_list_name)).setText(metric.name);
        ((TextView) convertView.findViewById(R.id.metric_list_description)).setText(descriptionString);
        ((TextView) convertView.findViewById(R.id.metric_list_range)).setText(rangeString);
        ((TextView) convertView.findViewById(R.id.metric_list_type)).setText(typeString);
        return convertView;
    }
}
