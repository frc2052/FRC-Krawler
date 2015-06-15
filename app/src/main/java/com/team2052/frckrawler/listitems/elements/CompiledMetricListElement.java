package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.CompiledMetricValue;
import com.team2052.frckrawler.listitems.ListElement;

/**
 * @author Adam
 * @since 12/31/2014.
 */
public class CompiledMetricListElement extends ListElement {

    private CompiledMetricValue compiledMetricValue;

    public CompiledMetricListElement(CompiledMetricValue compiledMetricValue) {
        super(String.valueOf(compiledMetricValue.getRobot().getTeam_id()));
        this.compiledMetricValue = compiledMetricValue;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        View inflate = inflater.inflate(R.layout.list_item_metric_value, null);
        ((TextView) inflate.findViewById(R.id.title)).setText(Long.toString(compiledMetricValue.getRobot().getTeam_id()));
        ((TextView) inflate.findViewById(R.id.value)).setText(compiledMetricValue.getCompiledValue());
        return inflate;
    }
}
