package com.team2052.frckrawler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.MetricWidget;

public class EditRobotDialogActivity extends Activity implements OnClickListener {
	
	public static final String ROBOT_ID_EXTRA = "com.team2052.frckrawler.robotIDExtra";
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_edit_robot);
		
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.remove).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		Robot[] rArr = dbManager.getRobotsByColumns
				(new String[] {DBContract.COL_ROBOT_ID},
						new String[] {getIntent().getStringExtra(ROBOT_ID_EXTRA)});
		Robot r = rArr[0];
		
		((TextView)findViewById(R.id.game)).setText(r.getGame());
		((TextView)findViewById(R.id.comments)).setText(r.getComments());
		
		LinearLayout metricList = (LinearLayout)findViewById(R.id.metricList);
		metricList.removeAllViews();
		MetricValue[] metricVals = r.getMetricValues();
		
		for(int i = 0; i < metricVals.length; i++) {
			
			MetricWidget widget = MetricWidget.createWidget(this, metricVals[i]);
			metricList.addView(widget);
		}
	}

	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.save:
				
				LinearLayout metricList = (LinearLayout)findViewById(R.id.metricList);
				String[] updateVals = new String[metricList.getChildCount() + 1];
				String[] updateCols = new String[metricList.getChildCount() + 1];
				
				for(int i = 0; i < metricList.getChildCount(); i++) {
					
					MetricWidget widget = (MetricWidget)metricList.getChildAt(i);
					updateVals[i] = widget.getMetricValue().getValueAsDBReadableString();
					updateCols[i] = widget.getMetric().getKey();
				}
				
				updateVals[updateVals.length - 1] = 
						((EditText)findViewById(R.id.comments)).getText().toString();
				updateCols[updateCols.length - 1] = DBContract.COL_COMMENTS;
				
				dbManager.updateRobots(
						new String[] {DBContract.COL_ROBOT_ID}, 
						new String[] {getIntent().getStringExtra(ROBOT_ID_EXTRA)}, 
						updateCols, 
						updateVals);
				
				finish();
				
				break;
				
			case R.id.remove:
				
				dbManager.removeRobot(Integer.parseInt(
						getIntent().getStringExtra(ROBOT_ID_EXTRA)));
				
				finish();
				
				break;
				
			case R.id.cancel:
				
				finish();
		}
		
	}
}
