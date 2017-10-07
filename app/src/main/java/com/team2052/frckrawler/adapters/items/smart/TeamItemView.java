package com.team2052.frckrawler.adapters.items.smart;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.models.Team;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public class TeamItemView extends BindableFrameLayout<Team> {
    @BindView(R.id.list_item_team_number)
    TextView mTeamNumber;

    @BindView(R.id.list_item_team_name)
    TextView mTeamName;

  /*  @BindView(R.id.list_item_team_location)
    TextView mTeamLocation;*/

    public TeamItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_team;
    }

    @Override
    public void bind(Team team) {
        setOnClickListener(v -> notifyItemAction(SmartAdapterInteractions.EVENT_CLICKED));
        this.setFocusable(true);
        this.setClickable(true);

        JsonObject data = JSON.getAsJsonObject(team.getData());

        mTeamNumber.setText(Long.toString(team.getNumber()));
        mTeamName.setText(team.getName());
        /*if (data.has("location")) {
            mTeamLocation.setText(data.get("location").getAsString());
        } else {
            mTeamLocation.setText("Unknown");
        }*/
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
