package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Team;

/**
 * Created by Adam on 8/24/2014.
 */
public class TeamListItem extends ListElement {
    private final Team team;

    public TeamListItem(Team team) {
        super(Integer.toString(team.number));
        this.team = team;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_team, null);
        ((TextView) convertView.findViewById(R.id.list_item_team_number)).setText(Integer.toString(team.number));
        ((TextView) convertView.findViewById(R.id.list_item_team_name)).setText(team.name);
        ((TextView) convertView.findViewById(R.id.list_item_team_location)).setText(team.city + ", " + team.postalCode);
        return convertView;
    }
}
