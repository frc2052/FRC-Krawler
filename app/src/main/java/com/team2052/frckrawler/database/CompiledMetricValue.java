package com.team2052.frckrawler.database;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.MetricUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

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
                    if (matchData.getValue() != null) {
                        //Get the value
                        JsonObject values = JSON.getAsJsonObject(matchData.getValue());

                        boolean data = false;

                        if (values.has("value") && !values.get("value").isJsonNull()) {
                            try {
                                data = values.get("value").getAsBoolean();
                            } catch (NumberFormatException e) {
                                data = false;
                            }
                        }

                        if (data) {
                            numerator += compileWeight;
                        } else {
                            denominator += compileWeight;
                        }
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
                    if (metricValue.getValue() != null) {
                        JsonObject values = JSON.getAsJsonObject(metricValue.getValue());

                        int int_val = 0;

                        if (values.has("value") && !values.get("value").isJsonNull()) {
                            try {
                                int_val = values.get("value").getAsInt();
                            } catch (NumberFormatException e) {
                                int_val = 0;
                            }
                        }

                        numerator += int_val * compileWeight;
                        denominator += compileWeight;
                    }
                }

                value = format.format(numerator / denominator);
                compiledValue.addProperty("value", value);
                break;
            case MetricUtil.CHOOSER:
            case MetricUtil.CHECK_BOX:
                JsonArray possible_values = JSON.getAsJsonObject(metric.getData()).get("values").getAsJsonArray();
                Map<Integer, String> valueNames = Maps.newTreeMap();
                Map<Integer, Double> compiledVal = Maps.newTreeMap();
                denominator = 0.0;

                for (int i = 0; i < possible_values.size(); i++) {
                    valueNames.put(i, possible_values.get(i).getAsString());
                    compiledVal.put(i, 0.0);
                }

                JsonArray names = JSON.getGson().toJsonTree(valueNames.values().toArray()).getAsJsonArray();

                if (metricData.isEmpty()) {
                    JsonArray values = JSON.getGson().toJsonTree(compiledVal.values().toArray()).getAsJsonArray();
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
                JsonArray values = JSON.getGson().toJsonTree(compiledVal.values().toArray()).getAsJsonArray();
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
        return "Error";
    }

    public Metric getMetric() {
        return metric;
    }

    public Robot getRobot() {
        return robot;
    }
}
