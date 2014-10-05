package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.listitems.ListElement;

/**
 * @author Adam
 */
public class TeamListElement extends ListElement
{
    private final Team team;

    public TeamListElement(Team team)
    {
        super(Integer.toString(team.number));
        this.team = team;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView)
    {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_team, null);
        }
        ((TextView) convertView.findViewById(R.id.list_item_team_number)).setText(Integer.toString(team.number));
        ((TextView) convertView.findViewById(R.id.list_item_team_name)).setText(team.name);
        ((TextView) convertView.findViewById(R.id.list_item_team_location)).setText(team.location);
        return convertView;
    }
}