package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.RobotEvents;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.SimpleListElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Adam on 10/3/2014.
 */
public class RobotsActivity extends ListActivity
{

    private Event mEvent;

    public static Intent newInstance(Context context, Event event)
    {
        Intent intent = new Intent(context, RobotsActivity.class);
        intent.putExtra(PARENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mEvent = Event.load(Event.class, getIntent().getLongExtra(PARENT_ID, 0));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void updateList()
    {
        new GetRobots().execute();
    }

    public class GetRobots extends AsyncTask<Void, Void, List<RobotEvents>>
    {

        @Override
        protected List<RobotEvents> doInBackground(Void... voids)
        {
            return new Select().from(RobotEvents.class).where("Event = ?", mEvent.getId()).execute();
        }

        @Override
        protected void onPostExecute(List<RobotEvents> robotEventses)
        {
            Collections.sort(robotEventses, new Comparator<RobotEvents>()
            {
                @Override
                public int compare(RobotEvents robotEvents, RobotEvents robotEvents2)
                {
                    return Double.compare(robotEvents.robot.team.number, robotEvents2.robot.team.number);
                }
            });

            List<ListItem> listItems = new ArrayList<>();
            for (RobotEvents robotEvent : robotEventses) {
                listItems.add(new SimpleListElement(Integer.toString(robotEvent.robot.team.number), Long.toString(robotEvent.robot.getId())));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(RobotsActivity.this, listItems));
        }
    }
}
