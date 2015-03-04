package com.team2052.frckrawler.client.background;

import android.os.AsyncTask;

import com.team2052.frckrawler.db.DaoSession;

/**
 * @author Adam
 * @since 1/22/2015.
 */
public class DeleteAllDataTask extends AsyncTask<Void, Void, Void> {
    private DaoSession mDaoSession;

    public DeleteAllDataTask(DaoSession daoSession) {

        this.mDaoSession = daoSession;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mDaoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                mDaoSession.getGameDao().deleteAll();
                mDaoSession.getMatchDao().deleteAll();
                mDaoSession.getRobotDao().deleteAll();
                mDaoSession.getRobotEventDao().deleteAll();
                mDaoSession.getMatchDao().deleteAll();
                mDaoSession.getTeamDao().deleteAll();
                mDaoSession.getUserDao().deleteAll();
                mDaoSession.getMetricDao().deleteAll();
                mDaoSession.getPitDataDao().deleteAll();
                mDaoSession.getMatchDataDao().deleteAll();
                mDaoSession.getMatchCommentDao().deleteAll();
                mDaoSession.getRobotPhotoDao().deleteAll();
            }
        });
        return null;
    }
}
