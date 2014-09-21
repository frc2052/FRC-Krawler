package com.team2052.frckrawler.database;


import com.team2052.frckrawler.database.models.Metric;

import java.text.DecimalFormat;

public class MetricValue {

    private Metric metric;
    private String[] value; //Array only used for
    private int[] chooserCounts;    //Only used if this addbutton value is a COMPILED chooser

    public MetricValue(Metric metric, String[] value) throws MetricTypeMismatchException {
        this(metric, value, null);
    }

    public MetricValue(Metric metric, String[] value, int[] chooserCounts) throws MetricTypeMismatchException {
        if (metric.type == Metric.COUNTER || metric.type == Metric.SLIDER) {
            for (String v : value) {
                //Check to see if the values are valid
                try {
                    Double.parseDouble(v);
                } catch (NumberFormatException e) {
                    throw new MetricTypeMismatchException();
                }
            }
        }

        this.metric = metric;
        this.value = value;
        this.chooserCounts = null;
    }

    public String getValueAsDBReadableString() {
        if (value == null)
            return new String();

        String returnString = new String();
        for (String aValue : value) {
            returnString += aValue + ":";
        }

        return returnString;
    }

    public String getValueAsHumanReadableString() {
        if (value == null)
            return new String();

        String returnString = new String();
        for (int i = 0; i < value.length; i++) {
            boolean isDecimal = true;

            try {
                Double.parseDouble(value[i]);
            } catch (NumberFormatException e) {
                isDecimal = false;
            }

            if (isDecimal) {
                DecimalFormat format = new DecimalFormat("0.00");
                if (i != value.length - 1) {
                    returnString += format.format(Double.parseDouble(value[i]));
                    if (metric.type == Metric.BOOLEAN)
                        returnString += "%";
                    returnString += ", ";
                } else {
                    returnString += format.format(Double.parseDouble(value[i]));
                    if (metric.type == Metric.BOOLEAN)
                        returnString += "%";
                }

            } else {
                if (i != value.length - 1 && !value[i].trim().equals(""))
                    returnString += value[i] + ", ";
                else
                    returnString += value[i];
            }
        }

        return returnString;
    }

    public Metric getMetric() {
        return metric;
    }

    public String[] getValue() {
        return value;
    }

    public int[] getChooserCounts() {
        return chooserCounts;
    }

    public class MetricTypeMismatchException extends Exception {
    }
}
