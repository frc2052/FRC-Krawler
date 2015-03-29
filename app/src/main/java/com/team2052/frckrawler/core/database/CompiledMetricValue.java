package com.team2052.frckrawler.core.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    private String compiledValue = "";
    private Metric metric;
    private double compileWeight;

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
        switch (metricType) {
            case MetricUtil.BOOLEAN:
                double yes = 0;
                double no = 0;

                for (MetricValue matchData : metricData) {
                    //Get the value
                    Boolean data = JSON.getAsJsonObject(matchData.getValue()).get("value").getAsBoolean();
                    //Parse the value and weight it
                    if (data) {
                        yes += compileWeight;
                    } else {
                        no += compileWeight;
                    }
                }
                //Check to see if it is a NaN if it is then set the value to 0.0
                //Return the amount of yes in the amount of yes and no's
                if (!Double.isNaN(yes / (yes + no) * 100)) {
                    compiledValue = format.format((yes / (yes + no)) * 100);
                } else {
                    compiledValue = "0.0";
                }
                break;
            //Do the same for slider and counter
            case MetricUtil.SLIDER:
            case MetricUtil.COUNTER:
                double numerator = 0;
                double denominator = 0;

                for (MetricValue metricValue : metricData) {
                    int value = JSON.getAsJsonObject(metricValue.getValue()).get("value").getAsInt();
                    numerator += value * compileWeight;
                    denominator += compileWeight;
                }

                if (denominator == 0)
                    denominator = 1;

                if (!Double.isNaN(numerator / denominator)) {
                    compiledValue = format.format(numerator / denominator);
                } else {
                    compiledValue = "0.0";
                }

                break;
            case MetricUtil.CHOOSER:
            case MetricUtil.CHECK_BOX:
                Map<Integer, String> valueNames = new TreeMap<>();
                JsonArray possible_values = JSON.getAsJsonObject(metric.getData()).get("values").getAsJsonArray();
                for (int i = 0; i < possible_values.size(); i++) {
                    valueNames.put(i, possible_values.get(i).getAsString());
                }

                if (metricData.isEmpty()) {
                    for (String value : valueNames.values()) {
                        compiledValue += String.format("%s:%s%s ", value, 0.0, '%');
                    }
                    break;
                }

                Map<Integer, Integer> compiledVal = new TreeMap<>();

                for (Integer integer : valueNames.keySet()) {
                    compiledVal.put(integer, 0);
                }

                for (MetricValue value : metricData) {
                    JsonArray values = JSON.getAsJsonObject(value.getValue()).get("values").getAsJsonArray();

                    for (JsonElement element : values) {
                        int index = element.getAsInt();

                        compiledVal.put(index, compiledVal.get(index) + 1);
                    }
                }

                for (Map.Entry<Integer, Integer> entry : compiledVal.entrySet()) {
                    double i = entry.getValue() / metricData.size() * 100;
                    compiledValue += String.format("%s:%s%s ", valueNames.get(entry.getKey()), format.format(i), '%');
                }
                break;
        }
    }

    public String getCompiledValue() {
        return compiledValue;
    }

    public Metric getMetric() {
        return metric;
    }

    public Robot getRobot() {
        return robot;
    }
}
