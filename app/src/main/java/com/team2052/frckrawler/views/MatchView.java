package com.team2052.frckrawler.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.tba.JSON;

/**
 * @author Adam Custom view just for matches
 */
public class MatchView extends FrameLayout {
    private TextView matchTitle, red1, red2, red3, redScore, blue1, blue2, blue3, blueScore;
    private View redAlliance, blueAlliance;

    public MatchView(Context context) {
        super(context);
        init();
    }

    public MatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.match_view, this, true);
        matchTitle = (TextView) findViewById(R.id.match_title);
        red1 = (TextView) findViewById(R.id.red1);
        red2 = (TextView) findViewById(R.id.red2);
        red3 = (TextView) findViewById(R.id.red3);
        blue1 = (TextView) findViewById(R.id.blue1);
        blue2 = (TextView) findViewById(R.id.blue2);
        blue3 = (TextView) findViewById(R.id.blue3);
        blueScore = (TextView) findViewById(R.id.blue_score);
        redScore = (TextView) findViewById(R.id.red_score);
        redAlliance = findViewById(R.id.red_alliance);
        blueAlliance = findViewById(R.id.blue_alliance);
    }

    public void init(Match match, boolean showScores) {
        matchTitle.setText("Match " + Integer.toString(match.getMatch_number()));
        JsonObject alliances = JSON.getAsJsonObject(match.getData()).get("alliances").getAsJsonObject();
        JsonObject red = alliances.get("red").getAsJsonObject();
        JsonObject blue = alliances.get("blue").getAsJsonObject();

        JsonArray red_teams = red.get("teams").getAsJsonArray();
        red1.setText(red_teams.get(0).getAsString().substring(3));
        red2.setText(red_teams.get(1).getAsString().substring(3));
        red3.setText(red_teams.get(2).getAsString().substring(3));

        JsonArray blue_teams = blue.get("teams").getAsJsonArray();
        blue3.setText(blue_teams.get(0).getAsString().substring(3));
        blue1.setText(blue_teams.get(1).getAsString().substring(3));
        blue2.setText(blue_teams.get(2).getAsString().substring(3));

        int red_score = red.get("score").getAsInt();
        //Matches that aren't played yet have a score of -1
        if (red_score < 0)
            redScore.setText("?");
        else
            redScore.setText(Integer.toString(red_score));

        int blue_score = blue.get("score").getAsInt();
        if (blue_score < 0)
            blueScore.setText("?");
        else
            blueScore.setText(Integer.toString(blue_score));

        if (!showScores) {
            blueScore.setVisibility(GONE);
            redScore.setVisibility(GONE);
        } else {
            if (red_score > blue_score) {
                redAlliance.setBackgroundResource(R.drawable.alliance_border);
                blueAlliance.setBackgroundResource(R.drawable.no_border);
            } else if (blue_score > red_score) {
                blueAlliance.setBackgroundResource(R.drawable.alliance_border);
                redAlliance.setBackgroundResource(R.drawable.no_border);
            } else {
                redAlliance.setBackgroundResource(R.drawable.no_border);
                blueAlliance.setBackgroundResource(R.drawable.no_border);
            }
        }
    }
}
