package com.team2052.frckrawler.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Match;

/**
 * @author Adam
 *         Costom view just for matches
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

    public void init(Match match) {
        matchTitle.setText("Match " + Integer.toString(match.getNumber()));
        red1.setText(Long.toString(match.getRed1Id()));
        red2.setText(Long.toString(match.getRed2Id()));
        red3.setText(Long.toString(match.getRed3Id()));
        blue1.setText(Long.toString(match.getBlue1Id()));
        blue2.setText(Long.toString(match.getBlue2Id()));
        blue3.setText(Long.toString(match.getBlue1Id()));
        //Matches that aren't played yet have a score of -1
        if (match.getRedscore() < 0)
            redScore.setText("?");
        else
            redScore.setText(Integer.toString(match.getRedscore()));


        if (match.getBluescore() < 0)
            blueScore.setText("?");
        else
            blueScore.setText(Integer.toString(match.getBluescore()));


        if (match.getRedscore() > match.getBluescore()) {
            redAlliance.setBackgroundResource(R.drawable.alliance_border);
            blueAlliance.setBackgroundResource(R.drawable.no_border);
        } else if (match.getBluescore() > match.getRedscore()) {
            blueAlliance.setBackgroundResource(R.drawable.alliance_border);
            redAlliance.setBackgroundResource(R.drawable.no_border);
        } else {
            redAlliance.setBackgroundResource(R.drawable.no_border);
            blueAlliance.setBackgroundResource(R.drawable.no_border);
        }
    }
}
