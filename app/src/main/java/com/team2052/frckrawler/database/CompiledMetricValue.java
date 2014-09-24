package com.team2052.frckrawler.database;

import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.MetricMatchData;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Adam
 */
public class CompiledMetricValue {
    public static DecimalFormat format = new DecimalFormat("0.0");
    private final List<MetricMatchData> metricData;
    public Robot robot;
    public int metricType;
    public String compiledValue = "";
    private Metric metric;
    private double compileWeight;

    public CompiledMetricValue(Robot robot, Metric metric, List<MetricMatchData> metricData, int metricType, float compileWeight) {
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
            case Metric.BOOLEAN:
                double yes = 0;
                double no = 0;

                for (MetricMatchData matchData : metricData) {
                    //Get the value
                    String data = StringArrayDeserializer.deserialize(matchData.data)[0];
                    //Parse the value and weight it
                    if (Boolean.parseBoolean(data)) {
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
            case Metric.SLIDER:
            case Metric.COUNTER:
                double numerator = 0;
                double denominator = 0;

                for (MetricMatchData matchData : metricData) {
                    int value = Integer.parseInt(StringArrayDeserializer.deserialize(matchData.data)[0]);
                    numerator += value * compileWeight;
                    denominator += compileWeight;
                }

                if (!Double.isNaN(numerator / denominator)) {
                    compiledValue = format.format(numerator / denominator);
                } else {
                    compiledValue = "0.0";
                }

                break;
            case Metric.CHOOSER:
                Object[] range = metric.range;
                double[] counts = new double[range.length];

                for (MetricMatchData matchData : metricData) {
                    String value = StringArrayDeserializer.deserialize(matchData.data)[0];
                    int rangeAddress = -1;

                    for (int choiceCount = 0; choiceCount < range.length; choiceCount++) {
                        if (value.equals(range[choiceCount]))
                            rangeAddress = choiceCount;
                    }

                    if (rangeAddress != -1) {
                        counts[rangeAddress]++;
                    }
                }

                for (int choiceCount = 0; choiceCount < range.length; choiceCount++) {
                    compiledValue += range[choiceCount] + " " + format.format(Double.isNaN((counts[choiceCount] / metricData.size()) * 100) ? 0.0D : (counts[choiceCount] / metricData.size()) * 100) + "% ";
                }

                break;
        }
    }
}
