package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.db.Team;

/**
 * @author Adam
 * @since 10/31/2014
 */
public class TeamInfoFragment extends BaseFragment {
    private Team team;

    public static TeamInfoFragment newInstance(Team team) {
        TeamInfoFragment fragment = new TeamInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BaseActivity.PARENT_ID, team.getNumber());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.team = mDbManager.getTeamsTable().load(getArguments().getLong(BaseActivity.PARENT_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_info, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*binding.teamInfoName.setText(team.getName());
        JsonObject data = JSON.getAsJsonObject(team.getData());

        if (data.has("rookie_year") && !data.get("rookie_year").isJsonNull()) {
            binding.teamInfoRookieYear.setText("First started in " + data.get("rookie_year").getAsString());
        } else {
            binding.teamInfoRookieYear.setVisibility(View.GONE);
        }

        if (data.has("long_name") && !data.get("long_name").isJsonNull()) {
            binding.teamInfoLongName.setText(data.get("long_name").getAsString());
        } else {
            binding.teamInfoLongName.setVisibility(View.GONE);
        }

        if (data.has("website") && !data.get("website").isJsonNull()) {
            binding.teamInfoWebsite.setText(data.get("website").getAsString());
        } else {
            binding.teamInfoWebsite.setVisibility(View.GONE);
        }*/
    }
}
