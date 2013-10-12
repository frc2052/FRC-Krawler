package com.team2052.frckrawler;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
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
import com.team2052.frckrawler.fa.readers.OPRReader;
import com.team2052.frckrawler.fa.readers.TeamReader;
import com.team2052.frckrawler.fa.types.FAEvent;
import com.team2052.frckrawler.fa.types.FAOPR;
import com.team2052.frckrawler.fa.types.FATeam;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class ImportDialogActivity extends Activity implements OnClickListener {
	
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";
	
	private DBManager db;
	private Event frckrawlerEvent;
	private FAEvent[] faEvents;
	private AlertDialog tempDownloadProg;
	
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
		tempDownloadProg = null;
		new ImportFAEventsTask().execute();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(tempDownloadProg != null)
			tempDownloadProg.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.importTeamsAndRobots:
				final int pos = ((Spinner)findViewById(R.id.eventsSpinner))
										.getSelectedItemPosition();
				AlertDialog.Builder trBuilder = new AlertDialog.Builder(this);
				trBuilder.setTitle("Are you sure?");
				trBuilder.setMessage("Are you sure you want to import the teams and " +
						"robots from " + faEvents[pos].getName() + 
						"? Any robot not on the imported list will be removed from " +
						frckrawlerEvent.getEventName() + " and lose all match data for " +
						"this event");
				trBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				trBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ImportTeamsAndRobotsTask().execute(faEvents[pos]);
					}
				});
				trBuilder.show();
				break;
				
			case R.id.importOPR:
				int spinnerPos = ((Spinner)findViewById(R.id.eventsSpinner))
										.getSelectedItemPosition();
				final FAEvent selectedFAEvent = faEvents[spinnerPos];
				AlertDialog.Builder oprBuilder = new AlertDialog.Builder(this);
				oprBuilder.setTitle("Import OPR");
				oprBuilder.setMessage("Would like to import OPR based on matches played over " +
						"the entire season, or matches only played at " + 
						selectedFAEvent.getName() + "? Note: this will only import OPRs for " +
								"teams at " + frckrawlerEvent.getEventName());
				oprBuilder.setPositiveButton("Entire Season", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ImportOPRTask(selectedFAEvent, false).execute();
					}
				});
				oprBuilder.setNeutralButton("Only " + selectedFAEvent.getName(), 
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ImportOPRTask(selectedFAEvent, true).execute();
					}
				});
				oprBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				oprBuilder.show();
				break;
				
			case R.id.cancelImport:
				finish();
				break;
		}
	}
	
	private void lockScreenOrientation() {
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	private void releaseScreenOrientation() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	
	private class ImportFAEventsTask extends AsyncTask<Void, Void, FAEvent[]> {
		
		private AlertDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			lockScreenOrientation();
			AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
			builder.setTitle("Connecting...");
			builder.setView(new ProgressSpinner(ImportDialogActivity.this));
			builder.setCancelable(false);
			progressDialog = tempDownloadProg = builder.create();
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
						"the Internet:\n" + e.getMessage());
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
			if(e == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
				builder.setTitle("Redirect Error");
				builder.setMessage("The request has been redirected. You probably need to " +
						"sign in to the network on your browser.");
				builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
				builder.show();
			} else {
				faEvents = e;
				ArrayAdapter<FAEvent> adapter = new ArrayAdapter<FAEvent>(ImportDialogActivity.this, 
						android.R.layout.simple_spinner_item, faEvents);
				((Spinner)findViewById(R.id.eventsSpinner)).setAdapter(adapter);
				progressDialog.dismiss();
				releaseScreenOrientation();
			}
		}
	}
	
	private class ImportTeamsAndRobotsTask extends AsyncTask<FAEvent, Void, Void> {
		
		private AlertDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			lockScreenOrientation();
			AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
			builder.setTitle("Downloading...");
			builder.setView(new ProgressSpinner(ImportDialogActivity.this));
			builder.setCancelable(false);
			progressDialog = tempDownloadProg = builder.create();
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
					
					//Add the new robot to the FRCKrawler Event
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
			releaseScreenOrientation();
		}
	}
	
	private class ImportOPRTask extends AsyncTask<Void, Void, Void> {
		
		private boolean byEvent;
		private FAEvent event;
		private AlertDialog progressDialog;
		
		public ImportOPRTask(FAEvent _event, boolean _byEvent) {
			event = _event;
			byEvent = _byEvent;
		}
		
		@Override
		protected void onPreExecute() {
			lockScreenOrientation();
			AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
			builder.setTitle("Downloading...");
			builder.setView(new ProgressSpinner(ImportDialogActivity.this));
			builder.setCancelable(false);
			progressDialog = tempDownloadProg = builder.create();
			progressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... v) {
			Robot[] robots = db.getRobotsAtEvent(frckrawlerEvent.getEventID());
			OPRReader reader = new OPRReader();
			
			for(int i = 0; i < robots.length; i++) {
				FAOPR opr;
				try {
					opr = reader.readOPR(
							robots[i].getTeamNumber(), 
							event.getCode(), 
							byEvent);
					Log.d("FRCKrawler", opr.toString());
				} catch (IOException e) {
					opr = new FAOPR(robots[i].getTeamNumber(), -1);
					e.printStackTrace();
				}
				
				db.updateRobots(
						new String[] {DBContract.COL_ROBOT_ID}, 
						new String[] {Integer.toString(robots[i].getID())}, 
						new String[] {DBContract.COL_OPR}, 
						new String[] {Double.toString(opr.getOPR())});
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			progressDialog.dismiss();
			releaseScreenOrientation();
		}
	}
}
