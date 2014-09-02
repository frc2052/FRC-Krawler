package com.team2052.frckrawler.fragment.scout;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.RobotEvents;

import java.util.List;

/**
 * @author Adam
 */
public class ScoutTypeFragment extends Fragment{
    private Event mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        Long eventId = preferences.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, -1);
        if(eventId == -1){
            try {
                throw new Exception("Event Id Can't be -1 Try Resyncing");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mEvent = Event.load(Event.class, eventId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_scouting, null);
        return view;
    }

    public class GetAllRobots extends AsyncTask<Void, Void, List<RobotEvents>>{

        @Override
        protected List<RobotEvents> doInBackground(Void... params) {
            return new Select().from(RobotEvents.class).where("Event = ?", mEvent.getId()).execute();
        }

        @Override
        protected void onPostExecute(List<RobotEvents> robotEventses) {

        }
    }
}
