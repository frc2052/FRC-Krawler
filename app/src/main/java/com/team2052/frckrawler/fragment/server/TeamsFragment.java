package com.team2052.frckrawler.fragment.server;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.TeamListItem;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment {
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.addbutton, menu);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_metric_action) {
            /*Intent i = new Intent(getActivity(), AddTeamDialogActivity.class);
            startActivityForResult(i, 1);*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_teams, null);
        ((ListView) view.findViewById(R.id.teams_list_view)).setFastScrollAlwaysVisible(true);
        ((ListView) view.findViewById(R.id.teams_list_view)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                     /*String teamNumber = ((ListElement) adapterView.getAdapter().getItem(position)).getKey();
                     Intent i = new Intent(getActivity(), RobotsActivity.class);
                     i.putExtra(DatabaseActivity.PARENTS_EXTRA, new String[]{teamNumber});
                     i.putExtra(DatabaseActivity.DB_VALUES_EXTRA, new String[]{teamNumber});
                     i.putExtra(DatabaseActivity.DB_KEYS_EXTRA, new String[]{DBContract.COL_TEAM_NUMBER});
                     startActivity(i);*/
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
            AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(new ListViewAdapter(getActivity(), teamItems));
            adapter.setAbsListView(((ListView) getView().findViewById(R.id.teams_list_view)));
            ((ListView) getView().findViewById(R.id.teams_list_view)).setAdapter(adapter);
        }
    }
}
