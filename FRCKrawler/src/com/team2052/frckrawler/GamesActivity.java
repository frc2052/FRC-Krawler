package com.team2052.frckrawler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

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
	private AlertDialog metricSelectDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		Button add = ((Button)findViewById(R.id.addGameButton));
		add.setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
		metricSelectDialog = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		new GetGamesTask().execute();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(metricSelectDialog != null)
			metricSelectDialog.dismiss();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		setNoRootActivitySelected();
	}
	
	public void postResults(Game[] games) {
		TableLayout table = (TableLayout)findViewById(R.id.gamesDataTable);
		table.removeAllViews();
		
		for(int i = 0; i < games.length; i++) {
			int color;
			
			if(i % 2 == 0)
				color = GlobalValues.ROW_COLOR;
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
	
	@Override
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
				final View metricsButton = v;
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				LinearLayout l = new LinearLayout(this);
				l.setOrientation(LinearLayout.VERTICAL);
				
				//Match Metrics Button
				l.addView(new MyButton(this, "Match Metrics", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(GamesActivity.this, MetricsActivity.class);
						i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, 
								MetricsActivity.MATCH_PERF_METRICS);
						i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[] 
								{(String)metricsButton.getTag()});
						i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[] 
								{(String)metricsButton.getTag()});
						i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[] 
								{DBContract.COL_GAME_NAME});
						startActivity(i);
					}
				}));
				
				//Robot Metrics Button
				l.addView(new MyButton(this, "Robot Metrics", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(GamesActivity.this, MetricsActivity.class);
						i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, 
								MetricsActivity.ROBOT_METRICS);
						i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[] 
								{(String)metricsButton.getTag()});
						i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[] 
								{(String)metricsButton.getTag()});
						i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[] 
								{DBContract.COL_GAME_NAME});
						startActivity(i);
					}
				}));
				
				//Cancel Button
				builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				builder.setView(l);
				metricSelectDialog = builder.create();
				metricSelectDialog.show();
				break;
		}
	}
	
	private class GetGamesTask extends AsyncTask<Void, Void, Game[]> {
		
		@Override
		protected Game[] doInBackground(Void... params) {
			return dbManager.getAllGames();
		}
		
		@Override
		protected void onPostExecute(Game[] games) {
			((TextView)findViewById(R.id.gamesNum)).setText(games.length + " Games");
			postResults(games);
		}
	}
}
