package com.team2052.frckrawler;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.fa.readers.EventReader;
import com.team2052.frckrawler.fa.readers.TeamReader;
import com.team2052.frckrawler.fa.types.FAEvent;
import com.team2052.frckrawler.fa.types.FATeam;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class ImportDialogActivity extends Activity implements OnClickListener {
	
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";
	
	private DBManager db;
	private Event frckrawlerEvent;
	private FAEvent[] faEvents;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_import);
		
		findViewById(R.id.importTeamsAndRobots).setOnClickListener(this);
		findViewById(R.id.importOPR).setOnClickListener(this);
		findViewById(R.id.cancelImport).setOnClickListener(this);
		
		db = DBManager.getInstance(this);
		frckrawlerEvent = db.getEventsByColumns(
				new String[] {DBContract.COL_EVENT_ID}, 
				new String[] {Integer.toString(getIntent().getIntExtra(EVENT_ID_EXTRA, -1))})[0];
		new ImportFAEventsTask().execute();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.importTeamsAndRobots:
				int pos = ((Spinner)findViewById(R.id.eventsSpinner)).getSelectedItemPosition();
				new ImportTeamsAndRobotsTask().execute(faEvents[pos]);
				break;
				
			case R.id.importOPR:
				break;
				
			case R.id.cancelImport:
				finish();
				break;
		}
	}
	
	private class ImportFAEventsTask extends AsyncTask<Void, Void, FAEvent[]> {
		
		private AlertDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
			builder.setTitle("Connecting...");
			builder.setView(new ProgressSpinner(ImportDialogActivity.this));
			builder.setCancelable(false);
			progressDialog = builder.create();
			progressDialog.show();
		}
		
		@Override
		protected FAEvent[] doInBackground(Void... params) {
			try {
				return new EventReader().readEvents();
			} catch(IOException e) {
				progressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
				builder.setTitle("Connection error!");
				builder.setMessage("There was an in loading the list of events from " +
						"this Internet:\n" + e.getMessage());
				builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
			
			return new FAEvent[0];
		}
		
		@Override
		protected void onPostExecute(FAEvent[] e) {
			faEvents = e;
			ArrayAdapter<FAEvent> adapter = new ArrayAdapter<FAEvent>(ImportDialogActivity.this, 
					android.R.layout.simple_spinner_item, faEvents);
			((Spinner)findViewById(R.id.eventsSpinner)).setAdapter(adapter);
			progressDialog.dismiss();
		}
	}
	
	
	private class ImportTeamsAndRobotsTask extends AsyncTask<FAEvent, Void, Void> {
		
		private AlertDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
			builder.setTitle("Downloading...");
			builder.setView(new ProgressSpinner(ImportDialogActivity.this));
			builder.setCancelable(false);
			progressDialog = builder.create();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(FAEvent... e) {
			
			try {
				//Get a list of teams from FIRST Alliance
				FATeam[] faTeams = new TeamReader(e[0].getID()).readTeams();
				
				//Remove old robots and their match data from the event
				Robot[] oldRobots = db.getRobotsAtEvent(frckrawlerEvent.getEventID());
				for(Robot r : oldRobots) {
					boolean isAtEvent = false;
					for(FATeam t : faTeams) {
						if(r.getTeamNumber() == Integer.parseInt(t.getNumber())) {
							isAtEvent = true;
							break;
						}
					}
					
					if(!isAtEvent) {
						db.removeMatchDataByColumns(
								new String[] {DBContract.COL_EVENT_ID, DBContract.COL_ROBOT_ID}, 
								new String[] {Integer.toString(frckrawlerEvent.getEventID()), 
										Integer.toString(r.getID())});
						db.removeRobotFromEvent(frckrawlerEvent.getEventID(), r.getID());
					}
				}
				
				//Create teams that don't exist yet and give them robots
				for(FATeam t : faTeams) {
					db.addTeam(Integer.parseInt(t.getNumber()), t.getName(), 
							null, null, -1, null, "MN", null);
					
					if(db.getRobotsByColumns(
							new String[] {DBContract.COL_TEAM_NUMBER, DBContract.COL_GAME_NAME}, 
							new String[] {t.getNumber(), frckrawlerEvent.getGameName()})
							.length < 1) {
						db.addRobot(Integer.parseInt(t.getNumber()),
								frckrawlerEvent.getGameName(), 
								"", 
								"",
								new MetricValue[0]);
					}
					
					Robot r = db.getRobotsByColumns(
							new String[] {DBContract.COL_TEAM_NUMBER, DBContract.COL_GAME_NAME}, 
							new String[] {t.getNumber(), frckrawlerEvent.getGameName()})[0];
					db.addRobotToEvent(frckrawlerEvent.getEventID(), r.getID());
				}
			} catch (IOException e1) {
				Log.e("FRCKrawler", e1.getMessage());
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			progressDialog.dismiss();
		}
		
	}
}
