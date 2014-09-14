package com.team2052.frckrawler.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Alliance;
import com.team2052.frckrawler.database.models.Match;

/**
 * @author Adam
 * Costom view just for matches
 */
public class MatchView extends FrameLayout {
    private TextView matchTitle, red1, red2, red3, redScore, blue1, blue2, blue3, blueScore;
    private View redAlliance;
    private View blueAlliance;

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
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_match, this, true);
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

    public void init(Match match){
        Alliance alliance = match.alliance;
        matchTitle.setText(Integer.toString(match.matchNumber));
        red1.setText(Integer.toString(alliance.red1.number));
        red2.setText(Integer.toString(alliance.red2.number));
        red3.setText(Integer.toString(alliance.red3.number));
        blue1.setText(Integer.toString(alliance.blue1.number));
        blue2.setText(Integer.toString(alliance.blue2.number));
        blue3.setText(Integer.toString(alliance.blue3.number));
        blueScore.setText(Integer.toString(alliance.blueScore));
        redScore.setText(Integer.toString(alliance.redScore));
        if(alliance.redScore > alliance.blueScore){
            redAlliance.setBackgroundResource(R.drawable.alliance_border);
            blueAlliance.setBackgroundResource(R.drawable.no_border);
        } else if(alliance.blueScore > alliance.redScore) {
            blueAlliance.setBackgroundResource(R.drawable.alliance_border);
            redAlliance.setBackgroundResource(R.drawable.no_border);
        }
    }
}
