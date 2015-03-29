package com.team2052.frckrawler.core.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.database.CompiledMetricValue;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.database.MetricValue;
import com.team2052.frckrawler.db.Event;
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
        public static Event getScoutEvent(Context context, DBManager session) {
            SharedPreferences scoutPrefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            if (scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
                return session.getDaoSession().getEventDao().load(scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
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


    public static class MetricCompiler {
        /**
         * Used to compile data based on ATTENDING TEAMS and METRIC
         *
         * @param event  the event for attending teams
         * @param metric the metric that you want to compile
         * @return the compiled data from attending teams and metric
         */
        public static List<CompiledMetricValue> getCompiledMetric(Event event, Metric metric, DBManager dbManager, float compileWeight) {
            List<RobotEvent> robotEventses = dbManager.getDaoSession().getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(event.getId())).list();
            List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
            for (RobotEvent robotEvents : robotEventses) {
                List<MetricValue> metricData = new ArrayList<>();
                Robot robot = dbManager.getRobot(robotEvents);
                if (metric.getCategory() == MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()) {

                    QueryBuilder<MatchData> queryBuilder = dbManager.getDaoSession().getMatchDataDao().queryBuilder();
                    queryBuilder.where(MatchDataDao.Properties.EventId.eq(event.getId()));
                    queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robotEvents.getRobotId()));

                    for (MatchData matchData : queryBuilder.list()) {
                        metricData.add(new MetricValue(dbManager.getDaoSession(), matchData));
                    }

                } else if (metric.getCategory() == MetricUtil.MetricType.ROBOT_METRICS.ordinal()) {
                    QueryBuilder<PitData> queryBuilder = dbManager.getDaoSession().getPitDataDao().queryBuilder();
                    queryBuilder.where(PitDataDao.Properties.EventId.eq(event.getId()));
                    queryBuilder.where(PitDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(PitDataDao.Properties.RobotId.eq(robotEvents.getRobotId()));

                    for (PitData matchData : queryBuilder.list()) {
                        metricData.add(new MetricValue(dbManager.getDaoSession(), matchData));
                    }
                }
                compiledMetricValues.add(new CompiledMetricValue(robot, metric, metricData, metric.getType(), compileWeight));
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
        public static List<CompiledMetricValue> getCompiledRobot(Event event, Robot robot, DBManager dbManager, float compileWeight) {
            //Load all the metrics
            final List<Metric> metrics = dbManager.getDaoSession().getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(event.getGameId())).list();
            final List<CompiledMetricValue> compiledMetricValues = new ArrayList<>();
            for (Metric metric : metrics) {
                List<MetricValue> metricData = new ArrayList<>();
                if (metric.getCategory() == MetricUtil.MetricType.MATCH_PERF_METRICS.ordinal()) {

                    QueryBuilder<MatchData> queryBuilder = dbManager.getDaoSession().getMatchDataDao().queryBuilder();
                    queryBuilder.where(MatchDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(MatchDataDao.Properties.EventId.eq(event.getId()));
                    queryBuilder.where(MatchDataDao.Properties.RobotId.eq(robot.getId()));

                    for (MatchData matchData : queryBuilder.list()) {
                        if (dbManager.getEventId(matchData) == event.getId()) {
                            metricData.add(new MetricValue(dbManager.getDaoSession(), matchData));
                        }
                    }

                } else if (metric.getCategory() == MetricUtil.MetricType.ROBOT_METRICS.ordinal()) {
                    QueryBuilder<PitData> queryBuilder = dbManager.getDaoSession().getPitDataDao().queryBuilder();
                    queryBuilder.where(PitDataDao.Properties.EventId.eq(event.getId()));
                    queryBuilder.where(PitDataDao.Properties.MetricId.eq(metric.getId()));
                    queryBuilder.where(PitDataDao.Properties.RobotId.eq(robot.getId()));

                    for (PitData matchData : queryBuilder.list()) {
                        metricData.add(new MetricValue(dbManager.getDaoSession(), matchData));
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
