package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.MetricValue.MetricTypeMismatchException;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MetricWidget;

public class AddRobotDialogActivity extends Activity implements OnClickListener, 
																	OnItemSelectedListener {
	
	public static final String TEAM_NUMBER_EXTRA = "com.team2052.frckrawler.teamNumberExtra";
	
	private DBManager dbManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode
			(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.dialogactivity_add_robot);
		
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.addRobot).setOnClickListener(this);
		((Spinner)findViewById(R.id.gameSpinner)).setOnItemSelectedListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		//Set the choices for the spinner
		Game[] games = dbManager.getAllGames();
		String[] spinnerVals = new String[games.length];
		
		for(int i = 0; i < spinnerVals.length; i++) {
			spinnerVals[i] = games[i].getName();
		}
		
		Spinner gameSpinner = (Spinner)findViewById(R.id.gameSpinner);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, spinnerVals);
		
		gameSpinner.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.addRobot:
				
				if(((Spinner)findViewById(R.id.gameSpinner)).getChildCount() == 0) {
					Toast.makeText(this, "Could not add robot. " +
							"There are no games in the database. " +
							"Please create a game first.", Toast.LENGTH_LONG);
				}
				
				LinearLayout metricList = (LinearLayout)findViewById(R.id.metricList);
				MetricValue[] vals = new MetricValue[metricList.getChildCount()];
				
				for(int i = 0; i < metricList.getChildCount(); i++) {
					try {
						MetricWidget widget = (MetricWidget)metricList.getChildAt(i);
						vals[i] = new MetricValue(widget.getMetric(), widget.getValues());
						
					} catch (MetricTypeMismatchException e) {
						e.printStackTrace();
					}
				}
				
				dbManager.addRobot(new Robot(
						Integer.parseInt(getIntent().getStringExtra(TEAM_NUMBER_EXTRA)),
						((Spinner)findViewById(R.id.gameSpinner)).getSelectedItem().toString(),
						((EditText)findViewById(R.id.comments)).getText().toString(),
						vals
						));
				
				setResult(RESULT_OK);
				finish();
				
				break;
				
			case R.id.cancel:
				
				finish();
				
				break;
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> adapter, View v, int selectedItem,
			long id) {
		
		LinearLayout metricList = (LinearLayout)findViewById(R.id.metricList);
		metricList.removeAllViews();
		
		Metric[] metrics = dbManager.getRobotMetricsByColumns
				(new String[] {DBContract.COL_GAME_NAME}, 
						new String[] {(String)adapter.getSelectedItem()});
		
		for(int i = 0; i < metrics.length; i++) {
			
			MetricWidget m = MetricWidget.createWidget(this, metrics[i]);
			m.setTag(metrics[i].getKey());
			metricList.addView(m);
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> adapter) {
		
		((ListView)findViewById(R.id.metricList)).removeAllViews();
	}
}
