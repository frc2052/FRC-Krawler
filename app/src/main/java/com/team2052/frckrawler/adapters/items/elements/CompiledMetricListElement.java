package com.team2052.frckrawler.adapters.items.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.items.ListElement;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;
import com.team2052.frckrawler.models.Team;

/**
 * @author Adam
 * @since 12/31/2014.
 */
public class CompiledMetricListElement extends ListElement {

    public CompiledMetricValue compiledMetricValue;

    public CompiledMetricListElement(CompiledMetricValue compiledMetricValue) {
        super(String.valueOf(compiledMetricValue.getTeam().getNumber()));
        this.compiledMetricValue = compiledMetricValue;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        View inflate = inflater.inflate(R.layout.list_item_metric_value, null);
        Team team = compiledMetricValue.getTeam();
        ((TextView) inflate.findViewById(R.id.title)).setText(String.format("%s, %s", team.getName(), team.getNumber()));
        ((TextView) inflate.findViewById(R.id.value)).setText(compiledMetricValue.getReadableValue());
        return inflate;
    }
}
