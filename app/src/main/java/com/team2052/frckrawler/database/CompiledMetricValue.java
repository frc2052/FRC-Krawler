package com.team2052.frckrawler.database;

import com.team2052.frckrawler.database.serializers.StringArrayDeserializer;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.util.LogHelper;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Adam
 */
public class CompiledMetricValue
{
    public static DecimalFormat format = new DecimalFormat("0.0");
    private final List<MetricValue> metricData;
    public Robot robot;
    public int metricType;
    public String compiledValue = "";
    private Metric metric;
    private double compileWeight;

    public CompiledMetricValue(Robot robot, Metric metric, List<MetricValue> metricData, int metricType, float compileWeight)
    {
        this.robot = robot;
        this.metric = metric;
        this.metricData = metricData;
        this.metricType = metricType;
        //Compute the compile weight
        this.compileWeight = Math.pow(compileWeight, metricData.size());
        compileMetricValues();
    }

    private void compileMetricValues()
    {
        switch (metricType) {
            case MetricValues.BOOLEAN:
                double yes = 0;
                double no = 0;

                for (MetricValue matchData : metricData) {
                    //Get the value
                    String data = matchData.getValue();
                    //Parse the value and weight it
                    if (Boolean.parseBoolean(data)) {
                        yes += compileWeight;
                    } else {
                        no += compileWeight;
                    }
                    LogHelper.debug(data + robot.getTeam().getNumber());
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
            case MetricValues.SLIDER:
            case MetricValues.COUNTER:
                double numerator = 0;
                double denominator = 0;

                for (MetricValue metricValue : metricData) {
                    int value = Integer.parseInt(metricValue.getValue());
                    numerator += value * compileWeight;
                    denominator += compileWeight;
                }

                if (!Double.isNaN(numerator / denominator)) {
                    compiledValue = format.format(numerator / denominator);
                } else {
                    compiledValue = "0.0";
                }

                break;
            case MetricValues.CHOOSER:
                String[] range = StringArrayDeserializer.deserialize(metric.getRange());
                double[] counts = new double[range.length];

                for (MetricValue metricValue : metricData) {
                    String value = metricValue.getValue();
                    int rangeAddress = -1;

                    for (int choiceCount = 0; choiceCount < range.length; choiceCount++) {
                        if (value.equals(range[choiceCount]))
                            rangeAddress = choiceCount;
                    }

                    if (rangeAddress != -1) {
                        counts[rangeAddress]++;
                    }
                }
                compiledValue += "\n";
                for (int choiceCount = 0; choiceCount < range.length; choiceCount++) {
                    compiledValue += range[choiceCount] + " " + format.format(Double.isNaN((counts[choiceCount] / metricData.size()) * 100) ? 0.0D : (counts[choiceCount] / metricData.size()) * 100) + "% \n";
                }

                break;
        }
    }
}
