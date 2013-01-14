package com.team2052.frckrawler;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DatabaseManager;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.gui.*;

public class GamesActivity extends TabActivity {
	
	private DatabaseManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		dbManager = DatabaseManager.getInstance(this);
	}
	
	public void onStart() {
		
		super.onStart();
		
		Game[] games = dbManager.getAllGames();
		TableLayout table = (TableLayout)findViewById(R.id.gamesDataTable);
		
		for(int i = 0; i < games.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			table.addView(new MyTableRow(this, new View[] {
					new MyTextView(this, games[i].getName())
			}, color));
		}
	}
}
