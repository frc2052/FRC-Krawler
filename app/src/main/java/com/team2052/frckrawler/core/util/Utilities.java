package com.team2052.frckrawler.core.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.activities.ScoutActivity;
import com.team2052.frckrawler.core.database.CompiledMetricValue;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/27/14.
 */
public class Utilities {
    public static int getPixelsFromDp(Context c, int dipValue) {
        Resources r = c.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

    public static class ScoutUtil {
        @Nullable
        public static Set<BluetoothDevice> getAllBluetoothDevices() {
            if (!BluetoothUtil.hasBluetoothAdapter()) {
                return null;
            }
            return BluetoothUtil.getBluetoothAdapter().getBondedDevices();
        }


        @Nullable
        public static BluetoothDevice[] getAllBluetoothDevicesArray() {
            if (!BluetoothUtil.hasBluetoothAdapter()) {
                return null;
            }
            return BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray(new BluetoothDevice[BluetoothAdapter.getDefaultAdapter().getBondedDevices().size()]);
        }

        public static CharSequence[] getDeviceNames(@Nullable Set<BluetoothDevice> bluetoothDevices) {
            if (bluetoothDevices == null) {
                return new CharSequence[0];
            }

            BluetoothDevice[] devices = bluetoothDevices.toArray(new BluetoothDevice[bluetoothDevices.size()]);
            CharSequence[] deviceNames = new CharSequence[devices.length];

            for (int i = 0; i < deviceNames.length; i++) {
                deviceNames[i] = devices[i].getName();
            }

            return deviceNames;
        }

        public static void setDeviceAsScout(Context context, boolean isScout) {
            SharedPreferences.Editor editor = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0).edit();
            editor.putBoolean(GlobalValues.IS_SCOUT_PREF, isScout);
            editor.apply();
        }

        public static boolean getDeviceIsScout(Context context) {
            SharedPreferences preferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            return preferences.getBoolean(GlobalValues.IS_SCOUT_PREF, false);
        }

        public static void setSyncDevice(Context context, BluetoothDevice device) {
            SharedPreferences prefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, device.getAddress());
            prefsEditor.apply();
        }

        public static BluetoothDevice getSyncDevice(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            String address = prefs.getString(GlobalValues.MAC_ADRESS_PREF, "null");
            if (address.equals("null")) {
                return null;
            }
            return BluetoothUtil.getDevice(address);
        }

