package com.team2052.frckrawler.fragment.server;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.TeamListItem;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_teams, null);
        ((ListView) view.findViewById(R.id.teams_list_view)).setFastScrollAlwaysVisible(true);
        ((ListView) view.findViewById(R.id.teams_list_view)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RobotsFragment fragment = RobotsFragment.newInstance((Team) new Select().from(Team.class).where("Number = ?", ((ListElement) adapterView.getAdapter().getItem(position)).getKey()).executeSingle());
                fragment.setRetainInstance(true);
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "mainFragment").commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetTeamsTask().execute();
    }

    private class GetTeamsTask extends AsyncTask<Void, Void, List<Team>> {

        @Override
        protected List<Team> doInBackground(Void... params) {
            return new Select().from(Team.class).orderBy("Number").execute();
        }

        @Override
        protected void onPostExecute(List<Team> teams) {
            ArrayList<ListItem> teamItems = new ArrayList<ListItem>();
            for (Team team : teams) {
                teamItems.add(new TeamListItem(team));
            }
            ((ListView) getView().findViewById(R.id.teams_list_view)).setAdapter(new ListViewAdapter(getActivity(), teamItems));
        }
    }
}
