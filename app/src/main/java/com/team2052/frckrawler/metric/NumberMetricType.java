package com.team2052.frckrawler.metric;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.helpers.Tuple2;
import com.team2052.frckrawler.helpers.metric.MetricDataHelper;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.view.MetricWidget;
import com.team2052.frckrawler.models.Metric;

import java.util.List;

public abstract class NumberMetricType<W extends MetricWidget> extends MetricType<W> {
    @Override
    public CompiledMetricValue compile(Metric metric, List<MetricValue> metricValues, float weight) {
        final JsonObject compiledValue = new JsonObject();

        if (metricValues.isEmpty()) {
            compiledValue.addProperty("value", 0.0);
            return CompiledMetricValue.create(metric, metricValues, compiledValue);
        }

        double numerator = 0.0, denominator = 0.0;

        for (MetricValue value : metricValues) {
            final Tuple2<Double, MetricDataHelper.ReturnResult> result = MetricDataHelper.getDoubleMetricValue(value);

            if (result.t2.isError)
                continue;

            final double compileWeightForMatchNumber = MetricDataHelper.getCompileWeightForMatchNumber(value, metricValues, weight);
            numerator += result.t1 * compileWeightForMatchNumber;
            denominator += compileWeightForMatchNumber;
        }
        final String value = MetricType.format.format(numerator / denominator);
        compiledValue.addProperty("value", value);
        return CompiledMetricValue.create(metric, metricValues, compiledValue);
    }

    @Override
    public String convertCompiledValueToString(JsonObject jsonObject) {
        return jsonObject.get("value").getAsString();
    }
}
