package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.RobotActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.SimpleListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class RobotsFragment extends ListFragment
{
    public static final String VIEW_TYPE = "VIEW_TYPE";
    private int mViewType;
    private long mKey;

    //To create a valid instance view by team or by game
    public static RobotsFragment newInstance(Team team)
    {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 0);
        b.putLong(DatabaseActivity.PARENT_ID, team.getId());
        fragment.setArguments(b);
        return fragment;
    }

    public static RobotsFragment newInstance(Event event)
    {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 1);
        b.putLong(DatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        this.mViewType = b.getInt(VIEW_TYPE, 0);
        mKey = b.getLong(DatabaseActivity.PARENT_ID);
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
                getActivity().startActivity(RobotActivity.newInstance(getActivity(), Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey())));
            }
        });
        return view;
    }

    @Override
    public void updateList()
    {
        new GetAllRobotsBy().execute();
    }

    public class GetAllRobotsBy extends AsyncTask<Void, Void, List<Robot>>
    {

        @Override
        protected List<Robot> doInBackground(Void... params)
        {
            //Load robots based on the view type
            return new Select().from(Robot.class).where(mViewType == 0 ? "Team = ?" : "Event = ?", mKey).execute();
        }

        @Override
        protected void onPostExecute(List<Robot> robots)
        {
            List<ListItem> listItems = new ArrayList<>();
            for (Robot robot : robots) {
                //TODO List Item
                listItems.add(new SimpleListElement(robot.team.toString() + " - " + robot.game.name, Long.toString(robot.getId())));
            }
            mListView.setAdapter(new ListViewAdapter(getActivity(), listItems));
        }
    }
}
