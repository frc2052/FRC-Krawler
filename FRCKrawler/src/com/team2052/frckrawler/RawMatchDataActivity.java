package com.team2052.frckrawler;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class RawMatchDataActivity extends StackableTabActivity implements OnClickListener, 
																			android.content.DialogInterface.OnClickListener{
	
	public static final String LIMIT_LOADING_EXTRA = "com.team2052.frckrawler.limitLoading";
	public static final String DISABLE_BUTTONS_EXTRA = "com.team2052.frckrawler.disableAdd";
	
	private static final int COMMENT_CHAR_LIMIT = 20;
	private static final int EDIT_BUTTON_ID = 1;
	
	private boolean limitLoading;
	private int minMatch;
	private int maxMatch;
	
	private DBManager dbManager;
	private MatchData[] matchData;
	private GetMatchDataTask getDataTask;
	private EditText minNumberEntry;
	private EditText maxNumberEntry;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_data);
		
		if(getIntent().getBooleanExtra(DISABLE_BUTTONS_EXTRA, false)) {
			findViewById(R.id.addData).setEnabled(false);
			findViewById(R.id.changeSelection).setEnabled(false);
			
		} else {
			findViewById(R.id.addData).setOnClickListener(this);
			findViewById(R.id.changeSelection).setOnClickListener(this);
		}
		
		limitLoading = getIntent().getBooleanExtra(LIMIT_LOADING_EXTRA, false);
		minMatch = 1;
		maxMatch = 10;
		dbManager = DBManager.getInstance(this);
		
		if(limitLoading) {
			showSelectionDialog();
		} else {
			getDataTask = new GetMatchDataTask(minMatch, maxMatch);
			getDataTask.execute(this);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		if(getDataTask != null)
			getDataTask.cancel(true);
	}
	
	@Override
	public void onClick(View v) {
		Intent i;
		
		Event e = dbManager.getEventsByColumns(new String[] {DBContract.COL_EVENT_ID}, 
				new String[] {databaseValues
				[getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]})[0];
		
		switch(v.getId()) {
			case R.id.addData:
				
				i = new Intent(this, AddMatchDataDialogActivity.class);
				i.putExtra(AddMatchDataDialogActivity.EVENT_ID_EXTRA, e.getEventID());
				i.putExtra(AddMatchDataDialogActivity.GAME_NAME_EXTRA, e.getGameName());
				startActivityForResult(i, 1);
				
				break;
				
			case EDIT_BUTTON_ID:
				
				i = new Intent(this, EditMatchDataDialogActivity.class);
				i.putExtra(EditMatchDataDialogActivity.EVENT_ID_EXTRA, e.getEventID());
				i.putExtra(EditMatchDataDialogActivity.GAME_NAME_EXTRA, e.getGameName());
				i.putExtra(EditMatchDataDialogActivity.MATCH_ID_EXTRA, (Integer)v.getTag());
				startActivityForResult(i, 1);
				break;
				
			case R.id.changeSelection:
				
				showSelectionDialog();
				break;
		}
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON_POSITIVE) {
			minMatch = (int)Double.parseDouble(minNumberEntry.getText().toString());
			maxMatch = (int)Double.parseDouble(maxNumberEntry.getText().toString());
			
			getDataTask = new GetMatchDataTask(minMatch, maxMatch);
			getDataTask.execute(this);
		}
		
		dialog.dismiss();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent i) {
		if(resultCode == RESULT_OK) {
			getDataTask = new GetMatchDataTask(minMatch, maxMatch);
			getDataTask.execute(this);
		}
	}
	
	private void showSelectionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Load Matches...");
		
		LinearLayout fullView = new LinearLayout(this);
		fullView.setOrientation(LinearLayout.VERTICAL);
		LinearLayout dialogView = new LinearLayout(this);
		dialogView.setOrientation(LinearLayout.HORIZONTAL);
		dialogView.addView(new MyTextView(this, "Load matches from ", 18));
		
		minNumberEntry = new EditText(this);
		minNumberEntry.setInputType(InputType.TYPE_CLASS_NUMBER);
		minNumberEntry.setWidth((int)(50 * getResources().getDisplayMetrics().density + 0.5));
		dialogView.addView(minNumberEntry);
		dialogView.addView(new MyTextView(this, " to ", 18));
		maxNumberEntry = new EditText(this);
		maxNumberEntry.setInputType(InputType.TYPE_CLASS_NUMBER);
		maxNumberEntry.setWidth((int)(50 * getResources().getDisplayMetrics().density + 0.5));
		dialogView.addView(maxNumberEntry);
		
		fullView.addView(dialogView);
		fullView.addView(new MyTextView(this, "Note: Loading more than 20 matches at once", 12));
		fullView.addView(new MyTextView(this, "may take a considerable amount of time.", 12));
		
		builder.setView(fullView);
		builder.setPositiveButton("Load", this);
		builder.setNegativeButton("Cancel", this);
		builder.show();
	}
	
	private class GetMatchDataTask extends AsyncTask<RawMatchDataActivity, MyTableRow, Void> {
		
		private int dataNum;
		private int min;
		private int max;
		private TableLayout dataTable;
		
		public GetMatchDataTask(int minMatch, int matchMax) {
			dataNum = 0;
			min = minMatch;
			max = matchMax;
		}
		
		@Override
		protected void onPreExecute() {
			((FrameLayout)findViewById(R.id.progressFrame)).
			addView(new ProgressSpinner(getApplicationContext()));
			
			dataTable = (TableLayout)findViewById(R.id.dataTable);
			dataTable.removeAllViews();
		}
		
		@Override
		protected Void doInBackground(RawMatchDataActivity... params) {
			
			RawMatchDataActivity activity = params[0];
			
			if(limitLoading)
				matchData = dbManager.getMatchDataByColumns(databaseKeys, 
						databaseValues, min, max);
			else 
				matchData = dbManager.getMatchDataByColumns(databaseKeys, databaseValues);
			
			dataNum = matchData.length;
			
			//Create the descriptors row and add it to the array
			MyTableRow descriptorsRow = new MyTableRow(activity);
			
			descriptorsRow.addView(new MyTextView(activity, " ", 18));
			descriptorsRow.addView(new MyTextView(activity, "Event", 18));
			descriptorsRow.addView(new MyTextView(activity, "Team", 18));
			descriptorsRow.addView(new MyTextView(activity, "Match #", 18));
			descriptorsRow.addView(new MyTextView(activity, "User", 18));
			descriptorsRow.addView(new MyTextView(activity, "Match Type", 18));
			descriptorsRow.addView(new MyTextView(activity, "Comments", 18));
			
			if(matchData.length > 0) {
				for(MetricValue v : matchData[0].getMetricValues()) {
					if(v.getMetric().getType() != DBContract.MATH)
						descriptorsRow.addView
							(new MyTextView(activity, v.getMetric().getMetricName(), 18));
				}
			}
			
			publishProgress(descriptorsRow);
			
			//Add a row for every data entry
			for(int i = 0; i < matchData.length; i++) {
				
				int color;
				
				if(i % 2 == 0)
					color = GlobalValues.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				
				//These calls to the database should take very little time, so they are 
				//"acceptable" to do without an AsyncTask
				Event[] eventArr = dbManager.getEventsByColumns
						(new String[] {DBContract.COL_EVENT_ID}, 
								new String[] {Integer.toString(matchData[i].getEventID())});
				
				Robot[] robotsArr = dbManager.getRobotsByColumns
						(new String[] {DBContract.COL_ROBOT_ID}, 
								new String[] {Integer.toString(matchData[i].getRobotID())});
				
				User[] userArr = dbManager.getUsersByColumns
						(new String[] {DBContract.COL_USER_ID}, 
								new String[] {Integer.toString(matchData[i].getUserID())});
				
				MyButton editButton = new MyButton(activity, "Edit Data", activity);
				editButton.setId(EDIT_BUTTON_ID);
				editButton.setTag(Integer.valueOf(matchData[i].getMatchID()));
				
				ArrayList<View> viewArr = new ArrayList<View>();
				
				viewArr.add(editButton);
				viewArr.add(new MyTextView(activity, eventArr[0].getEventName(), 18));
				viewArr.add(new MyTextView(activity, Integer.toString(robotsArr[0].getTeamNumber()), 
						18));
				viewArr.add(new MyTextView(activity, Integer.toString(matchData[i].getMatchNumber()), 
						18));
				if(userArr.length > 0)
					viewArr.add(new MyTextView(activity, userArr[0].getName(), 18));
				else 
					viewArr.add(new MyTextView(activity, " ", 18));
				
				viewArr.add(new MyTextView(activity, matchData[i].getMatchType(), 18));
				
				//Put a character limit on the comments string
				String displayedString = new String();
				
				if(matchData[i].getComments().length() < COMMENT_CHAR_LIMIT)
					displayedString = matchData[i].getComments();
				else
					displayedString = matchData[i].getComments().
						substring(0, COMMENT_CHAR_LIMIT - 1);
					
				viewArr.add(new MyTextView(activity, displayedString, 18));
				
				//Add the metric data
				MetricValue[] metricValues = matchData[i].getMetricValues();
				
				for(int k = 0; k < metricValues.length; k++) {
					if(metricValues[k].getMetric().getType() != DBContract.MATH)
						viewArr.add(new MyTextView(activity, metricValues[k].
								getValueAsHumanReadableString(), 18));
				}
				
				publishProgress(new MyTableRow(activity, viewArr.toArray
						(new View[0]), color));
				
				try {	//Wait for the UI to update
					Thread.sleep(50);
				} catch(InterruptedException e) {}
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(MyTableRow... row) {
			dataTable.addView(row[0]);
		}
		
		@Override
		protected void onPostExecute(Void v) {
			((TextView)findViewById(R.id.matchNumTextBox)).setText(dataNum + " Data Displayed");
			((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
		}
	}
}
