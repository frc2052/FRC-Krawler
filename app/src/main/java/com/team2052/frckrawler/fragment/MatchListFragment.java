package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.items.MatchListItem;

import java.util.ArrayList;
import java.util.List;

import frckrawler.Event;
import frckrawler.Match;
import frckrawler.MatchDao;

/**
 * @author Adam
 */
public class MatchListFragment extends ListFragment
{

    private Event mEvent;

    public static MatchListFragment newInstance(Event event)
    {
        MatchListFragment fragment = new MatchListFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onPostCreate()
    {
        mEvent = mDaoSession.getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
    }

    @Override
    public void updateList()
    {
        new GetMatches().execute();
    }

    public class GetMatches extends AsyncTask<Void, Void, List<Match>>
    {

        @Override
        protected List<Match> doInBackground(Void... params)
        {
            //Get Matches ascending from the provided event id
            return mDaoSession.getMatchDao().queryBuilder().orderAsc(MatchDao.Properties.Number).where(MatchDao.Properties.EventId.eq(mEvent.getId())).list();
        }

        @Override
        protected void onPostExecute(List<Match> matches)
        {
            List<ListItem> listItems = new ArrayList<>();

            for (Match match : matches) {
                listItems.add(new MatchListItem(match));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), listItems));
        }
    }
}
