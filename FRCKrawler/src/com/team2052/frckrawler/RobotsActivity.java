package com.team2052.frckrawler;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.*;
import com.team2052.frckrawler.gui.*;

public class RobotsActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int COMMENT_CHAR_LIMIT = 20;
	private static final int EDIT_ROBOT_ID = 1;
	private static final int EVENTS_ID = 2;
	private static final int PICTURES_ID = 3;
	
	private DBManager dbManager;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robots);
		
		findViewById(R.id.addRobotButton).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onStart();
		
		Robot[] robots = dbManager.getRobotsByColumns(databaseKeys, databaseValues);
		Metric[] metrics;
		
		if(robots.length > 0)
			metrics = robots[0].getMetrics();
		else
			metrics = new Metric[0];
		
		TableLayout table = (TableLayout)findViewById(R.id.robotsDataTable);
		TableRow descriptorsRow = (TableRow)findViewById(R.id.descriptorsRow);
		
		for(Metric m : metrics) {
			
			descriptorsRow.addView(new MyTextView(this, m.getMetricName(), 18));
		}
		
		table.removeAllViews();
		table.addView(descriptorsRow);
		
		for(int i = 0; i < robots.length; i++) {
			
			int color;
			
			if(i % 2 == 0)
				color = Color.BLUE;
			else
				color = Color.TRANSPARENT;
			
			//Create the buttons for each row
			MyButton editRobot = new MyButton(this, "Edit Robot", this, 
					Integer.toString(robots[i].getID()));
			editRobot.setId(EDIT_ROBOT_ID);
			
			MyButton events = new MyButton(this, "Events", this,
					(Integer)robots[i].getID());
			events.setId(EVENTS_ID);
			
			MyButton pictures = new MyButton(this, "Pictures", this, 
					Integer.toString(robots[i].getID()));
			pictures.setId(PICTURES_ID);
			
			//Holds the row's data
			ArrayList<View> rowArrayList = new ArrayList<View>();
			
			rowArrayList.add(editRobot);
			rowArrayList.add(new MyTextView(this, Integer.toString(robots[i].
					getTeamNumber()), 18));
			rowArrayList.add(new MyTextView(this, robots[i].getGame(), 18));
			
			//Stops a index out of bounds exception from being thrown if there's a short comment
			String comment;
			
			if(robots[i].getGame().length() >= COMMENT_CHAR_LIMIT)
				comment = robots[i].getComments().substring(0, COMMENT_CHAR_LIMIT - 1);
			else
				comment = robots[i].getComments();
				
			rowArrayList.add(new MyTextView(this, comment, 18));
			rowArrayList.add(events);
			rowArrayList.add(pictures);
			
			for(Metric m : metrics)
				rowArrayList.add(new MyTextView(this, m.getMetricName(), 18));
			
			//Add the row to the table
			table.addView(new MyTableRow(this, rowArrayList.toArray(new View[0]), color));
		}
	}

	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.addRobotButton:
				
				i = new Intent(this, AddRobotDialogActivity.class);
				i.putExtra(AddRobotDialogActivity.TEAM_NUMBER_EXTRA, 
						databaseValues[this.getAddressOfDatabaseKey
						               (DBContract.COL_TEAM_NUMBER)]);
				startActivity(i);
			
				break;
				
			case EDIT_ROBOT_ID:
				
				i = new Intent(this, EditRobotDialogActivity.class);
				i.putExtra(EditRobotDialogActivity.ROBOT_ID_EXTRA, v.getTag().toString());
				startActivity(i);
				
				break;
				
			case EVENTS_ID:
				
				String[] passedParents = new String[parents.length + 1];
				
				for(int p = 0; p < passedParents.length; p++) {
					
					if(p < parents.length)
						passedParents[p] = parents[p];
					else
						passedParents[p] = "Robot";
				}
				
				Event[] e = dbManager.getEventsByRobot((Integer)v.getTag());
				
				String[] passedDBVals = new String[e.length];
				String[] passedDBKeys = new String[e.length];
				
				for(int p = 0; p < passedDBVals.length; p++) {
					passedDBVals[p] = Integer.toString(e[p].getEventID());
					passedDBKeys[p] = DBContract.COL_EVENT_ID;
				}
				
				i = new Intent(this, EventsActivity.class);
				i.putExtra(PARENTS_EXTRA, passedParents);
				i.putExtra(DB_VALUES_EXTRA, passedDBVals);
				i.putExtra(DB_KEYS_EXTRA, passedDBKeys);
				startActivity(i);
				
				break;
				
			case PICTURES_ID:
				
				break;
		}
	}

}
