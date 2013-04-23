package com.team2052.frckrawler;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class SummaryActivity extends Activity implements OnClickListener {
	
	private static final int MATCH_COMMENTS_BUTTON_ID = 1;
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
			case MATCH_COMMENTS_BUTTON_ID:
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Match Comments");
				builder.setView(new MyTextView(this, "", 18));
				builder.show();
				
				break;
				
			case MATCH_DATA_BUTTON_ID:
				
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
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "Team", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "M. Played", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "Robot Comments", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "Match Comments", 18));
			descriptorsViews.add(new MyTextView(SummaryActivity.this, "Raw Match Data", 18));
			
			if(compiledData.length > 0) {
				//Add the match metric names to the table
				for(int i = 0; i < compiledData[0].getCompiledMatchData().length; i++)
					descriptorsViews.add(new MyTextView(SummaryActivity.this, 
							compiledData[0].getCompiledMatchData()[i].getMetric().
							getMetricName(), 18));
				
				//Add the robot metric names to the table
				for(int i = 0; i < compiledData[0].getRobot().getMetrics().length; i++)
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
				
				Button matchCommentsButton = new Button(SummaryActivity.this);
				matchCommentsButton.setOnClickListener(SummaryActivity.this);
				matchCommentsButton.setId(MATCH_COMMENTS_BUTTON_ID);
				matchCommentsButton.setTag(compiledData[i].getRobot().getID());
				matchCommentsButton.setText("Match Comments");
				
				Button rawDataButton = new Button(SummaryActivity.this);
				rawDataButton.setOnClickListener(SummaryActivity.this);
				rawDataButton.setId(MATCH_DATA_BUTTON_ID);
				rawDataButton.setTag(compiledData[i].getRobot().getID());
				rawDataButton.setText("Match Data");
				
				ArrayList<View> dataRow = new ArrayList<View>();
				dataRow.add(new MyTextView(SummaryActivity.this, 
						Integer.toString(compiledData[i].getRobot().
								getTeamNumber()), 18));
				dataRow.add(new MyTextView(SummaryActivity.this, Integer.toString(
						compiledData[i].getMatchesPlayed().length), 18));
				dataRow.add(new MyTextView(SummaryActivity.this, compiledData[i].
						getRobot().getComments(), 18));
				dataRow.add(matchCommentsButton);
				dataRow.add(rawDataButton);
				
				for(MetricValue v : compiledData[i].getRobot().getMetricValues())
					dataRow.add(new MyTextView(SummaryActivity.this, 
							v.getValueAsHumanReadableString(), 18));
				
				for(MetricValue v : compiledData[i].getCompiledMatchData())
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
}