        @Nullable
        public static Event getScoutEvent(Context context, DaoSession session) {
            SharedPreferences scoutPrefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            if (scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
                return session.getEventDao().load(scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
            }
            return null;
        }

        public static void resetSyncDevice(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, "null");
            prefsEditor.apply();
        }
    }

    /**
     * @author Adam
     * @since 12/21/2014.
     */
    public static class MetricUtil {
        public static final int BOOLEAN = 0;
        public static final int COUNTER = 1;
        public static final int SLIDER = 2;
        public static final int CHOOSER = 3;
        public static final int TIMER = 4;

        public static Metric createBooleanMetric(Game game, MetricType metricCategory, String name, String description) {

            return new Metric(null, name, metricCategory.ordinal(), description, BOOLEAN, null, game.getId());
        }

        public static Metric createCounterMetric(Game game, MetricType metricCategory, String name, String description, int min, int max, int incrementation) {
            JsonObject range = new JsonObject();
            range.addProperty("min", min);
            range.addProperty("max", max);
            range.addProperty("inc", incrementation);

            return new Metric(null, name, metricCategory.ordinal(), description, COUNTER, JSON.getGson().toJson(range), game.getId());
        }

        public static Metric createSliderMetric(Game game, MetricType metricCategory, String name, String description, int min, int max) {
            JsonObject range = new JsonObject();
            range.addProperty("min", min);
            range.addProperty("max", max);

            return new Metric(null, name, metricCategory.ordinal(), description, SLIDER, JSON.getGson().toJson(range), game.getId());
        }

        public static Metric createChooserMetric(Game game, MetricType metricCategory, String name, String description, String[] choices) {
            JsonArray range = new JsonArray();

            for (String choice : choices) {
                JsonObject choiceObj = new JsonObject();
                choiceObj.addProperty("value", choice);
                range.add(choiceObj);
            }

            return new Metric(null, name, metricCategory.ordinal(), description, CHOOSER, JSON.getGson().toJson(range), game.getId());
        }

        public static Metric createTimerMetric(Game game, MetricType metricCategory, String name, String description){
            return new Metric(null, name, metricCategory.ordinal(), description, TIMER, null, game.getId());
        }

        public static enum MetricType {
            MATCH_PERF_METRICS, ROBOT_METRICS;
            public static final MetricType[] VALID_TYPES = values();
        }

    }

    public static class MetricCompiler {
        /**
         * Used to compile data based on ATTENDING TEAMS and METRIC
         *
         * @param event  the event for attending teams
         * @param metric the metric that you want to compile
         * @return the compiled data from attending teams and metric
         */
        public static List<CompiledMetricValue> getCompiledMetric(Event event, Metric metric, DaoSession daoSession, float compileWeight) {
            List<RobotEvent> robotEventses = daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(event.getId())).list();
            List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
            for (RobotEvent robotEvents : robotEventses) {
                List<MetricValue> metricData = new ArrayList<>();
                if (metric.getCategory() == MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()) {

                    QueryBuilder<MatchData> queryBuilder = daoSession.getMatchDataDao().queryBuilder();
                    queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robotEvents.getRobotId()));

                    for (MatchData matchData : queryBuilder.list()) {
                        if (matchData.getMatch().getEvent().getId().equals(event.getId())) {
                            metricData.add(new MetricValue(matchData));
                        }
                    }

                } else if (metric.getCategory() == MetricUtil.MetricType.ROBOT_METRICS.ordinal()) {
                    QueryBuilder<PitData> queryBuilder = daoSession.getPitDataDao().queryBuilder();
                    queryBuilder.where(PitDataDao.Properties.EventId.eq(event.getId()));
                    queryBuilder.where(PitDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(PitDataDao.Properties.RobotId.eq(robotEvents.getRobotId()));

                    for (PitData matchData : queryBuilder.list()) {
                        metricData.add(new MetricValue(matchData));
                    }
                }
                compiledMetricValues.add(new CompiledMetricValue(robotEvents.getRobot(), metric, metricData, metric.getType(), compileWeight));
            }
            return compiledMetricValues;
        }

        /**
         * Used to export to CSV by ROW based on PER ROBOT
         *
         * @param event
         * @param robot
         * @return
         */
        public static List<CompiledMetricValue> getCompiledRobot(Event event, Robot robot, DaoSession daoSession, float compileWeight) {
            //Load all the metrics
            final List<Metric> metrics = daoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(event.getGameId())).list();
            final List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
            for (Metric metric : metrics) {
                List<MetricValue> metricData = new ArrayList<>();
                if (metric.getCategory() == MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()) {

                    QueryBuilder<MatchData> queryBuilder = daoSession.getMatchDataDao().queryBuilder();
                    queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robot.getId()));

                    for (MatchData matchData : queryBuilder.list()) {
                        if (matchData.getMatch().getEvent().getId().equals(event.getId())) {
                            metricData.add(new MetricValue(matchData));
                        }
                    }

                } else if (metric.getCategory() == MetricUtil.MetricType.ROBOT_METRICS.ordinal()) {
                    QueryBuilder<PitData> queryBuilder = daoSession.getPitDataDao().queryBuilder();
                    queryBuilder.where(PitDataDao.Properties.EventId.eq(event.getId()));
                    queryBuilder.where(PitDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(PitDataDao.Properties.RobotId.eq(robot.getId()));

                    for (PitData matchData : queryBuilder.list()) {
                        metricData.add(new MetricValue(matchData));
                    }
                }
                compiledMetricValues.add(new CompiledMetricValue(robot, metric, metricData, metric.getType(), compileWeight));
            }
            return compiledMetricValues;
        }
    }

    /**
     * @author Adam
     * @since 12/9/2014.
     */
    public static class BluetoothUtil {

        @Nullable
        public static BluetoothAdapter getBluetoothAdapter() {
            return BluetoothAdapter.getDefaultAdapter();
        }

        public static boolean hasBluetoothAdapter() {
            return BluetoothAdapter.getDefaultAdapter() != null;
        }

        @Nullable
        public static BluetoothDevice getDevice(String address) {
            if (!hasBluetoothAdapter())
                return null;
            return getBluetoothAdapter().getRemoteDevice(address);
        }

        public static boolean isBluetoothEnabled() {
            return hasBluetoothAdapter() && getBluetoothAdapter().isEnabled();
        }


    }
}
