package com.team2052.frckrawler.core.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.team2052.frckrawler.core.activities.TeamInfoActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.TeamListElement;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.TeamDao;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Team team = mDaoSession.getTeamDao().load(Long.valueOf((((ListElement) ((ListViewAdapter) mAdapter).getItem(position))).getKey()));
                startActivity(TeamInfoActivity.newInstance(getActivity(), team));
            }
        });

        return view;
    }


    @Override
    public void updateList() {
        new GetTeamsTask().execute();
    }

    private class GetTeamsTask extends AsyncTask<Void, Void, List<Team>> {

        @Override
        protected List<Team> doInBackground(Void... params) {
            return mDaoSession.getTeamDao().queryBuilder().orderAsc(TeamDao.Properties.Number).list();
        }

        @Override
        protected void onPostExecute(List<Team> teams) {
            if (teams.size() == 0) {
                showError(true);
                return;
            }
            showError(false);

            ArrayList<ListItem> teamListElements = new ArrayList<>();
            for (Team team : teams) {
                teamListElements.add(new TeamListElement(team));
            }

            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), teamListElements));
        }
    }
}
