package com.team2052.frckrawler.background;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;

/**
 * Created by adam on 6/16/15.
 */
public class DeleteEventTask extends AsyncTask<Void, Void, Void> {

    private Activity activity;
    private boolean finishActivity;
    private final Context context;
    private final Event event;
    private final DBManager mDBManager;

    public DeleteEventTask(Activity activity, Event event, boolean finishActivity) {
        this(activity, event);
        this.activity = activity;
        this.finishActivity = finishActivity;
    }

    public DeleteEventTask(Context context, Event event) {
        this.context = context;
        this.event = event;
        this.mDBManager = DBManager.getInstance(context);
        activity = null;
        finishActivity = false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mDBManager.runInTx(() -> mDBManager.getEventsTable().delete(event));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (activity != null && finishActivity) {
            activity.finish();
        }
    }
}
