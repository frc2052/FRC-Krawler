package com.team2052.frckrawler;

import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.gui.*;

public class GamesActivity extends TabActivity implements OnClickListener {
	
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
			
			table.addView(new MyTableRow(this, new View[] {
					new MyButton(this, "Edit Game", this, games[i].getName()),
					new MyTextView(this, games[i].getName()),
					new MyButton(this, "Events", this, games[i].getName()),
					new MyButton(this, "Metrics", this, games[i].getName())
			}, color));
		}
	}
	
	
	/*****
	 * Method: onClick
	 * 
	 * Summary: This is the listener for the Views on this activity.
	 *****/
	
	public void onClick(View v) {
		
		if(v.getId() == R.id.addGameButton) {
			
			Intent i = new Intent(this, AddGameDialogActivity.class);
			startActivity(i);
			
		} else {
			
			Intent i = new Intent(this, EditGameDialogActivity.class);
			i.putExtra(EditGameDialogActivity.GAME_NAME_EXTRA, (String)v.getTag());
			startActivity(i);
		}
	}
}
