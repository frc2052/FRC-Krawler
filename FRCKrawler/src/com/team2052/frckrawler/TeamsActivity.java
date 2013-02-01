package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseContract;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;

public class TeamsActivity extends TabActivity implements OnClickListener {
	
	private static final int EDIT_BUTTON_ID = 1;
	private static final int ROBOTS_BUTTON_ID = 2;
	private static final int COMMENTS_BUTTON_ID = 3;
	private static final int CONTACTS_BUTTON_ID = 4;
	
	private final Object ADD_TEAMS_TAG = new Object();
	
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
			
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			MyButton editTeam = new MyButton(this, "Edit Team", this, 
					teams[i].getNumber());
			editTeam.setId(EDIT_BUTTON_ID);
			MyButton robots = new MyButton(this, "Robots", this, 
					teams[i].getNumber());
			robots.setId(ROBOTS_BUTTON_ID);
			MyButton contacts = new MyButton(this, "Contacts", this, 
					teams[i].getNumber());
			contacts.setId(CONTACTS_BUTTON_ID);
			MyButton comments = new MyButton(this, "Comments", this, 
					teams[i].getNumber());
			comments.setId(COMMENTS_BUTTON_ID);
			
			MyTableRow row = new MyTableRow(this, new View[] {
					editTeam,
					new MyTextView(this, Integer.toString(teams[i].getNumber()), 18),
					new MyTextView(this, teams[i].getName(), 18),
					new MyTextView(this, teams[i].getSchool(), 18),
					new MyTextView(this, teams[i].getCity(), 18),
					new MyTextView(this, teams[i].getRookieYear(), 18),
					new MyTextView(this, teams[i].getWebsite(), 18),
					new MyTextView(this, teams[i].getStatePostalCode(), 18),
					new MyTextView(this, teams[i].getColors(), 18),
					robots,
					contacts,
					comments
			}, color);
			
			row.setTag(Integer.valueOf(teams[i].getNumber()));
			
			table.addView(row);
		}
	}

	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addTeam:
			
				i = new Intent(this, AddTeamDialogActivity.class);
				startActivity(i);
			
				break;
			
			case EDIT_BUTTON_ID:
			
				i =  new Intent(this, EditTeamDialogActivity.class);
				i.putExtra(EditTeamDialogActivity.TEAM_NUMBER_EXTRA_KEY, 
						((Integer)v.getTag()).intValue());
				startActivity(i);
			
				break;
				
			case ROBOTS_BUTTON_ID:
				
				i = new Intent(this, RobotsActivity.class);
				i.putExtra(StackableTabActivity.PARENTS_EXTRA, 
						new String[] {((Integer)v.getTag()).toString()});
				i.putExtra(StackableTabActivity.PARENT_KEYS_EXTRA, 
						new String[] {DatabaseContract.COL_TEAM_NUMBER});
				startActivity(i);
				
				break;
				
			case CONTACTS_BUTTON_ID:
				
				i = new Intent(this, ContactsActivity.class);
				i.putExtra(StackableTabActivity.PARENTS_EXTRA, 
						new String[] {((Integer)v.getTag()).toString()});
				i.putExtra(StackableTabActivity.PARENT_KEYS_EXTRA, 
						new String[] {DatabaseContract.COL_TEAM_NUMBER});
				startActivity(i);
				
				break;
				
			case COMMENTS_BUTTON_ID:
				
				i = new Intent(this, CommentsActivity.class);
				i.putExtra(StackableTabActivity.PARENTS_EXTRA, 
						new String[] {((Integer)v.getTag()).toString()});
				i.putExtra(StackableTabActivity.PARENT_KEYS_EXTRA, 
						new String[] {DatabaseContract.COL_TEAM_NUMBER});
				startActivity(i);
				
				break;
		}
	}
}
