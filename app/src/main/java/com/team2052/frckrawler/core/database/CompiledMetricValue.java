package com.team2052.frckrawler.core.database;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.util.Utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
        final Gson gson = JSON.getGson();
        switch (metricType) {
            case Utilities.MetricUtil.BOOLEAN:
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
            case Utilities.MetricUtil.SLIDER:
            case Utilities.MetricUtil.COUNTER:
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
            case Utilities.MetricUtil.CHOOSER:
                JsonArray range = JSON.getAsJsonArray(metric.getRange());
                int rangeCnt = range.size();
                List<String> rangeValues = new ArrayList<>();

                for (JsonElement jsonElement : range) {
                    JsonObject object = jsonElement.getAsJsonObject();
                    rangeValues.add(object.get("value").getAsString());
                }


                double[] counts = new double[rangeValues.size()];

                for (MetricValue metricValue : metricData) {
                    String value = JSON.getAsJsonObject(metricValue.getValue()).get("value").getAsString();
                    for (int i = 0; i < rangeCnt; i++) {
                        if (value.equals(rangeValues.get(i))) {
                            counts[i]++;
                            break;
                        }
                    }
                }

                compiledValue += "\n";
                for (int choiceCount = 0; choiceCount < rangeCnt; choiceCount++) {
                    compiledValue += rangeValues.get(choiceCount) + " " + format.format(Double.isNaN((counts[choiceCount] / metricData.size()) * 100) ? 0.0D : (counts[choiceCount] / metricData.size()) * 100) + "% \n";
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
