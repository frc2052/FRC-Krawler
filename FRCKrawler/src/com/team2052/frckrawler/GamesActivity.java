package com.team2052.frckrawler;

import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseContract;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.gui.*;

public class GamesActivity extends TabActivity implements OnClickListener {
	
	private static final int EDIT_BUTTON_ID = 1;
	private static final int EVENTS_BUTTON_ID = 2;
	private static final int METRICS_BUTTON_ID = 3;
	
	private DatabaseManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		Button add = ((Button)findViewById(R.id.addGameButton));
		add.setOnClickListener(this);
		
		dbManager = DatabaseManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		Game[] games = dbManager.getAllGames();
		TableLayout table = (TableLayout)findViewById(R.id.gamesDataTable);
		table.removeAllViews();
		
		for(int i = 0; i < games.length; i++) {
			
			int color;
			
			if(i % 2 != 0)
				color = Color.BLUE;
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
				i.putExtra(StackableTabActivity.PARENT_KEYS_EXTRA, new String[] 
						{DatabaseContract.COL_GAME_NAME});
				startActivity(i);
				
				break;
				
			case METRICS_BUTTON_ID:
				
				break;
		}
	}
}
