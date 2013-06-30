package com.team2052.frckrawler;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.database.structures.SortKey;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.QueryWidget;

public class QuerySortingDialogActivity extends Activity implements OnClickListener {
	
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventID";
	
	private DBManager dbManager;
	private QueryWidget matchQueryWidget;
	private QueryWidget pitQueryWidget;
	private volatile Event event;
	private volatile Metric[] matchMetrics;
	private volatile Metric[] pitMetrics;
	private ArrayList<Metric> sortMetrics;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_query_and_sorting);
		
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
		sortMetrics = new ArrayList<Metric>();
		
		new GetDataTask().execute();
	}

	public void onClick(View v) {
		
		if(v.getId() == R.id.cancel) {
			finish();
			
		} else {
			QueryActivity.setQuery(Integer.parseInt(getIntent().getStringExtra
					(EVENT_ID_EXTRA)), matchQueryWidget.getQuerys(), 
					pitQueryWidget.getQuerys(), new Query[0]);
			
			int itemPos = ((Spinner)findViewById(R.id.sortKey)).getSelectedItemPosition();
			
			if(itemPos != 0) {
				int metricType = SortKey.PIT_METRIC_TYPE;
				
				if(itemPos <= matchMetrics.length)
					metricType = SortKey.MATCH_METRIC_TYPE;
				
				QueryActivity.setSortKey(new SortKey(
					metricType,
					sortMetrics.get(itemPos - 1).getKey())
				);
			}
			else
				QueryActivity.setSortKey(null);
			
			setResult(1);
			finish();
		}
	}
	
	
	/*****
	 * Class: GetMetricsTask
	 */
	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			event = dbManager.getEventsByColumns
					(new String[] {DBContract.COL_EVENT_ID}, 
							new String[] {getIntent().getStringExtra
							(EVENT_ID_EXTRA)})[0];
			matchMetrics = dbManager.getMatchPerformanceMetricsByColumns
					(new String[] {DBContract.COL_GAME_NAME},
							new String[] {event.getGameName()});
			pitMetrics = dbManager.getRobotMetricsByColumns
					(new String[] {DBContract.COL_GAME_NAME},
							new String[] {event.getGameName()});
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			LinearLayout list = (LinearLayout)findViewById(R.id.queryListList);
			list.removeAllViews();
			
			list.addView(new MyTextView(getApplicationContext(), 
					"Match Data", 18));
			matchQueryWidget = new QueryWidget(getApplicationContext(), 
					matchMetrics, QueryActivity.getMatchQuerys
					(Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA))), 
					Query.TYPE_MATCH_DATA);
			list.addView(matchQueryWidget);
			
			list.addView(new MyTextView(getApplicationContext(), 
					"Robot Data", 18));
			pitQueryWidget = new QueryWidget(getApplicationContext(), 
					pitMetrics, QueryActivity.getPitQuerys
					(Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA))),
					Query.TYPE_ROBOT);
			list.addView(pitQueryWidget);
			
			for(int i = 0; i < matchMetrics.length; i++) {
				if(matchMetrics[i].getType() != DBContract.CHOOSER && 
						matchMetrics[i].getType() != DBContract.TEXT)
					sortMetrics.add(matchMetrics[i]);
			}
				
			for(int i = 0; i < pitMetrics.length; i++) {
				if(pitMetrics[i].getType() != DBContract.CHOOSER && 
						pitMetrics[i].getType() != DBContract.TEXT)
					sortMetrics.add(pitMetrics[i]);
			}
			
			ArrayAdapter<String> sortChoices = new ArrayAdapter<String>
					(QuerySortingDialogActivity.this, R.layout.scout_spinner_item);
			sortChoices.add("None");
			
			for(int i = 0; i < sortMetrics.size(); i++)
				sortChoices.add(sortMetrics.get(i).getMetricName());
			
			((Spinner)findViewById(R.id.sortKey)).setAdapter(sortChoices);
		}
	}
}
