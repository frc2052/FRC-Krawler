package com.team2052.frckrawler.database;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.Tuple2;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import static com.team2052.frckrawler.database.MetricHelper.MetricType;

/**
 * @author Adam
 */
public class CompiledMetricValue {
    private static final DecimalFormat format = new DecimalFormat("0.0");
    private final List<MetricValue> metricData;
    private final Robot robot;
    private final MetricType metricType;
    private final Metric metric;
    private final double compileWeight;
    private final JsonObject compiledValue = new JsonObject();

    public CompiledMetricValue(Robot robot, Metric metric, List<MetricValue> metricData, MetricType metricType, float compileWeight) {
        this.robot = robot;
        this.metric = metric;
        this.metricData = metricData;
        this.metricType = metricType;
        //Compute the compile weight
        this.compileWeight = compileWeight;
        compileMetricValues();
    }

    private void compileMetricValues() {
        String value;
        double numerator = 0;
        double denominator = 0;
        double weight;
        switch (metricType) {
            case BOOLEAN:
                if (metricData.isEmpty()) {
                    compiledValue.addProperty("value", 0.0);
                    break;
                }

                for (MetricValue metricValue : metricData) {
                    Tuple2<Boolean, MetricHelper.ReturnResult> result = MetricHelper.compileBooleanMetricValue(metricValue);

                    if (result.t2.isError)
                        continue;

                    weight = getCompileWeightForMatchNumber(metricValue);

                    if (result.t1) {
                        numerator += weight;
                    } else {
                        denominator += weight;
                    }
                }

                value = format.format((numerator / (numerator + denominator)) * 100);
                compiledValue.addProperty("value", value);
                break;
            //Do the same for slider and counter
            case SLIDER:
            case COUNTER:
                if (metricData.isEmpty()) {
                    compiledValue.addProperty("value", 0.0);
                    break;
                }

                for (MetricValue metricValue : metricData) {
                    final Tuple2<Integer, MetricHelper.ReturnResult> result = MetricHelper.getIntMetricValue(metricValue);

                    if (result.t2.isError)
                        continue;

                    weight = getCompileWeightForMatchNumber(metricValue);
                    numerator += result.t1 * weight;
                    denominator += weight;
                }

                value = format.format(numerator / denominator);
                compiledValue.addProperty("value", value);
                break;
            case CHOOSER:
            case CHECK_BOX:
                JsonArray possible_values = JSON.getAsJsonObject(metric.getData()).get("values").getAsJsonArray();
                Map<Integer, Tuple2<String, Double>> compiledVal = Maps.newTreeMap();

                for (int i = 0; i < possible_values.size(); i++) {
                    compiledVal.put(i, new Tuple2<>(possible_values.get(i).getAsString(), 0.0));
                }

                if (metricData.isEmpty()) {
                    JsonArray values = JSON.getGson().toJsonTree(Tuple2.yieldValues(compiledVal.values()).toArray()).getAsJsonArray();
                    compiledValue.add("names", possible_values);
                    compiledValue.add("values", values);
                    break;
                }


                for (MetricValue metricValue : metricData) {
                    final Tuple2<List<Integer>, MetricHelper.ReturnResult> result = MetricHelper.getListIndexMetricValue(metricValue);
                    if (result.t2.isError)
                        continue;

                    weight = getCompileWeightForMatchNumber(metricValue);

                    for (Integer index : result.t1)
                        compiledVal.put(index, compiledVal.get(index).setT2(compiledVal.get(index).t2 + weight));

                    denominator += weight;
                }

                for (Map.Entry<Integer, Tuple2<String, Double>> entry : compiledVal.entrySet()) {
                    compiledVal.put(entry.getKey(), entry.getValue().setT2(entry.getValue().t2 / denominator * 100));
                }

                JsonArray values = JSON.getGson().toJsonTree(Tuple2.yieldValues(compiledVal.values()).toArray()).getAsJsonArray();
                compiledValue.add("names", possible_values);
                compiledValue.add("values", values);
                break;
        }
    }

    public JsonObject getCompiledValueJson() {
        return compiledValue;
    }

    public String getCompiledValue() {
        switch (metricType) {
            case COUNTER:
            case SLIDER:
            case BOOLEAN:
                return String.valueOf(compiledValue.get("value").getAsDouble());
            case CHOOSER:
            case CHECK_BOX:
                JsonArray names = compiledValue.get("names").getAsJsonArray();
                JsonArray values = compiledValue.get("values").getAsJsonArray();
                String value = "";
                for (int i = 0; i < names.size(); i++) {
                    value += String.format("%s:%s%s", names.get(i).getAsString(), values.get(i).getAsDouble(), '%');
                }
                return value;
        }
        return "Error";
    }

    private double getCompileWeightForMatchNumber(MetricValue metricValue) {
        return Math.pow(compileWeight, metricData.indexOf(metricValue) + 1);
    }

    public Metric getMetric() {
        return metric;
    }

    public Robot getRobot() {
        return robot;
    }
}
