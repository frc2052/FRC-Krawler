package com.team2052.frckrawler;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class QueryActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int COMMENT_BUTTON_ID = 1;
	private static final int PICTURE_BUTTON_ID = 2;
	private static HashMap<Integer, Query[]> matchQuerys = new HashMap<Integer, Query[]>();
	private static HashMap<Integer, Query[]> pitQuerys = new HashMap<Integer, Query[]>();
	private static HashMap<Integer, Query[]> driverQuerys = new HashMap<Integer, Query[]>();
	
	private CompiledData[] data;
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);
		
		findViewById(R.id.query).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onStart() {
		super.onStart();
		new GetCompiledDataTask().execute(this);
	}
	
	public void postResults(MyTableRow[] rows) {
		TableLayout table = (TableLayout)findViewById(R.id.queryDataTable);
		table.removeAllViews();
		
		for(int i = 0; i < rows.length; i++)
			table.addView(rows[i]);
		
		((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
	}
	
	public void onClick(View v) {
		if(v.getId() == R.id.query) { 
			
			Intent i = new Intent(this, QuerySortingDialogActivity.class);
			i.putExtra(QuerySortingDialogActivity.EVENT_ID_EXTRA, 
					databaseValues[getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]);
			startActivityForResult(i, 1);
			
		} else if(v.getId() == COMMENT_BUTTON_ID) {
			
			Intent i = new Intent(this, CommentDialogActivity.class);
			i.putExtra(CommentDialogActivity.COMMENT_ARRAY_EXTRA, 
					data[(Integer)v.getTag()].getMatchComments());
			i.putExtra(CommentDialogActivity.MATCHES_ARRAY_EXTRA, 
					data[(Integer)v.getTag()].getMatchesPlayed());
			startActivityForResult(i, 1);
			
		} else if(v.getId() == PICTURE_BUTTON_ID) {
			
			Intent i = new Intent(this, PicturesActivity.class);
			i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, 
					new String[] {DBContract.COL_ROBOT_ID});
			i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, 
					new String[] {Integer.toString(data[(Integer)v.getTag()].
							getRobot().getID())});
			startActivity(i);
		}
	}
	
	@Override
	protected void onActivityResult(int r, int c, Intent i) {
		new GetCompiledDataTask().execute(this);
	}
	
	public static void setQuery(int eventID, Query[] match, Query[] pit, 
			Query[] driver) {
		matchQuerys.put(eventID, match);
		pitQuerys.put(eventID, pit);
		driverQuerys.put(eventID, driver);
	}
	
	public static Query[] getMatchQuerys(int eventID) {
		return matchQuerys.get(eventID);
	}
	
	public static Query[] getPitQuerys(int eventID) {
		return pitQuerys.get(eventID);
	}
	
	public static Query[] getDriverQuerys(int eventID) {
		return driverQuerys.get(eventID);
	}
	
	private class GetCompiledDataTask extends AsyncTask
										<QueryActivity, Void, MyTableRow[]> {
											
		protected void onPreExecute() {
			((FrameLayout)findViewById(R.id.progressFrame)).
			addView(new ProgressSpinner(getApplicationContext()));
		}

		protected MyTableRow[] doInBackground(QueryActivity... params) {
			
			QueryActivity activity = params[0];
			
			ArrayList<Query> allQuerys = new ArrayList<Query>();
			Query[] matchQuerys = getMatchQuerys(Integer.parseInt
					(databaseValues[getAddressOfDatabaseKey
					                (DBContract.COL_EVENT_ID)]));
			Query[] pitQuerys = getPitQuerys(Integer.parseInt
					(databaseValues[getAddressOfDatabaseKey
					                (DBContract.COL_EVENT_ID)]));
			Query[] driverQuerys = getDriverQuerys(Integer.parseInt
					(databaseValues[getAddressOfDatabaseKey
					                (DBContract.COL_EVENT_ID)]));
			
			if(matchQuerys != null)
				for(Query q : matchQuerys)
					allQuerys.add(q);
			if(pitQuerys != null)
				for(Query q : pitQuerys)
					allQuerys.add(q);
			if(driverQuerys != null)
				for(Query q : driverQuerys)
					allQuerys.add(q);
					
			Query[] querys = allQuerys.toArray(new Query[0]);
			
			if(querys.length == 0) {
				data = dbManager.getCompiledEventData
					(Integer.parseInt(databaseValues[
					getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]), 
					new Query[0]);
				
			} else {
				
				data = dbManager.getCompiledEventData
						(Integer.parseInt(databaseValues[
						getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]), 
						querys);
			}
			
			MyTableRow[] rows = new MyTableRow[data.length + 1];
			MyTableRow descriptorsRow = new MyTableRow(activity);
			
			descriptorsRow.addView(new MyTextView(activity, " ", 18));
			descriptorsRow.addView(new MyTextView(activity, "Team", 18));
			descriptorsRow.addView(new MyTextView(activity, "M. Played", 18));
			descriptorsRow.addView(new MyTextView(activity, "Comments", 18));
			descriptorsRow.addView(new MyTextView(activity, "Pictures", 18));
			
			MetricValue[] matchMetrics;
			Metric[] robotMetrics;
			MetricValue[] driverMetrics;
			
			if(data.length > 0) {
				matchMetrics = data[0].getCompiledMatchData();
				robotMetrics = data[0].getRobot().getMetrics();
				driverMetrics = data[0].getCompiledDriverData();
			} else {
				matchMetrics = new MetricValue[0];
				robotMetrics = new Metric[0];
				driverMetrics = new MetricValue[0];
			}
			
			//Add all the metric names to the descriptors row, but 
			//only if they are displayed.
			for(MetricValue m : matchMetrics)
				if(m.getMetric().isDisplayed())
					descriptorsRow.addView(new MyTextView(activity, 
						m.getMetric().getMetricName(), 18));
			
			for(Metric m : robotMetrics)
				if(m.isDisplayed())
					descriptorsRow.addView(new MyTextView(activity, 
							m.getMetricName(), 18));
			
			for(MetricValue m : driverMetrics)
				if(m.getMetric().isDisplayed())
					descriptorsRow.addView(new MyTextView(activity, 
						m.getMetric().getMetricName(), 18));
			
			rows[0] = descriptorsRow;
			
			//Create a new row for each piece of data
			for(int dataCount = 0; dataCount < data.length; dataCount++) {
				
				int color;
				
				if(dataCount % 2 == 0)
					color = GlobalSettings.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				MyTableRow dataRow = new MyTableRow(activity, color);
				dataRow.addView(new CheckBox(activity));
				
				MyButton commentsButton = new MyButton
						(activity, "Comments", activity);
				commentsButton.setId(COMMENT_BUTTON_ID);
				commentsButton.setTag(Integer.valueOf(dataCount));
				
				MyButton picturesButton = new MyButton
						(activity, "Pictures", activity);
				picturesButton.setId(PICTURE_BUTTON_ID);
				picturesButton.setTag(Integer.valueOf(dataCount));
				
				dataRow.addView(new MyTextView(activity, Integer.toString(
						data[dataCount].getRobot().getTeamNumber()), 18));
				dataRow.addView(new MyTextView(activity, Integer.toString(
						data[dataCount].getMatchesPlayed().length), 18));
				dataRow.addView(commentsButton);
				dataRow.addView(picturesButton);
				
				//Get the data arrays for the robot, matches, and driver data
				MetricValue[] matchData = data[dataCount].getCompiledMatchData();
				MetricValue[] robotData = data[dataCount].getRobot().
						getMetricValues();
				MetricValue[] driverData = data[dataCount].getCompiledDriverData();
				
				for(int i = 0; i < matchData.length; i++)
					if(matchData[i].getMetric().isDisplayed())
						dataRow.addView(new MyTextView(activity, 
								matchData[i].getValueAsHumanReadableString(), 18));
				
				for(int i = 0; i < robotData.length; i++)
					if(robotData[i].getMetric().isDisplayed())
						dataRow.addView(new MyTextView(activity, 
								robotData[i].getValueAsHumanReadableString(), 18));
				
				for(int i = 0; i < driverData.length; i++)
					if(driverData[i].getMetric().isDisplayed())
						dataRow.addView(new MyTextView(activity, 
								driverData[i].getValueAsHumanReadableString(), 18));
				
				rows[dataCount + 1] = dataRow;
			}
			
			return rows;
		}
		
		protected void onPostExecute(MyTableRow[] rows) {
			
			postResults(rows);
		}
	}
}