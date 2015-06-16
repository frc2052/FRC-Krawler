package com.team2052.frckrawler.background;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Game;

/**
 * Created by adam on 6/14/15.
 */
public class DeleteGameTask extends AsyncTask<Void, Void, Void> {
    private final DBManager mDBManager;
    Game game;
    Activity activity;
    Context context;
    private boolean finishActivity = false;

    public DeleteGameTask(Activity activity, Game game, boolean finishActivity) {
        this.game = game;
        this.activity = activity;
        this.context = activity;
        this.finishActivity = finishActivity;
        this.mDBManager = DBManager.getInstance(activity);
    }

    public DeleteGameTask(Context context, Game game) {
        this.context = context;
        this.game = game;
        this.mDBManager = DBManager.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        mDBManager.runInTx(() -> mDBManager.getGamesTable().delete(game));
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (finishActivity) {
            activity.finish();
        }
    }
}
