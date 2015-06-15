package com.team2052.frckrawler.background;

import android.os.AsyncTask;

import com.team2052.frckrawler.database.DBManager;

/**
 * @author Adam
 * @since 1/22/2015.
 */
public class DeleteAllDataTask extends AsyncTask<Void, Void, Void> {
    private DBManager mDaoSession;

    public DeleteAllDataTask(DBManager daoSession) {
        this.mDaoSession = daoSession;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mDaoSession.runInTx(mDaoSession::deleteAll);
        return null;
    }
}
