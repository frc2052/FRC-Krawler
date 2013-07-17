package com.team2052.frckrawler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;

public class OptionsActivity extends TabActivity implements OnClickListener {
	
	public static final String PREFS_NAME = "userSettings";
	public static final String PREFS_IS_WRITTEN = "isWritten";
	public static final String PREFS_COMPILE_WEIGHT = "compileWeight";
	public static final String PREFS_GENERATE_ROBOTS = "generateRobots";
	public static final String PREFS_ROBOT_GAME = "robotGame";
	
	private String gameName;
	
	private SharedPreferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		
		((EditText)findViewById(R.id.weightValue)).setText
				(Float.toString(preferences.getFloat(PREFS_COMPILE_WEIGHT, 1.0f)));
		((ToggleButton)findViewById(R.id.generateRobots)).setChecked
				(preferences.getBoolean(PREFS_GENERATE_ROBOTS, false));
		gameName = preferences.getString(PREFS_ROBOT_GAME, "none");
		
		findViewById(R.id.saveOptions).setOnClickListener(this);
		findViewById(R.id.restoreDefaults).setOnClickListener(this);
		findViewById(R.id.cancelOptions).setOnClickListener(this);
		findViewById(R.id.weightHelp).setOnClickListener(this);
		findViewById(R.id.generateRobots).setOnClickListener(this);
		findViewById(R.id.generateHelp).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.saveOptions:
				
				SharedPreferences.Editor editor = preferences.edit();
				
				try {
					float weight = Float.parseFloat(((EditText)findViewById
							(R.id.weightValue)).getText().toString());
					editor.putFloat(PREFS_COMPILE_WEIGHT, weight);
					
					boolean generateRobots = ((ToggleButton)findViewById(R.id.generateRobots)).
							isChecked();
					editor.putBoolean(PREFS_GENERATE_ROBOTS, generateRobots);
					editor.putString(PREFS_ROBOT_GAME, gameName);
					editor.putBoolean(PREFS_IS_WRITTEN, true);
					editor.commit();
					
					finish();
					
				} catch(NumberFormatException e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Options Not Saved");
					builder.setMessage("You must enter a number for the weight when " +
							"compiling data.");
					builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
				}
				
				break;
				
			case R.id.restoreDefaults:
				
				restoreDefaultOptions(this, true);
				finish();
				break;
				
			case R.id.cancelOptions:
				
				finish();
				break;
				
			case R.id.weightHelp:
				
				AlertDialog.Builder weightBuilder = new AlertDialog.Builder(this);
				weightBuilder.setTitle("About Compiled Data Weight");
				weightBuilder.setMessage("The weight for compiled data is how many times " +
						"a match's data is worth in the averaging process, compared to the " +
						"previous match. Values " +
						" greater than 1 make later matches more valuable, and values " +
						"less than 1 make earlier matches more valuable. A value of 1 makes" +
						"all matches equal. This " +
						"applies to math, counter, slider, boolean, and numeric chooser " +
						"metrics that track match data. For example, " +
						"a robot receives a score of 10 in a given metric for match #1, and " +
						"a score of 20 for the same metric in match #5. If the weight " +
						"is 1.5, the average shown for compiled data will be 17.5. Match #5 " +
						"counts more towards the average. Likewise, if the weight is set " +
						"to 0.5, the average shown will be 12.5. Match #1 is more 'valuable' " +
						"in the averaging process. Be careful, values larger than 2 or 3 will" +
						" seriously skew data towards later matches. The default setting is 1.");
				weightBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				weightBuilder.show();
				break;
				
			case R.id.generateRobots:
				
				final ToggleButton b = (ToggleButton)v;
				
				if(b.isChecked()) {
					Game[] games = DBManager.getInstance(this).getAllGames();
					final CharSequence[] choices = new CharSequence[games.length];

					for(int i = 0; i < games.length; i++)
						choices[i] = games[i].getName();

					AlertDialog.Builder generateBuilder = new AlertDialog.Builder(this);
					generateBuilder.setTitle("Choose a game for new robots");
					generateBuilder.setItems(choices, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							gameName = choices[which].toString();
							dialog.dismiss();
						}
					});

					AlertDialog gameDialog = generateBuilder.create();
					gameDialog.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							b.setChecked(false);
						}
					});
					gameDialog.show();
					
				} else {
					gameName = "none";
				}
				
				break;
				
			case R.id.generateHelp:
				
				break;
		}
	}
	
	/*****
	 * Method: restoreDefualtOptions
	 * 
	 * @param overwrite
	 * 
	 * Summary: Sets the user options to their defaults. If overwrite is false,
	 * the settings will not overwrite the options saved. If true, the options
	 * will be overwritten.
	 * 	 */
	public static void restoreDefaultOptions(Context context, boolean overwrite) {
		SharedPreferences prefs = 
				context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		
		if(!prefs.getBoolean(PREFS_IS_WRITTEN, false) || overwrite) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(PREFS_IS_WRITTEN, true);
			editor.putFloat(PREFS_COMPILE_WEIGHT, 1.0f);
			editor.putBoolean(PREFS_GENERATE_ROBOTS, false);
			editor.putString(PREFS_ROBOT_GAME, "none");
			editor.commit();
		}
	}
}
