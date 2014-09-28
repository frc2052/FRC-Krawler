package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.*;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.SummaryMetricsActivity;
import com.team2052.frckrawler.database.models.Metric;

/**
 * @author Adam
 */
public class MetricListElement extends ListElement
{
    private final String descripstionString;
    private final String isDisplayed;
    private final Metric metric;
    private String typeString = "";
    private String rangeString = "";

    public MetricListElement(Metric metric)
    {
        super(Long.toString(metric.getId()));
        this.metric = metric;
        Object[] rangeArr = metric.range;
        isDisplayed = Boolean.toString(metric.display);
        if (metric.description.equals("")) {
            descripstionString = "No Description";
        } else {
            descripstionString = metric.description;
        }
        switch (metric.type) {
            case Metric.BOOLEAN:
                typeString = "Boolean";
                rangeString = "Not Applicable";
                break;
            case Metric.TEXT:
                typeString = "Text";
                rangeString = "Not Applicable";
                break;
            case Metric.COUNTER:
                typeString = "Counter";
                rangeString = rangeArr[0] + " to " + rangeArr[1] + " Incrementing by " + rangeArr[2];
                break;
            case Metric.CHOOSER:
                for (Object o : rangeArr) {
                    rangeString += ", ";
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
        ((TextView) convertView.findViewById(R.id.metric_list_description)).setText(descripstionString);
        ((TextView) convertView.findViewById(R.id.metric_list_displayed)).setText(isDisplayed);
        ((TextView) convertView.findViewById(R.id.metric_list_range)).setText(rangeString);
        ((TextView) convertView.findViewById(R.id.metric_list_type)).setText(typeString);
        convertView.findViewById(R.id.metric_list_edit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //c.startActivity(EditMetricDialogActivity.newInstance(c, metric));
            }
        });
        if (c instanceof SummaryMetricsActivity) {
            convertView.findViewById(R.id.metric_list_edit).setVisibility(View.GONE);
        }
        return convertView;
    }
}
