package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;

public class TeamsActivity extends TabActivity implements OnClickListener {
	
	private final Object ADD_TEAMS_TAG = new Object();
	private final Object TEAM_ROW_TAG = new Object();
	
	private Team[] teams;
	private DatabaseManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teams);
		
		Button addTeams = (Button)findViewById(R.id.addTeam);
		addTeams.setTag(ADD_TEAMS_TAG);
		addTeams.setOnClickListener(this);
		
		dbManager = DatabaseManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		teams = dbManager.getAllTeams();
		TableLayout table = (TableLayout)findViewById(R.id.teamsDataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		for(int i = 0; i < teams.length; i++) {
			
			int color = Color.TRANSPARENT;
			
			if(i % 2 != 0)
				color = Color.BLUE;
			
			MyTableRow row = new MyTableRow(this, new View[] {
					new MyButton(this, "Edit Team", this, teams[i].getNumber()),
					new MyTextView(this, Integer.toString(teams[i].getNumber()), 18),
					new MyTextView(this, teams[i].getName(), 18),
					new MyTextView(this, teams[i].getSchool(), 18),
					new MyTextView(this, teams[i].getCity(), 18),
					new MyTextView(this, teams[i].getRookieYear(), 18),
					new MyTextView(this, teams[i].getWebsite(), 18),
					new MyTextView(this, teams[i].getStatePostalCode(), 18),
					new MyTextView(this, teams[i].getColors(), 18)
			}, color);
			
			row.setTag(TEAM_ROW_TAG);
			
			table.addView(row);
		}
	}

	public void onClick(View v) {
		
		if(v.getTag() == ADD_TEAMS_TAG) {
			
			Intent i = new Intent(this, AddTeamDialogActivity.class);
			startActivity(i);
			
		} else {
			
			Intent i =  new Intent(this, EditTeamDialogActivity.class);
			i.putExtra(EditTeamDialogActivity.TEAM_NUMBER_EXTRA_KEY, ((Integer)v.getTag()).intValue());
			startActivity(i);
		}
	}
}
