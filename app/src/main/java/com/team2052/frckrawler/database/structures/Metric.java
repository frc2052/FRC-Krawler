package com.team2052.frckrawler.database.structures;

import com.team2052.frckrawler.database.DBContract;

public class Metric implements Structure {

    private int id;
    private String gameName;
    private String metricName;
    private String description;
    private String key;
    private int type;
    private Object[] range;
    private boolean display;
    private int position;

	/*
     * Types
	 * 
	 * 0 - BOOLEAN
	 * 1 - COUNTER
	 * 2 - SLIDER
	 * 3 - CHOOSER
	 * 4 - TEXT
	 * 5 - MATH
	 * 
	 * 
	 * The 'range' array is used different for different 'type' values
	 * 
	 * BOOLEAN - not used, set to null
	 * COUNTER - first value is the lower limit, second is the upper, third is the 
	 * incrementation. They are expected to be Integer objects.
	 * 
	 * SLIDER - first value is the lower limit, second is the upper. They are expected 
	 * to be Integer objects.
	 * 
	 * CHOOSER - all the values that the user can select
	 * TEXT - not used, set to null
	 * MATH - the list of addbutton IDs to do the operation on
	 */

    /*
     * WARNING! - it is not advised to use these constructors. Use the static
     * methods in MetricFactory instead.
     */
    public Metric(int _id, String _gameName, String _metricName, String _description, String _key, int _type, Object[] _range, boolean _displayed) {
        this(_id, _gameName, _metricName, _description, _key, _type, _range, _displayed, 0);
    }

    public Metric(int _id, String _gameName, String _metricName, String _description, String _key, int _type, Object[] _range, boolean _displayed, int _position) {
        id = _id;
        gameName = _gameName;
        metricName = _metricName;
        description = _description;
        key = _key;
        type = _type;
        range = _range;
        display = _displayed;
        position = _position;

        if (type == 0 || type == 4)
            range = null;
    }

    public int getID() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

    public int getType() {
        return type;
    }

    public Object[] getRange() {
        return range;
    }

    public boolean isDisplayed() {
        return display;
    }

    public int getPosition() {
        return position;
    }

    public boolean isNumericChooser() {
        boolean isNumeric = true;
        for (Object o : range) {
            try {
                Double.parseDouble(o.toString());
            } catch (NumberFormatException e) {
                isNumeric = false;
                break;
            }
        }
        return isNumeric && type == DBContract.CHOOSER;
    }

    public static class MetricFactory {

        /**
         * **
         * Class: MatchPerformanceFactory
         * <p/>
         * Summary: This provides easier creation of metrics. Instead of
         * having to know if you can use an array, and the type number
         * for your metrics, you can use the factory.
         * ***
         */

        //BOOLEAN METRICS
        public static Metric createBooleanMetric(String game, String name, String description) {
            return createBooleanMetric(game, name, description, true);
        }

        public static Metric createBooleanMetric(String game, String name, String description, boolean displayed) {
            return createBooleanMetric(-1, game, name, description, null, displayed);
        }

        public static Metric createBooleanMetric(int id, String game, String name, String description, String key, boolean displayed) {
            return new Metric(id, game, name, description, key, 0, null, displayed);
        }

        //COUNTER METRICS
        public static Metric createCounterMetric(String game, String name, String description, int min, int max, int incrementation) {
            return createCounterMetric(game, name, description, min, max, incrementation, true);
        }

        public static Metric createCounterMetric(String game, String name, String description, int min, int max, int incrementation, boolean displayed) {

            return createCounterMetric(-1, game, name, description, min, max, incrementation, null, displayed);
        }

        public static Metric createCounterMetric(int id, String game, String name, String description, int min, int max, int incrementation, String key, boolean displayed) {
            return new Metric(id, game, name, description, key, 1, new Integer[]{Integer.valueOf(min), Integer.valueOf(max), Integer.valueOf(incrementation)}, displayed
            );
        }

        //SLIDER METRICS
        public static Metric createSliderMetric(String game, String name, String description, int min, int max) {
            return createSliderMetric(game, name, description, min, max, true);
        }

        public static Metric createSliderMetric(String game, String name, String description, int min, int max, boolean displayed) {
            return createSliderMetric(-1, game, name, description, min, max, null, displayed);
        }

        public static Metric createSliderMetric(int id, String game, String name, String description, int min, int max, String key, boolean displayed) {
            return new Metric(id, game, name, description, key, 2, new Integer[]{Integer.valueOf(min), Integer.valueOf(max)}, displayed);
        }

        //CHOOSER METRICS
        public static Metric createChooserMetric(String game, String name, String description, String[] choices) {
            return createChooserMetric(game, name, description, choices, true);
        }

        public static Metric createChooserMetric(String game, String name, String description, String[] choices, boolean displayed) {
            return createChooserMetric(-1, game, name, description, choices, null, displayed);
        }

        public static Metric createChooserMetric(int id, String game, String name, String description, String[] choices, String key, boolean displayed) {
            return new Metric(id, game, name, description, key, 3, choices, displayed);
        }

        //TEXT METRICS
        public static Metric createTextMetric(String game, String name, String description) {
            return createTextMetric(game, name, description, true);
        }

        public static Metric createTextMetric(String game, String name, String description, boolean displayed) {
            return createTextMetric(-1, game, name, description, null, displayed);
        }

        public static Metric createTextMetric(int id, String game, String name, String description, String key, boolean displayed) {
            return new Metric(id, game, name, description, key, 4, null, displayed);
        }

        //MATH METRICS
        public static Metric createMathMetric(String game, String name, String description, Integer[] operendIDs) {
            return createMathMetric(game, name, description, operendIDs, true);
        }

        public static Metric createMathMetric(String game, String name, String description, Integer[] operendIDs, boolean displayed) {
            return createMathMetric(-1, game, name, description, operendIDs, null, displayed);
        }

        public static Metric createMathMetric(int id, String game, String name, String description, Integer[] operendIDs, String key, boolean displayed) {
            return new Metric(id, game, name, description, key, 5, operendIDs, displayed);
        }
    }


}
