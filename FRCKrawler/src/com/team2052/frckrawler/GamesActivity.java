package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;

public class GamesActivity extends TabActivity implements OnClickListener {
	
	private static final int EDIT_BUTTON_ID = 1;
	private static final int EVENTS_BUTTON_ID = 2;
	private static final int METRICS_BUTTON_ID = 3;
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		Button add = ((Button)findViewById(R.id.addGameButton));
		add.setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		new GetGamesTask().execute();
	}
	
	public void postResults(Game[] games) {
		
		TableLayout table = (TableLayout)findViewById(R.id.gamesDataTable);
		table.removeAllViews();
		
		for(int i = 0; i < games.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = GlobalSettings.ROW_COLOR;
			else
				color = Color.TRANSPARENT;
			
			MyButton editButton = new MyButton(this, "Edit Game", this, games[i].getName());
			editButton.setId(EDIT_BUTTON_ID);
			MyButton eventsButton = new MyButton(this, "Events", this, games[i].getName());
			eventsButton.setId(EVENTS_BUTTON_ID);
			MyButton metricsButton = new MyButton(this, "Metrics", this, games[i].getName());
			metricsButton.setId(METRICS_BUTTON_ID);
			
			table.addView(new MyTableRow(this, new View[] {
					editButton,
					new MyTextView(this, games[i].getName(), 18),
					eventsButton,
					metricsButton
			}, color));
		}
	}
	
	
	/*****
	 * Method: onClick
	 * 
	 * Summary: This is the listener for the Views on this activity.
	 *****/
	
	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addGameButton:
			
				i = new Intent(this, AddGameDialogActivity.class);
				startActivity(i);
				
				break;
				
			case EDIT_BUTTON_ID:
			
				i = new Intent(this, EditGameDialogActivity.class);
				i.putExtra(EditGameDialogActivity.GAME_NAME_EXTRA, (String)v.getTag());
				startActivity(i);
				
				break;
				
			case EVENTS_BUTTON_ID:
				
				i = new Intent(this, EventsActivity.class);
				i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[] 
						{(String)v.getTag()});
				i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[] 
						{(String)v.getTag()});
				i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[] 
						{DBContract.COL_GAME_NAME});
				startActivity(i);
				
				break;
				
			case METRICS_BUTTON_ID:
				
				i = new Intent(this, MetricSelectionActivity.class);
				i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[] 
						{(String)v.getTag()});
				i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[] 
						{(String)v.getTag()});
				i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[] 
						{DBContract.COL_GAME_NAME});
				startActivity(i);
				
				break;
		}
	}
	
	private class GetGamesTask extends AsyncTask<Void, Void, Game[]> {
		
		protected Game[] doInBackground(Void... params) {
			
			return dbManager.getAllGames();
		}
		
		protected void onPostExecute(Game[] games) {
			
			postResults(games);
		}
	}
}
