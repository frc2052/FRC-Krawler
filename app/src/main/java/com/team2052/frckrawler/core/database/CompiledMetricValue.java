package com.team2052.frckrawler.core.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.util.MetricUtil;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Adam
 */
public class CompiledMetricValue {
    public static final DecimalFormat format = new DecimalFormat("0.0");
    private final List<MetricValue> metricData;
    private Robot robot;
    private int metricType;
    private Metric metric;
    private double compileWeight;
    private JsonObject compiledValue = new JsonObject();

    public CompiledMetricValue(Robot robot, Metric metric, List<MetricValue> metricData, int metricType, float compileWeight) {
        this.robot = robot;
        this.metric = metric;
        this.metricData = metricData;
        this.metricType = metricType;
        //Compute the compile weight
        this.compileWeight = Math.pow(compileWeight, metricData.size());
        compileMetricValues();
    }

    private void compileMetricValues() {
        String value;
        double numerator = 0;
        double denominator = 0;
        switch (metricType) {
            case MetricUtil.BOOLEAN:
                if (metricData.isEmpty()) {
                    compiledValue.addProperty("value", 0.0);
                    break;
                }

                for (MetricValue matchData : metricData) {
                    //Get the value
                    Boolean data = JSON.getAsJsonObject(matchData.getValue()).get("value").getAsBoolean();
                    //Parse the value and weight it
                    if (data) {
                        numerator += compileWeight;
                    } else {
                        denominator += compileWeight;
                    }
                }

                //Check to see if it is a NaN if it is then set the value to 0.0
                //Return the amount of yes in the amount of yes and no's
                value = format.format((numerator / (numerator + denominator)) * 100);
                compiledValue.addProperty("value", value);
                break;
            //Do the same for slider and counter
            case MetricUtil.SLIDER:
            case MetricUtil.COUNTER:
                if (metricData.isEmpty()) {
                    compiledValue.addProperty("value", 0.0);
                    break;
                }

                for (MetricValue metricValue : metricData) {
                    int int_val = JSON.getAsJsonObject(metricValue.getValue()).get("value").getAsInt();
                    numerator += int_val * compileWeight;
                    denominator += compileWeight;
                }

                value = format.format(numerator / denominator);
                break;
            case MetricUtil.CHOOSER:
            case MetricUtil.CHECK_BOX:
                JsonArray possible_values = JSON.getAsJsonObject(metric.getData()).get("values").getAsJsonArray();
                Map<Integer, String> valueNames = new TreeMap<>();
                Map<Integer, Double> compiledVal = new TreeMap<>();
                denominator = 0.0;

                for (int i = 0; i < possible_values.size(); i++) {
                    valueNames.put(i, possible_values.get(i).getAsString());
                    compiledVal.put(i, 0.0);
                }

                JsonArray names = JSON.getGson().toJsonTree(valueNames.values()).getAsJsonArray();

                if (metricData.isEmpty()) {
                    JsonArray values = JSON.getGson().toJsonTree(compiledVal.values()).getAsJsonArray();
                    compiledValue.add("values", values);
                    compiledValue.add("names", names);
                    break;
                }


                for (MetricValue metricValue : metricData) {
                    JsonArray values = JSON.getAsJsonObject(metricValue.getValue()).get("values").getAsJsonArray();

                    for (JsonElement element : values) {
                        int index = element.getAsInt();
                        compiledVal.put(index, compiledVal.get(index) + compileWeight);
                    }
                    denominator += compileWeight;
                }

                for (Map.Entry<Integer, Double> entry : compiledVal.entrySet()) {
                    compiledVal.put(entry.getKey(), entry.getValue() / denominator * 100);
                }

                JsonArray values = JSON.getGson().toJsonTree(compiledVal.values()).getAsJsonArray();
                compiledValue.add("names", names);
                compiledValue.add("values", values);
                break;
        }
    }

    public JsonObject getCompiledValueJson() {
        return compiledValue;
    }

    public String getCompiledValue() {
        switch (metricType) {
            case MetricUtil.COUNTER:
            case MetricUtil.SLIDER:
            case MetricUtil.BOOLEAN:
                return compiledValue.get("value").getAsString();
            case MetricUtil.CHOOSER:
            case MetricUtil.CHECK_BOX:
                JsonArray names = compiledValue.get("names").getAsJsonArray();
                JsonArray values = compiledValue.get("values").getAsJsonArray();
                String value = "";
                for (int i = 0; i < names.size(); i++) {
                    value += String.format("%s:%s%s", names.get(i).getAsString(), values.get(i).getAsDouble(), '%');
                }
                return value;
        }
        return "-1.0 Error";
    }

    public Metric getMetric() {
        return metric;
    }

    public Robot getRobot() {
        return robot;
    }
}
