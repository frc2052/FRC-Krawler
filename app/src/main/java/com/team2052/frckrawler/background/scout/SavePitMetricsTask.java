package com.team2052.frckrawler.background.scout;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.team2052.frckrawler.bluetooth.client.LoginHandler;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;

import java.util.List;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class SavePitMetricsTask extends AsyncTask<Void, Void, Void> {

    private final DBManager mDaoSession;
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
        this.mDaoSession = DBManager.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (MetricValue widget : metricValues) {
            PitData pitData = new PitData(
                    null,
                    robot.getId(),
                    widget.getMetric().getId(),
                    mEvent.getId(),
                    LoginHandler.getInstance(context, mDaoSession).getLoggedOnUser().getId(),
                    widget.getValue());
            mDaoSession.getPitDataTable().insert(pitData);
        }

        robot.setComments(comment);
        robot.update();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context, "Save Complete", Toast.LENGTH_LONG).show();
    }
}
