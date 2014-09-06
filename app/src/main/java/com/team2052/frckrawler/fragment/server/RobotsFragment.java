package com.team2052.frckrawler.fragment.server;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.team2052.frckrawler.activity.NewDatabaseActivity;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Team;

/**
 * @author Adam
 */
public class RobotsFragment extends Fragment {
    public static final String VIEW_TYPE = "VIEW_TYPE";
    private int mViewType;

    //To create a valid instance view by team or by game
    public static RobotsFragment newInstance(Team team) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 0);
        b.putLong(NewDatabaseActivity.PARENT_ID, team.getId());
        fragment.setArguments(b);
        return fragment;
    }

    public static RobotsFragment newInstance(Game game) {
        RobotsFragment fragment = new RobotsFragment();
        Bundle b = new Bundle();
        b.putInt(VIEW_TYPE, 1);
        b.putLong(NewDatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if(b != null){
            switch (mViewType = getArguments().getInt(VIEW_TYPE, 0)){
                case 0:
                case 1:
            }
        }
    }


}
