package com.team2052.frckrawler.client.background;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.team2052.frckrawler.client.LoginHandler;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.database.MetricValue;
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
    private final DBManager mDBmanager;
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
        this.mDBmanager = DBManager.getInstance(context, mDaoSession);
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (MetricValue widget : metricValues) {
            PitData currentData = mDaoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(robot.getId())).where(PitDataDao.Properties.MetricId.eq(widget.getMetric().getId())).where(PitDataDao.Properties.EventId.eq(mEvent.getId())).unique();
            PitData pitData = new PitData(null, robot.getId(),  widget.getMetric().getId(), mEvent.getId(), LoginHandler.getInstance(context, mDaoSession).getLoggedOnUser().getId(), widget.getValue());

            //If data already exists update it. If not, insert as a new entry.
            if (currentData != null) {
                currentData.setData(pitData.getData());
                mDaoSession.getPitDataDao().update(currentData);
            } else {
                mDaoSession.getPitDataDao().insert(pitData);
            }

        }

        //robot.setComment(comment);
        robot.update();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context, "Save Complete", Toast.LENGTH_LONG).show();
    }
}
