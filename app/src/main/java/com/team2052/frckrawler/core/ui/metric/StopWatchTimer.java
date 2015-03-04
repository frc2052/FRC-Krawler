package com.team2052.frckrawler.core.ui.metric;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.db.Metric;

import java.util.ArrayList;

/**
 * @author Adam
 * @since 1/18/2015.
 */
public class StopWatchTimer extends MetricWidget {
    private final ArrayList<Long> times = new ArrayList<>();
    private long mStartTime;
    private long mLastSplit;
    private TimeUpdater mUpdater;

    protected StopWatchTimer(Context context, Metric m, String val) {
        super(context, m, val);
    }

    public static long getCurrentTime() {
        return SystemClock.elapsedRealtime();
    }

    public void reset() {
        mStartTime = 0;
        mLastSplit = mStartTime;
    }

    public void stop() {
        mUpdater.cancel(false);
    }

    public void start() {
        mStartTime = getCurrentTime();
        mLastSplit = mStartTime;
        mUpdater = new TimeUpdater();
        mUpdater.execute();
    }

    public void lap() {
        long now = getCurrentTime();
        long elapse = now - mLastSplit;
        times.add(elapse);
        mLastSplit = now;
    }

    public void clearTimes() {
        times.clear();
    }

    @Override
    public JsonElement getData() {
        return null;
    }

    public class TimeUpdater extends AsyncTask<Void, Long, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (!isCancelled()) {
                    publishProgress(getCurrentTime() - mLastSplit);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            //Update the text view
        }
    }
}
