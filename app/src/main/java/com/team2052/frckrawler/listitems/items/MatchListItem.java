package com.team2052.frckrawler.listitems.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.views.MatchView;

/**
 * @author Adam
 */
public class MatchListItem implements ListItem {
    private final Match match;
    private final boolean showScores;

    public MatchListItem(Match match) {
        this(match, false);
    }

    public MatchListItem(Match match, boolean showScores) {
        this.match = match;
        this.showScores = showScores;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null || !(convertView instanceof MatchView)) {
            convertView = inflater.inflate(R.layout.list_view_match, null);
        }

        ((MatchView) convertView.findViewById(R.id.match)).init(match, showScores);
        return convertView;
    }
}
