package com.team2052.frckrawler.core.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.db.Team;

/**
 * @author Adam
 */
public class TeamListElement extends ListElement {
    private final Team team;
    private final JsonObject data;

    public TeamListElement(Team team) {
        super(Long.toString(team.getNumber()));
        this.team = team;
        this.data = JSON.getAsJsonObject(team.getData());
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_team, null);
        }
        ((TextView) convertView.findViewById(R.id.list_item_team_number)).setText(Long.toString(team.getNumber()));
        ((TextView) convertView.findViewById(R.id.list_item_team_name)).setText(team.getName());
        ((TextView) convertView.findViewById(R.id.list_item_team_location)).setText(data.get("location").getAsString());
        return convertView;
    }
}
