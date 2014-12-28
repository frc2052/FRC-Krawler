package com.team2052.frckrawler.background;

import android.content.Context;
import android.os.AsyncTask;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.bluetooth.scout.LoginHandler;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.Robot;

import java.util.List;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class SavePitMetricsTask extends AsyncTask<Void, Void, Void> {

    private final DaoSession mDaoSession;
    private Context context;
    private Event mEvent;
    private Robot robot;
    private List<MetricValue> metricValues;
    private String comment;

    public SavePitMetricsTask(Context context, Event event, Robot robot, List<MetricValue> metricValues, String comment) {
        this.context = context;
        this.mEvent = event;
        this.robot = robot;
        this.metricValues = metricValues;
        this.comment = comment;
        this.mDaoSession = ((FRCKrawler) context.getApplicationContext()).getDaoSession();
    }

    @Override
    protected Void doInBackground(Void... params) {
        LoginHandler loginHandler = LoginHandler.getInstance(context, mDaoSession);

        if (!loginHandler.isLoggedOn() && !loginHandler.loggedOnUserStillExists()) {
            loginHandler.login();
        }

        for (MetricValue widget : metricValues) {
            List<PitData> currentData = mDaoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(robot.getId())).where(PitDataDao.Properties.MetricId.eq(widget.getMetric().getId())).list();
            PitData pitData = new PitData(widget.getValue(), robot.getId(), widget.getMetric().getId(), mEvent.getId(), loginHandler.getLoggedOnUser().getId());

            if (currentData.size() > 0) {
                for (PitData data : currentData) {
                    data.delete();
                }
            }

            mDaoSession.getPitDataDao().insert(pitData);
        }

        robot.setComments(comment);
        robot.update();
        return null;
    }
}
