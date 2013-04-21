package com.team2052.frckrawler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.gui.MyTableRow;

public class SummaryActivity extends Activity {
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scout_summary);
		dbManager = DBManager.getInstance(this);
		
		Event e = dbManager.summaryGetEvent();
		((TextView)findViewById(R.id.summaryEventName)).
				setText(e.getEventName() + ", " + e.getGameName());
		
		new GetSummaryTask().execute();
	}
	
	private class GetSummaryTask extends AsyncTask<Void, MyTableRow, Void> {
		
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			
			
			return null;
		}
		
		protected void onProgressUpdate(MyTableRow... row) {
			
		}
		
		protected void onPostExecute(Void v) {
			
		}
	}
}
