package com.team2052.frckrawler.client.background;

import android.os.AsyncTask;

import com.team2052.frckrawler.core.database.DBManager;

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
        mDaoSession.getDaoSession().runInTx(() -> {
            mDaoSession.getDaoSession().getGameDao().deleteAll();
            mDaoSession.getDaoSession().getMatchDao().deleteAll();
            mDaoSession.getDaoSession().getRobotDao().deleteAll();
            mDaoSession.getDaoSession().getRobotEventDao().deleteAll();
            mDaoSession.getDaoSession().getMatchDao().deleteAll();
            mDaoSession.getDaoSession().getTeamDao().deleteAll();
            mDaoSession.getDaoSession().getUserDao().deleteAll();
            mDaoSession.getDaoSession().getMetricDao().deleteAll();
            mDaoSession.getDaoSession().getPitDataDao().deleteAll();
            mDaoSession.getDaoSession().getMatchDataDao().deleteAll();
            mDaoSession.getDaoSession().getMatchCommentDao().deleteAll();
            mDaoSession.getDaoSession().getRobotPhotoDao().deleteAll();
        });
        return null;
    }
}
