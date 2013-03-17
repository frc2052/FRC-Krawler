package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class TeamsActivity extends TabActivity implements OnClickListener {
	
	private static final int EDIT_BUTTON_ID = 1;
	private static final int ROBOTS_BUTTON_ID = 2;
	private static final int COMMENTS_BUTTON_ID = 3;
	private static final int CONTACTS_BUTTON_ID = 4;
	
	private final Object ADD_TEAMS_TAG = new Object();
	
	private Team[] teams;
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teams);
		
		Button addTeams = (Button)findViewById(R.id.addTeam);
		addTeams.setTag(ADD_TEAMS_TAG);
		addTeams.setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		((FrameLayout)findViewById(R.id.progressFrame)).
		addView(new ProgressSpinner(getApplicationContext()));
		
		TableLayout table = (TableLayout)findViewById(R.id.teamsDataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		new GetTeamsTask().execute(this);
	}
	
	public void postResults(MyTableRow[] teams) {

		TableLayout table = (TableLayout)findViewById(R.id.teamsDataTable);
		
		for(int i = 0; i < teams.length; i++) {
			
			table.addView(teams[i]);
		}
		
		((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
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
						new String[] {v.getTag().toString()});
				i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, 
						new String[] {v.getTag().toString()});
				i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, 
						new String[] {DBContract.COL_TEAM_NUMBER});
				startActivity(i);
				
				break;
				
			case CONTACTS_BUTTON_ID:
				
				i = new Intent(this, ContactsActivity.class);
				i.putExtra(StackableTabActivity.PARENTS_EXTRA, 
						new String[] {v.getTag().toString()});
				i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, 
						new String[] {v.getTag().toString()});
				i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, 
						new String[] {DBContract.COL_TEAM_NUMBER});
				startActivity(i);
				
				break;
				
			case COMMENTS_BUTTON_ID:
				
				/*i = new Intent(this, CommentsActivity.class);
				i.putExtra(StackableTabActivity.PARENTS_EXTRA, 
						new String[] {v.getTag().toString()});
				i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, 
						new String[] {v.getTag().toString()});
				i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, 
						new String[] {DBContract.COL_TEAM_NUMBER});
				startActivity(i);
				
				break;*/
		}
	}
	
	private class GetTeamsTask extends AsyncTask<TeamsActivity, Void, MyTableRow[]> {
		
		protected MyTableRow[] doInBackground(TeamsActivity... activities) {
			
			TeamsActivity activity = activities[0];
			
			Team[] teams = dbManager.getAllTeams();
			MyTableRow[] rows = new MyTableRow[teams.length];
			
			for(int i = 0; i < teams.length; i++) {
				
				int color;
				
				if(i % 2 == 0)
					color = Color.BLUE;
				else
					color = Color.TRANSPARENT;
				
				MyButton editTeam = new MyButton(activity, "Edit Team", activity, 
						teams[i].getNumber());
				editTeam.setId(EDIT_BUTTON_ID);
				MyButton robots = new MyButton(activity, "Robots", activity, 
						teams[i].getNumber());
				robots.setId(ROBOTS_BUTTON_ID);
				MyButton contacts = new MyButton(activity, "Contacts", activity, 
						teams[i].getNumber());
				contacts.setId(CONTACTS_BUTTON_ID);
				/*MyButton comments = new MyButton(listener, "Comments", listener, 
						teams[i].getNumber());
				comments.setId(COMMENTS_BUTTON_ID);*/
				
				String rookieYearText = "";
				
				if(teams[i].getRookieYear() > 0)
					rookieYearText = Integer.toString(teams[i].getRookieYear());
				
				MyTableRow row = new MyTableRow(activity, new View[] {
						editTeam,
						new MyTextView(activity, Integer.toString(teams[i].getNumber()), 18),
						new MyTextView(activity, teams[i].getName(), 18),
						new MyTextView(activity, teams[i].getSchool(), 18),
						new MyTextView(activity, teams[i].getCity(), 18),
						new MyTextView(activity, rookieYearText, 18),
						new MyTextView(activity, teams[i].getWebsite(), 18),
						new MyTextView(activity, teams[i].getStatePostalCode(), 18),
						new MyTextView(activity, teams[i].getColors(), 18),
						robots,
						contacts,
						//comments
				}, color);
				
				row.setTag(Integer.valueOf(teams[i].getNumber()));
				
				rows[i] = row;
			}
			
			return rows;
		}
		
		protected void onPostExecute(MyTableRow[] rows) {
			
			postResults(rows);
		}
	}
}
