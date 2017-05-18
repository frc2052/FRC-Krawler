package com.team2052.frckrawler.metric;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.data.tba.JSON;
import com.team2052.frckrawler.helpers.Tuple2;
import com.team2052.frckrawler.helpers.metric.MetricDataHelper;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.view.ListIndexMetricWidget;
import com.team2052.frckrawler.models.Metric;

import java.util.List;
import java.util.Map;

public abstract class IndexValueMetricType<M extends ListIndexMetricWidget> extends MetricType<M> {
    private static JsonObject compiledValueToJson(Map<Integer, Tuple2<String, Double>> compiledMap, JsonArray possible_values) {
        JsonObject compiledValue = new JsonObject();
        JsonArray values = JSON.getGson().toJsonTree(Tuple2.yieldValues(compiledMap.values()).toArray()).getAsJsonArray();
        compiledValue.add("names", possible_values);
        compiledValue.add("values", values);
        return compiledValue;
    }

    @Override
    public CompiledMetricValue compile(Metric metric, List<MetricValue> metricValues, float weight) {
        final JsonArray possibleValues = JSON.getAsJsonObject(metric.getData()).get("values").getAsJsonArray();
        final Map<Integer, Tuple2<String, Double>> compiledMap = Maps.newTreeMap();
        double denominator = 0.0;

        for (int i = 0; i < possibleValues.size(); i++) {
            compiledMap.put(i, new Tuple2<>(possibleValues.get(i).getAsString(), 0.0));
        }

        if (metricValues.isEmpty()) {
            return CompiledMetricValue.create(metric, metricValues, compiledValueToJson(compiledMap, possibleValues));
        }

        for (int i = 0; i < metricValues.size(); i++) {
            MetricValue value = metricValues.get(i);
            Tuple2<List<Integer>, MetricDataHelper.ReturnResult> value_result = MetricDataHelper.getListIndexMetricValue(value);
            if (value_result.t2.isError) {
                continue;
            }

            double weightForMatch = MetricDataHelper.getCompileWeightForMatchNumber(value, metricValues, weight);

            for (Integer index : value_result.t1) {
                if (!compiledMap.containsKey(index)) {
                    continue;
                }

                double newValue = compiledMap.get(index).t2 + weightForMatch;
                compiledMap.put(index, compiledMap.get(index).setT2(newValue));
            }

            denominator += weightForMatch;
        }

        for (Map.Entry<Integer, Tuple2<String, Double>> entry : compiledMap.entrySet()) {
            double percent = Math.round((entry.getValue().t2 / denominator * 100) * 100.0)
                    / 100.0;
            compiledMap.put(entry.getKey(), entry.getValue().setT2(percent));
        }

        return CompiledMetricValue.create(metric, metricValues, compiledValueToJson(compiledMap, possibleValues));
    }

    @Override
    public String convertCompiledValueToString(JsonObject jsonObject) {
        JsonArray names = jsonObject.get("names").getAsJsonArray();
        JsonArray values = jsonObject.get("values").getAsJsonArray();
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            value.append(String.format("%s:%s%s\n", names.get(i).getAsString(), values.get(i).getAsDouble(), '%'));
        }
        return value.toString();
    }
}
