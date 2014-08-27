package com.team2052.frckrawler.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.RobotsActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.listitems.ListElement;
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
        ((ListView)view.findViewById(R.id.teams_list_view)).setFastScrollAlwaysVisible(true);
        ((ListView)view.findViewById(R.id.teams_list_view)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String teamNumber = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
                Intent i = new Intent(getActivity(), RobotsActivity.class);
                i.putExtra(DatabaseActivity.PARENTS_EXTRA, new String[]{teamNumber});
                i.putExtra(DatabaseActivity.DB_VALUES_EXTRA, new String[]{teamNumber});
                i.putExtra(DatabaseActivity.DB_KEYS_EXTRA, new String[]{DBContract.COL_TEAM_NUMBER});
                startActivity(i);
            }
        });
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
