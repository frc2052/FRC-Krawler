package com.team2052.frckrawler.fragment;

import android.os.*;
import android.util.Log;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.*;
import com.team2052.frckrawler.listitems.*;

import java.util.*;

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
    public void onCreate(Bundle savedInstanceState)
    {
        mEvent = Event.load(Event.class, getArguments().getLong(DatabaseActivity.PARENT_ID));
        super.onCreate(savedInstanceState);
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
            return new Select().from(Match.class).orderBy("MatchNumber ASC").where("Event = ?", mEvent.getId()).execute();
        }

        @Override
        protected void onPostExecute(List<Match> matches)
        {
            Log.i("FRCKrawler", "Update List");
            List<ListItem> listItems = new ArrayList<>();

            for (Match match : matches) {
                listItems.add(new MatchListItem(match));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), listItems));
        }
    }
}
