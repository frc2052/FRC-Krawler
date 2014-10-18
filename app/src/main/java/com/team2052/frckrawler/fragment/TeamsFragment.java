package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.team2052.frckrawler.activity.TeamInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.TeamListElement;

import java.util.ArrayList;
import java.util.List;

import frckrawler.Team;
import frckrawler.TeamDao;

public class TeamsFragment extends ListFragment
{
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setShowAddAction(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Team team = mDaoSession.getTeamDao().load(Long.valueOf(((ListElement) parent.getAdapter().getItem(position)).getKey()));
                startActivity(TeamInfoActivity.newInstance(getActivity(), team));
            }
        });

        return view;
    }

    @Override
    public void updateList()
    {
        new GetTeamsTask().execute();
    }

    private class GetTeamsTask extends AsyncTask<Void, Void, List<Team>>
    {

        @Override
        protected List<Team> doInBackground(Void... params)
        {
            return mDaoSession.getTeamDao().queryBuilder().orderAsc(TeamDao.Properties.Number).list();
        }

        @Override
        protected void onPostExecute(List<Team> teams)
        {
            ArrayList<ListItem> teamItems = new ArrayList<>();
            for (Team team : teams) {
                teamItems.add(new TeamListElement(team));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), teamItems));
        }
    }
}
