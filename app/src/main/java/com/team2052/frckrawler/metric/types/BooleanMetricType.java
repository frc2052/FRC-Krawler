package com.team2052.frckrawler.metric.types;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.helpers.Tuple2;
import com.team2052.frckrawler.helpers.metric.MetricDataHelper;
import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.MetricType;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.view.impl.BooleanMetricWidget;
import com.team2052.frckrawler.models.Metric;

import java.util.List;

public class BooleanMetricType extends MetricType<BooleanMetricWidget> {
    @Override
    protected Class<BooleanMetricWidget> widgetClass() {
        return BooleanMetricWidget.class;
    }

    @Override
    public int getType() {
        return MetricHelper.BOOLEAN;
    }

    @Override
    public CompiledMetricValue compile(Metric metric, List<MetricValue> metricValues, float weight) {
        if (metric.getType() != getType()) {
            throw new IllegalStateException("Metric state must be the same when compiling values!");
        }

        final JsonObject compiledValue = new JsonObject();

        if (metricValues.isEmpty()) {
            compiledValue.addProperty("value", 0.0);
            return CompiledMetricValue.create(metric, metricValues, compiledValue);
        }

        double numerator = 0.0, denominator = 0.0;

        for (MetricValue value : metricValues) {
            final Tuple2<Boolean, MetricDataHelper.ReturnResult> result = MetricDataHelper.getBooleanMetricValue(value);

            if (result.t2.isError)
                continue;

            final double compileWeightForMatchNumber = MetricDataHelper.getCompileWeightForMatchNumber(value, metricValues, weight);

            if (result.t1) {
                numerator += compileWeightForMatchNumber;
            } else {
                denominator += compileWeightForMatchNumber;
            }
        }

        final String value = MetricType.format.format(numerator / (numerator + denominator) * 100);
        compiledValue.addProperty("value", value);
        return CompiledMetricValue.create(metric, metricValues, compiledValue);
    }

    @Override
    public String convertCompiledValueToString(JsonObject jsonObject) {
        return jsonObject.get("value").getAsString();
    }
}
