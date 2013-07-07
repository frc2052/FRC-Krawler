package com.team2052.frckrawler;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class RobotsActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int COMMENT_CHAR_LIMIT = 20;
	private static final int EDIT_ROBOT_ID = 1;
	private static final int EVENTS_ID = 2;
	private static final int PICTURES_ID = 3;
	
	private DBManager dbManager;
	private GetRobotsTask getRobotsTask;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robots);
		
		try {
			String value = 
					databaseValues[getAddressOfDatabaseKey(DBContract.COL_TEAM_NUMBER)];
			findViewById(R.id.addRobotButton).setOnClickListener(this);
			
		} catch(ArrayIndexOutOfBoundsException e) {
			findViewById(R.id.addRobotButton).setEnabled(false);
		}
		
		dbManager = DBManager.getInstance(this);
		
		getRobotsTask = new GetRobotsTask();
		getRobotsTask.execute();
	}
	
	public void onStop() {
		super.onStop();
		getRobotsTask.cancel(true);
	}

	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addRobotButton:
				
				try {
					i = new Intent(this, AddRobotDialogActivity.class);
					i.putExtra(AddRobotDialogActivity.TEAM_NUMBER_EXTRA, 
						databaseValues[this.getAddressOfDatabaseKey
						               (DBContract.COL_TEAM_NUMBER)]);
					startActivityForResult(i, 1);
				} catch (ArrayIndexOutOfBoundsException e) {
					Toast.makeText(this, "You can only add robots by going through the " +
							"teams list first.", Toast.LENGTH_SHORT).show();
				}
			
				break;
				
			case EDIT_ROBOT_ID:
				
				i = new Intent(this, EditRobotDialogActivity.class);
				i.putExtra(EditRobotDialogActivity.ROBOT_ID_EXTRA, v.getTag().toString());
				startActivityForResult(i, 1);
				
				break;
				
			case EVENTS_ID:
				
				String[] passedParents = new String[parents.length + 1];
				
				for(int p = 0; p < passedParents.length; p++) {
					
					if(p < parents.length)
						passedParents[p] = parents[p];
					else
						passedParents[p] = "Robot";
				}
				
				Event[] e = dbManager.getEventsByRobot((Integer)v.getTag());
				
				String[] passedDBVals = new String[e.length];
				String[] passedDBKeys = new String[e.length];
				
				for(int p = 0; p < passedDBVals.length; p++) {
					passedDBVals[p] = Integer.toString(e[p].getEventID());
					passedDBKeys[p] = DBContract.COL_EVENT_ID;
				}
				
				i = new Intent(this, EventsActivity.class);
				i.putExtra(PARENTS_EXTRA, passedParents);
				i.putExtra(DB_VALUES_EXTRA, passedDBVals);
				i.putExtra(DB_KEYS_EXTRA, passedDBKeys);
				startActivity(i);
				
				break;
				
			case PICTURES_ID:
				
				i = new Intent(this, PicturesActivity.class);
				i.putExtra(PARENTS_EXTRA, parents);
				i.putExtra(DB_VALUES_EXTRA, new String[] {v.getTag().toString()});
				i.putExtra(DB_KEYS_EXTRA, new String[] {DBContract.COL_ROBOT_ID});
				startActivity(i);
				
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent i) {
		if(resultCode == RESULT_OK)
			new GetRobotsTask().execute();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {}
	
	
	/*****
	 * Class: GetRobotTask
	 * 
	 * @author Charles Hofer
	 *
	 * Description: gets the robots out of the database and adds them to 
	 * the table, row by row.
	 */
	
	private class GetRobotsTask extends AsyncTask<Void, MyTableRow, Void> {
		
		private int numRobots;
		
		protected void onPreExecute() {
			numRobots = 0;
			((FrameLayout)findViewById(R.id.progressFrame)).addView
					(new ProgressSpinner(RobotsActivity.this));
			
			((TableLayout)findViewById(R.id.robotsDataTable)).removeAllViews();
		}

		protected Void doInBackground(Void... params) {
			Robot[] robots = dbManager.getRobotsByColumns
					(databaseKeys, databaseValues, true);
			
			Metric[] metrics;
			
			if(robots.length > 0)
				metrics = robots[0].getMetrics();
			else
				metrics = new Metric[0];
			
			MyTableRow descriptorsRow = new MyTableRow(RobotsActivity.this);
			descriptorsRow.addView(new TextView(RobotsActivity.this));
			descriptorsRow.addView(new MyTextView(RobotsActivity.this, "Team #", 18));
			descriptorsRow.addView(new MyTextView(RobotsActivity.this, "Game", 18));
			descriptorsRow.addView(new MyTextView(RobotsActivity.this, "Comments", 18));
			
			for(Metric m : metrics) {
				
				if(m != null)
					descriptorsRow.addView(new MyTextView(RobotsActivity.this, m.getMetricName(), 18));
			}
			
			publishProgress(descriptorsRow);
			
			for(int i = 0; i < robots.length; i++) {
				
				if(isCancelled())
					break;
				
				int color;
				
				if(i % 2 == 0)
					color = GlobalSettings.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				//Create the buttons for each row
				MyButton editRobot = new MyButton(RobotsActivity.this, "Edit Robot", RobotsActivity.this, 
						Integer.toString(robots[i].getID()));
				editRobot.setId(EDIT_ROBOT_ID);
				
				MyButton events = new MyButton(RobotsActivity.this, "Events", RobotsActivity.this,
						(Integer)robots[i].getID());
				events.setId(EVENTS_ID);
				
				MyButton pictures = new MyButton(RobotsActivity.this, "Pictures", RobotsActivity.this, 
						Integer.toString(robots[i].getID()));
				pictures.setId(PICTURES_ID);
				
				//Holds the row's data
				ArrayList<View> rowArrayList = new ArrayList<View>();
				
				rowArrayList.add(editRobot);
				rowArrayList.add(new MyTextView(RobotsActivity.this, Integer.toString(robots[i].
						getTeamNumber()), 18));
				rowArrayList.add(new MyTextView(RobotsActivity.this, robots[i].getGame(), 18));
				
				//Stops a index out of bounds exception from being thrown if there's a short comment
				String comment;
				
				if(robots[i].getComments() != null && 
						robots[i].getComments().length() >= COMMENT_CHAR_LIMIT)
					comment = robots[i].getComments().substring(0, COMMENT_CHAR_LIMIT - 1);
				else if(robots[i].getComments() != null)
					comment = robots[i].getComments();
				else
					comment = new String();
					
				rowArrayList.add(new MyTextView(RobotsActivity.this, comment, 18));
				
				for(MetricValue m : robots[i].getMetricValues()) {
					rowArrayList.add(new MyTextView(RobotsActivity.this, m.getValueAsHumanReadableString(), 18));
				}
				
				rowArrayList.add(events);
				rowArrayList.add(pictures);
				
				//Add the row to the table
				publishProgress(new MyTableRow(RobotsActivity.this, 
						rowArrayList.toArray(new View[0]), color));
				
				try {	//Wait for the UI to update
					Thread.sleep(75);
				} catch(InterruptedException e) {}
			}
			
			numRobots = robots.length;
			
			return null;
		}
		
		protected void onProgressUpdate(MyTableRow... row) {
			((TableLayout)findViewById(R.id.robotsDataTable)).addView(row[0]);
		}

		protected void onPostExecute(Void v) {
			((TextView)findViewById(R.id.numRobots)).setText(numRobots + " Robots");
			((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
		}
		
		protected void onCancelled(Void v) {
			((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
		}
	}
}
