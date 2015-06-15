package com.team2052.frckrawler.fragments.team;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.TeamDao;
import com.team2052.frckrawler.fragments.ListFragment;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.TeamListElement;

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
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            Team team = mDbManager.mTeams.load(Long.valueOf((((ListElement) ((ListViewAdapter) mAdapter).getItem(position))).getKey()));
            startActivity(TeamInfoActivity.newInstance(getActivity(), team));
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
            return mDbManager.mTeams.getQueryBuilder().orderAsc(TeamDao.Properties.Number).list();
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
