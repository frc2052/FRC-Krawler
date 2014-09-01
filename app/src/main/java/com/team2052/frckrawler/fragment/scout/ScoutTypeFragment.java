package com.team2052.frckrawler.fragment.scout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Robot;

import java.util.List;

/**
 * @author Adam
 */
public class ScoutTypeFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_scouting, null);
        return view;
    }

    public class GetAllRobots extends AsyncTask<Void, Void, List<Robot>>{

        @Override
        protected List<Robot> doInBackground(Void... params) {
            return new Select().from(Robot.class).execute();
        }
    }
}
