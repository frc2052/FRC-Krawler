package com.team2052.frckrawler.adapters.items.smart.metric;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class BooleanMetricValueSummaryCard extends BindableFrameLayout<CompiledMetricValue> {
    @BindView(R.id.bar_chart)
    BarChartView chartView;

    @BindView(R.id.name)
    TextView name;

    public BooleanMetricValueSummaryCard(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.summary_card_boolean;
    }

    @Override
    public void bind(CompiledMetricValue compiledMetricValue) {
        chartView.reset();
        float value = compiledMetricValue.getJsonValue().get("value").getAsFloat();
        name.setText(compiledMetricValue.getMetric().getName());

        BarSet chartSet = new BarSet();

        chartSet.addBar("Yes", value);
        chartSet.addBar("No", 100f - value);
        chartSet.setColor(getResources().getColor(R.color.color_accent));

        chartView.setLabelsFormat(new DecimalFormat("0.0"));
        chartView.setLabelsColor(getResources().getColor(R.color.white));
        chartView.setAxisBorderValues(0, 100, 50);
        chartView.addData(chartSet);
        chartView.show();
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
