package com.team2052.frckrawler.adapters.items.smart;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.models.Match;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;


public class MatchItemView extends BindableFrameLayout<Match> {
    @BindView(R.id.match_title)
    TextView mTitle;

    @BindViews({R.id.red1, R.id.red2, R.id.red3, R.id.red_score})
    List<TextView> mRedTeams;

    @BindViews({R.id.blue1, R.id.blue2, R.id.blue3, R.id.blue_score})
    List<TextView> mBLueTeams;

    @BindViews({R.id.red_alliance, R.id.blue_alliance})
    List<LinearLayout> mAllianceBorders;

    public MatchItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.match_view;
    }

    @Override
    public void bind(Match match) {
        mTitle.setText(String.format("#%s", Integer.toString(match.getMatch_number())));
        JsonObject alliances = JSON.getAsJsonObject(match.getData()).get("alliances").getAsJsonObject();
        JsonObject red = alliances.get("red").getAsJsonObject();
        JsonObject blue = alliances.get("blue").getAsJsonObject();

        JsonArray red_teams = red.get("team_keys").getAsJsonArray();
        mRedTeams.get(0).setText(red_teams.get(0).getAsString().substring(3));
        mRedTeams.get(1).setText(red_teams.get(1).getAsString().substring(3));
        mRedTeams.get(2).setText(red_teams.get(2).getAsString().substring(3));

        JsonArray blue_teams = blue.get("team_keys").getAsJsonArray();
        mBLueTeams.get(0).setText(blue_teams.get(0).getAsString().substring(3));
        mBLueTeams.get(1).setText(blue_teams.get(1).getAsString().substring(3));
        mBLueTeams.get(2).setText(blue_teams.get(2).getAsString().substring(3));

        int red_score = red.get("score").getAsInt();
        if (red_score < 0)
            mRedTeams.get(3).setText("?");
        else
            mRedTeams.get(3).setText(Integer.toString(red_score));
        int blue_score = blue.get("score").getAsInt();
        if (blue_score < 0)
            mBLueTeams.get(3).setText("?");
        else
            mBLueTeams.get(3).setText(Integer.toString(blue_score));

        if (red_score > blue_score) {
            mAllianceBorders.get(0).setBackgroundResource(R.drawable.alliance_border_top);
            mAllianceBorders.get(1).setBackgroundResource(R.drawable.no_border);
        } else if (blue_score > red_score) {
            mAllianceBorders.get(1).setBackgroundResource(R.drawable.alliance_border_bottom);
            mAllianceBorders.get(0).setBackgroundResource(R.drawable.no_border);
        } else {
            mAllianceBorders.get(0).setBackgroundResource(R.drawable.no_border);
            mAllianceBorders.get(1).setBackgroundResource(R.drawable.no_border);
        }
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
