package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.TeamListItem;

import java.util.ArrayList;

public class TeamsFragment extends Fragment {
    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = DBManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_teams, null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetTeamsTask().execute();
    }

    private class GetTeamsTask extends AsyncTask<Void, Void, Team[]> {

        @Override
        protected Team[] doInBackground(Void... params) {
            return dbManager.getAllTeams();
        }

        @Override
        protected void onPostExecute(Team[] teams) {
            ArrayList<ListItem> teamItems = new ArrayList<ListItem>();
            for (Team team : teams) {
                teamItems.add(new TeamListItem(team));
            }
            ((ListView) getView().findViewById(R.id.teams_list_view)).setAdapter(new ListViewAdapter(getActivity(), teamItems));
        }
    }
}
