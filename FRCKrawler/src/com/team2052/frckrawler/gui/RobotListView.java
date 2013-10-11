package com.team2052.frckrawler.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.List;
import com.team2052.frckrawler.database.structures.Robot;

public class RobotListView extends FrameLayout implements OnClickListener {

	private static final int RADIO_BUTTON_ID = 1;
	
	private int selectedRobotID;
	private DBManager dbManager;
	private List list;
	private TableLayout table;
	private AbstractRadioGroup radioGroup;
	
	public RobotListView(Context _context, List _list) {
		super(_context);
		inflate(_context, R.layout.widget_robot_list_view, this);
		
		selectedRobotID = -1;
		dbManager = DBManager.getInstance(_context);
		list = _list;
		table = (TableLayout)findViewById(R.id.listTable);
		
		
		((TextView)findViewById(R.id.listName)).setText(list.getName());
		((TextView)findViewById(R.id.listDescription)).setText(list.getDescription());
		findViewById(R.id.moveUp).setOnClickListener(this);
		findViewById(R.id.moveDown).setOnClickListener(this);
		findViewById(R.id.addRobotToList).setOnClickListener(this);
		findViewById(R.id.removeRobot).setOnClickListener(this);
		
		refresh(false);
	}
	
	public List getList() {
		return list;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.moveUp:
				
				if(radioGroup.getSelectedButton() == null)
					break;
				
				int robot1Pos = -1;
				for(int i = 0; i < list.getRobots().length; i++) {
					if(((Integer)radioGroup.getSelectedButton().getTag()).intValue() 
							== list.getRobots()[i].getID()) {
						robot1Pos = i;
						break;
					}
				}
				
				if(robot1Pos <= 0)
					break;
				
				dbManager.flipRobotPositionInList(list.getListID(), 
						(Integer)radioGroup.getSelectedButton().getTag(), 
						list.getRobots()[robot1Pos - 1].getID());
				refresh(true);
				break;
				
			case R.id.moveDown:
				if(radioGroup.getSelectedButton() == null)
					break;
				
				int robotPos = -1;
				for(int i = 0; i < list.getRobots().length; i++) {
					if(((Integer)radioGroup.getSelectedButton().getTag()).intValue() == 
							list.getRobots()[i].getID()) {
						robotPos = i;
						break;
					}
				}
				
				if(robotPos == list.getRobots().length - 1 || robotPos == -1)
					break;
				
				dbManager.flipRobotPositionInList(list.getListID(), 
						(Integer)radioGroup.getSelectedButton().getTag(), 
						list.getRobots()[robotPos + 1].getID());
				refresh(true);
				break;
				
			case R.id.addRobotToList:
				final Robot[] robots = dbManager.getRobotsAtEvent(list.getEventID());
				final CharSequence[] teamNumbers = new CharSequence[robots.length];
				
				for(int i = 0; i < robots.length; i++)
					teamNumbers[i] = Integer.toString(robots[i].getTeamNumber());
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setTitle("Add Robot...");
				builder.setItems(teamNumbers, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(dbManager.addRobotToList(robots[which].getID(), list.getListID())) {
							refresh(true);
							
						} else {
							Toast.makeText(getContext(), "Could not add. " +
									"Robot is already on this list.", Toast.LENGTH_LONG).show();
						}
					}
				});
				builder.show();
				
				break;
				
			case R.id.removeRobot:
				if(radioGroup.getSelectedButton() != null) {
					dbManager.removeRobotFromList((Integer)radioGroup.
							getSelectedButton().getTag(), list.getListID());
					refresh(true);
				} else {
					Toast.makeText(getContext(), "Could not remove robot. No " +
							"robot selected.", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case RADIO_BUTTON_ID:
				radioGroup.selectButton((RadioButton)v);
				selectedRobotID = (Integer)v.getTag();
				break;
		}
	}
	
	private void refresh(boolean fromDB) {
		if(fromDB) {
			List[] listArr = dbManager.getListsByColumns(
					new String[] {DBContract.COL_LIST_ID}, 
					new String[] {Integer.toString(list.getListID())});
			
			if(listArr.length > 0)
				list = listArr[0];
			
			((TextView)findViewById(R.id.listName)).setText(list.getName());
			((TextView)findViewById(R.id.listDescription)).setText(list.getDescription());
		}
		
		Robot[] robots = list.getRobots();
		radioGroup = new AbstractRadioGroup();
		table.removeAllViews();
		
		for(int i = 0; i < robots.length; i++) {
			MyTableRow row = new MyTableRow(getContext());
			if(i % 2 != 0)
				row.setBackgroundColor(GlobalValues.ROW_COLOR);
			
			RadioButton radButton = new RadioButton(getContext());
			radButton.setOnClickListener(this);
			radButton.setId(RADIO_BUTTON_ID);
			radButton.setTag(Integer.valueOf(robots[i].getID()));
			radioGroup.add(radButton);
			row.addView(radButton);
			if(robots[i].getID() == selectedRobotID)
				radioGroup.selectButton(radButton);
			
			row.addView(new MyTextView(getContext(), Integer.toString(robots[i].getTeamNumber()), 
					18));
			
			table.addView(row);
		}
	}
}
