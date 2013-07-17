package com.team2052.frckrawler;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;

public class OptionsActivity extends PreferenceActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		//Load the choices for the robot's games
		DBManager db = DBManager.getInstance(this);
		Game[] games = db.getAllGames();
		CharSequence[] gameChoices = new CharSequence[games.length + 1];
		ListPreference gamePreference = (ListPreference)findPreference
				(getResources().getString(R.string.preferences_key_robot_game));
		
		gameChoices[0] = "None";
		for(int i = 1; i < games.length; i++) {
			gameChoices[i] = games[i].getName();
		}
		
		gamePreference.setEntries(gameChoices);
		gamePreference.setEntryValues(gameChoices);
	}
}
