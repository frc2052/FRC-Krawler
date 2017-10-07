package com.team2052.frckrawler.adapters.items.smart.metric;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.helpers.metric.MetricDataHelper;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class NumberMetricSummaryCard extends BindableFrameLayout<CompiledMetricValue> {
    @BindView(R.id.bar_chart)
    LineChartView chartView;

    @BindView(R.id.name)
    TextView name;

    public NumberMetricSummaryCard(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.summary_card_int;
    }

    @Override
    public void bind(CompiledMetricValue compiledMetricValue) {
        name.setText(compiledMetricValue.getMetric().getName());

        chartView.reset();

        LineSet lineSet = new LineSet();
        lineSet.setSmooth(true);
        LineSet averageSet = new LineSet();
        for (int i = 0; i < compiledMetricValue.getMetricValues().size(); i++) {
            float v = MetricDataHelper.INSTANCE.getDoubleMetricValue(compiledMetricValue.getMetricValues().get(i)).t1.floatValue();
            lineSet.addPoint(Integer.toString(i), v);
            averageSet.addPoint(Integer.toString(i), compiledMetricValue.getJsonValue().get("value").getAsFloat());
        }

        lineSet.setColor(getResources().getColor(R.color.color_accent));
        averageSet.setColor(getResources().getColor(R.color.white));
        chartView.setLabelsFormat(new DecimalFormat("0.0"));
        chartView.setLabelsColor(getResources().getColor(R.color.white));

        if (lineSet.size() > 0) {
            chartView.addData(lineSet);
            chartView.addData(averageSet);
        } else {
            averageSet.addPoint("0", 0);
            chartView.addData(averageSet);
        }
        chartView.show();
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
