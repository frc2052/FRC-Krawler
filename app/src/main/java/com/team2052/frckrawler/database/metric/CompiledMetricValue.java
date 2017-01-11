package com.team2052.frckrawler.database.metric;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.metric.MetricTypeEntry;
import com.team2052.frckrawler.metric.MetricTypeEntryHandler;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Adam
 *         Compiled metric value, this object takes data and compiles it for you. Compiles all data on the thread it was instantiated on.
 */
public class CompiledMetricValue {
    public static final DecimalFormat format = new DecimalFormat("0.00");
    private final List<MetricValue> metricData;
    private final Robot robot;
    private final int metricType;
    private final Metric metric;
    private final double compileWeight;
    private JsonObject compiledValue = new JsonObject();

    /**
     * @param robot         The robot you are compiling
     * @param metric        The metric you are compiling
     * @param metricData    The data you are compiling
     * @param compileWeight The 'raw' compile weight from settings or another place
     */
    public CompiledMetricValue(Robot robot, Metric metric, List<MetricValue> metricData, float compileWeight) {
        this.robot = robot;
        this.metric = metric;
        this.metricData = metricData;
        this.metricType = metric.getType();
        //Compute the compile weight
        this.compileWeight = compileWeight;
        compiledValue = MetricTypeEntryHandler.INSTANCE.getTypeEntry(metricType).compileValues(robot, metric, metricData, compileWeight);
    }

    public JsonObject getCompiledValueJson() {
        return compiledValue;
    }

    /**
     * @return Takes the @link[getCompiledValueJson] and parses it to a readable String
     */
    public String getCompiledValue() {
        MetricTypeEntry<?> entry = MetricTypeEntryHandler.INSTANCE.getTypeEntry(metricType);
        if (entry != null) {
            return entry.convertValueToString(getCompiledValueJson());
        }
        return "Error";
    }

    /**
     * Get the weight for the current data
     */
    public static double getCompileWeightForMatchNumber(MetricValue metricValue, List<MetricValue> metricData, double compileWeight) {
        return Math.pow(compileWeight, metricData.indexOf(metricValue) + 1);
    }

    public Metric getMetric() {
        return metric;
    }

    public Robot getRobot() {
        return robot;
    }
}
