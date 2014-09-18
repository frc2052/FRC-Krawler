package com.team2052.frckrawler.fragment.server;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.NewDatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.SimpleListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class RobotsFragment extends Fragment implements ListUpdateListener {
    public static final String VIEW_TYPE = "VIEW_TYPE";
    private int mViewType;
    private Team mTeam;
    private Game mEvent;
    private ListView mListView;
    private long mKey;

    //To create a valid instance view by team or by game
    public static RobotsFragment newInstance(Team team) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 0);
        b.putLong(NewDatabaseActivity.PARENT_ID, team.getId());
        fragment.setArguments(b);
        return fragment;
    }

    public static RobotsFragment newInstance(Event event) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 1);
        b.putLong(NewDatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        this.mViewType = b.getInt(VIEW_TYPE, 0);
        mKey = b.getLong(NewDatabaseActivity.PARENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, null);
        mListView = (ListView) view.findViewById(R.id.list_layout);
        updateList();
        return view;
    }

    @Override
    public void updateList() {
        new GetAllRobotsBy().execute();
    }

    public class GetAllRobotsBy extends AsyncTask<Void, Void, List<Robot>> {

        @Override
        protected List<Robot> doInBackground(Void... params) {
            return new Select().from(Robot.class).where(mViewType == 0 ? "Team = ?" : "Event = ?", mKey).execute();
        }

        @Override
        protected void onPostExecute(List<Robot> robots) {
            List<ListItem> listItems = new ArrayList<ListItem>();
            for (Robot robot : robots) {
                listItems.add(new SimpleListElement(robot.team.toString(), robot.team.toString()));
            }
            mListView.setAdapter(new ListViewAdapter(getActivity(), listItems));
        }
    }
}
