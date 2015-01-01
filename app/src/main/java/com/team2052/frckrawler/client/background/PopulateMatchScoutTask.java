package com.team2052.frckrawler.client.background;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.core.fragments.ScoutMatchFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @since 12/28/2014.
 */
public class PopulateMatchScoutTask extends AsyncTask<Void, Void, Void> {
    private final DaoSession mDaoSession;
    private final Context context;
    private Event mEvent;
    private List<Match> mMatches;
    private ArrayList<String> matchNumbers;
    private ScoutMatchFragment mFragment;

    public PopulateMatchScoutTask(ScoutMatchFragment fragment, Event event) {
        mFragment = fragment;
        this.mDaoSession = ((FRCKrawler) fragment.getActivity().getApplicationContext()).getDaoSession();
        this.context = fragment.getActivity();
        this.mEvent = event;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mMatches = mDaoSession.getMatchDao().queryBuilder().orderAsc(MatchDao.Properties.Number).where(MatchDao.Properties.EventId.eq(mEvent.getId())).list();
        matchNumbers = new ArrayList<>();

        for (Match match : mMatches) {
            matchNumbers.add("Match #" + match.getNumber());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mFragment.mMatches = mMatches;
        mFragment.mMatchSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, matchNumbers));
        mFragment.setErrorVisible(false);
    }


}
