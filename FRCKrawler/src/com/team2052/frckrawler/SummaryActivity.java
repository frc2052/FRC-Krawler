package com.team2052.frckrawler;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class SummaryActivity extends Activity implements OnClickListener {
	
	private static final int COMMENT_CHAR_LIMIT = 20;
	private static final int MATCH_DATA_BUTTON_ID = 2;
	
	private static Query[] queries = new Query[0];
	
	private DBManager dbManager;
	private TableLayout table;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_summary);
		
		table = (TableLayout)findViewById(R.id.summaryTable);
		
		dbManager = DBManager.getInstance(this);
		
		Event e = dbManager.summaryGetEvent();
		if(e != null)
			((TextView)findViewById(R.id.summaryEventName)).
					setText(e.getEventName() + ", " + e.getGameName());
		
		new GetSummaryTask().execute();
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
			case MATCH_DATA_BUTTON_ID:
				
				new ShowMatchDataTask().execute((Integer)v.getTag());
				
				break;
		}
	}
	
	public static void setQuerys(Query[] q) {
		queries = q;
	}
	
	private class GetSummaryTask extends AsyncTask<Void, MyTableRow, Void> {
		
		AlertDialog progressDialog;
		
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
			builder.setTitle("Loading...");
			builder.setView(new ProgressSpinner(SummaryActivity.this));
			builder.setCancelable(false);
			progressDialog = builder.create();
			progressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			CompiledData[] compiledData = dbManager.summaryGetCompiledData(queries);
			
			ArrayList<View> descriptorsViews = new ArrayList<View>();
			descriptorsViews.add(new MyTextView(SummaryActivity.this, " ", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "Team", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "M. Played", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "R. Comments", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "M. Data", 18));
			
			if(compiledData.length > 0) {
				//Add the match metric names to the table
				for(int i = 0; i < compiledData[0].getCompiledMatchData().length; i++)
					if(compiledData[0].getCompiledMatchData()[i].getMetric().isDisplayed())
						descriptorsViews.add(new MyTextView(SummaryActivity.this, 
								compiledData[0].getCompiledMatchData()[i].getMetric().
								getMetricName(), 18));
				
				//Add the robot metric names to the table
				for(int i = 0; i < compiledData[0].getRobot().getMetrics().length; i++)
					if(compiledData[0].getRobot().getMetrics()[i].isDisplayed())
						descriptorsViews.add(new MyTextView(SummaryActivity.this, compiledData[0].
								getRobot().getMetrics()[i].getMetricName(), 18));
			}
			
			MyTableRow descriptorsRow = new MyTableRow(SummaryActivity.this,
					descriptorsViews.toArray(new View[0]) , Color.TRANSPARENT);
			
			publishProgress(descriptorsRow);
			
			for(int i = 0; i < compiledData.length; i++) {
				
				int color;
				
				if(i % 2 == 0)
					color = GlobalSettings.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				Button rawDataButton = new Button(SummaryActivity.this);
				rawDataButton.setOnClickListener(SummaryActivity.this);
				rawDataButton.setId(MATCH_DATA_BUTTON_ID);
				rawDataButton.setTag(compiledData[i].getRobot().getID());
				rawDataButton.setText("Match Data");
				
				ArrayList<View> dataRow = new ArrayList<View>();
				dataRow.add(new CheckBox(SummaryActivity.this));
				dataRow.add(new MyTextView(SummaryActivity.this, 
						Integer.toString(compiledData[i].getRobot().
								getTeamNumber()), 18));
				dataRow.add(new MyTextView(SummaryActivity.this, Integer.toString(
						compiledData[i].getMatchesPlayed().length), 18));
				dataRow.add(new MyTextView(SummaryActivity.this, compiledData[i].
						getRobot().getComments(), 18));
				dataRow.add(rawDataButton);
				
				for(MetricValue v : compiledData[i].getRobot().getMetricValues())
					if(v.getMetric().isDisplayed())
						dataRow.add(new MyTextView(SummaryActivity.this, 
								v.getValueAsHumanReadableString(), 18));
				
				for(MetricValue v : compiledData[i].getCompiledMatchData())
					if(v.getMetric().isDisplayed())
						dataRow.add(new MyTextView(SummaryActivity.this, 
								v.getValueAsHumanReadableString(), 18));
				
				MyTableRow row = new MyTableRow(
						SummaryActivity.this, dataRow.toArray(new View[0]), color);
				
				publishProgress(row);
				
				try{
					Thread.sleep(100);
				} catch(InterruptedException e) {}
			}
			
			return null;
		}
		
		protected void onProgressUpdate(MyTableRow... row) {
			table.addView(row[0]);
		}
		
		protected void onPostExecute(Void v) {
			progressDialog.dismiss();
		}
	}
	
	private class ShowMatchDataTask extends AsyncTask<Integer, Void, MatchData[]> {

		@Override
		protected MatchData[] doInBackground(Integer... params) {
			int robotID = params[0];
			return dbManager.summaryGetMatchDataByColumns(new String[] 
					{DBContract.COL_ROBOT_ID}, new String[] {Integer.toString(robotID)});
		}
		
		@Override
		protected void onPostExecute(MatchData[] matchData) {
			AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
			builder.setTitle("Raw Match Data");
			
			TableLayout dataTable = new TableLayout(SummaryActivity.this);
			
			MyTableRow descriptorsRow = new MyTableRow(SummaryActivity.this);
			descriptorsRow.addView(new MyTextView(SummaryActivity.this, "Match #", 18));
			descriptorsRow.addView(new MyTextView(SummaryActivity.this, "Match Type", 18));
			descriptorsRow.addView(new MyTextView(SummaryActivity.this, "Comments", 18));
			
			if(matchData.length > 0) {
				for(MetricValue v : matchData[0].getMetricValues()) {
					descriptorsRow.addView
						(new MyTextView(SummaryActivity.this, v.getMetric().getMetricName(), 18));
				}
			}
			
			dataTable.addView(descriptorsRow);
			
			for(int i = 0; i < matchData.length; i++) {
				
				int color;
				
				if(i % 2 == 0)
					color = GlobalSettings.ROW_COLOR;
				else
					color = Color.TRANSPARENT;
				
				ArrayList<View> viewArr = new ArrayList<View>();
				viewArr.add(new MyTextView(SummaryActivity.this, Integer.toString(matchData[i].getMatchNumber()), 
						18));
				viewArr.add(new MyTextView(SummaryActivity.this, matchData[i].getMatchType(), 18));
				
				//Put a character limit on the comments string
				String displayedString = new String();
				
				if(matchData[i].getComments().length() < COMMENT_CHAR_LIMIT)
					displayedString = matchData[i].getComments();
				else
					displayedString = matchData[i].getComments().
						substring(0, COMMENT_CHAR_LIMIT - 1);
					
				viewArr.add(new MyTextView(SummaryActivity.this, displayedString, 18));
				
				//Add the metric data
				MetricValue[] metricValues = matchData[i].getMetricValues();
				
				for(int k = 0; k < metricValues.length; k++) {
					viewArr.add(new MyTextView(SummaryActivity.this, metricValues[k].
							getValueAsHumanReadableString(), 18));
				}
				
				dataTable.addView(new MyTableRow(SummaryActivity.this, 
						viewArr.toArray(new View[0]), color));
			}
			
			builder.setView(dataTable);
			builder.show();
		}
	}
}
