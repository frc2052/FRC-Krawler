package com.team2052.frckrawler;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.List;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.SortKey;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;
import com.team2052.frckrawler.gui.StaticTableLayout;

public class QueryActivity extends StackableTabActivity implements OnClickListener {
	
	public static final int REQUEST_REFRESH = 1;
	public static final int REQUEST_NO_REFRESH = 2;
	
	private static final int COMMENT_BUTTON_ID = 1;
	private static final int PICTURE_BUTTON_ID = 2;
	private static final int MATCH_DATA_BUTTON_ID = 3;
	private static final int ADD_TO_LIST_BUTTON_ID = 4;
	private static HashMap<Integer, Query[]> matchQuerys = new HashMap<Integer, Query[]>();
	private static HashMap<Integer, Query[]> pitQuerys = new HashMap<Integer, Query[]>();
	private static HashMap<Integer, Query[]> driverQuerys = new HashMap<Integer, Query[]>();
	private static SortKey sortKey;
	
	private CompiledData[] data;
	private DBManager dbManager;
	private GetCompiledDataTask getDataTask;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);
		 
		findViewById(R.id.query).setOnClickListener(this);
		findViewById(R.id.lists).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
		
		getDataTask = new GetCompiledDataTask();
		getDataTask.execute(this);
	}
	
	public void onStop() {
		super.onStop();
		getDataTask.cancel(true);
	}
	
	public void onClick(View v) {
		Intent i;
		AlertDialog.Builder builder;
		
		switch(v.getId()) {
			case R.id.query:
				
				i = new Intent(this, QuerySortingDialogActivity.class);
				i.putExtra(QuerySortingDialogActivity.EVENT_ID_EXTRA, 
						databaseValues[getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]);
				startActivityForResult(i, 1);
				break;
				
			case R.id.lists:
				
				i = new Intent(this, ListsActivity.class);
				i.putExtra(ListsActivity.EVENT_ID_EXTRA, 
						Integer.parseInt(databaseValues
								[getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]));
				startActivity(i);
				break;
				
			case COMMENT_BUTTON_ID:
				
				i = new Intent(this, CommentDialogActivity.class);
				i.putExtra(CommentDialogActivity.COMMENT_ARRAY_EXTRA, 
						data[(Integer)v.getTag()].getMatchComments());
				i.putExtra(CommentDialogActivity.MATCHES_ARRAY_EXTRA, 
						data[(Integer)v.getTag()].getMatchesPlayed());
				startActivity(i);
				break;
				
			case PICTURE_BUTTON_ID:
				
				Robot r = data[(Integer)v.getTag()].getRobot();
				String imagePath = r.getImagePath();
				
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Team " + r.getTeamNumber() + "'s Robot");
				
				if(imagePath != null && !imagePath.equals("")) {
					ImageView image = new ImageView(this);
					image.setImageURI(Uri.parse(imagePath));
					builder.setView(image);
					
				} else {
					builder.setView(new MyTextView(this, "No image for this team.", 18));
				}
				
				builder.show();
				break;
				
			case MATCH_DATA_BUTTON_ID:
				
				i = new Intent(this, RawMatchDataActivity.class);
				i.putExtra(PARENTS_EXTRA, new String[] {});
				i.putExtra(DB_VALUES_EXTRA, new String[] {Integer.toString(data[(Integer)v.
				                                                 getTag()].getRobot().getID())});
				i.putExtra(DB_KEYS_EXTRA, new String[] {DBContract.COL_ROBOT_ID});
				i.putExtra(RawMatchDataActivity.DISABLE_BUTTONS_EXTRA, true);
				startActivity(i);
				break;
				
			case ADD_TO_LIST_BUTTON_ID:
				
				final int robotID = (Integer)v.getTag();
				final List[] lists = dbManager.getListsByColumns(
						new String[] {DBContract.COL_EVENT_ID}, 
						new String[] {(databaseValues[getAddressOfDatabaseKey
						                              (DBContract.COL_EVENT_ID)])});
				final CharSequence[] choices = new CharSequence[lists.length];
				
				for(int k = 0; k < lists.length; k++) {
					choices[k] = lists[k].getName();
				}
				
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Add to List...");
				builder.setItems(choices, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!dbManager.addRobotToList(robotID, lists[which].getListID())) {
							Toast.makeText(QueryActivity.this, "Robot is already in " +
									"that list.", Toast.LENGTH_SHORT).show();
						}
						
						dialog.dismiss();
					}
				});
				builder.show();
				break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == REQUEST_REFRESH)
			new GetCompiledDataTask().execute(this);
	}
	
	public static void setQuery(int eventID, Query[] match, Query[] pit, 
			Query[] driver) {
		matchQuerys.put(eventID, match);
		pitQuerys.put(eventID, pit);
		driverQuerys.put(eventID, driver);
	}
	
	public static Query[] getMatchQuerys(int eventID) {
		return matchQuerys.get(eventID);
	}
	
	public static Query[] getPitQuerys(int eventID) {
		return pitQuerys.get(eventID);
	}
	
	public static Query[] getDriverQuerys(int eventID) {
		return driverQuerys.get(eventID);
	}
	
	public static void setSortKey(SortKey key) {
		sortKey = key;
	}
	
	public static SortKey getSortKey() {
		return sortKey;
	}
	
	private class GetCompiledDataTask extends AsyncTask
										<QueryActivity, MyTableRow, Void> {
		
		private int dataNum;
		private StaticTableLayout table;
											
		protected void onPreExecute() {
			dataNum = 0;
			((FrameLayout)findViewById(R.id.progressFrame)).
					addView(new ProgressSpinner(getApplicationContext()));
			
			table = (StaticTableLayout)findViewById(R.id.queryTable);
			table.removeAllViews();
		}

		protected Void doInBackground(QueryActivity... params) {
			QueryActivity activity = params[0];
			
			ArrayList<Query> allQuerys = new ArrayList<Query>();
			Query[] matchQuerys = getMatchQuerys(Integer.parseInt
					(databaseValues[getAddressOfDatabaseKey
					                (DBContract.COL_EVENT_ID)]));
			Query[] pitQuerys = getPitQuerys(Integer.parseInt
					(databaseValues[getAddressOfDatabaseKey
					                (DBContract.COL_EVENT_ID)]));
			Query[] driverQuerys = getDriverQuerys(Integer.parseInt
					(databaseValues[getAddressOfDatabaseKey
					                (DBContract.COL_EVENT_ID)]));
			
			if(matchQuerys != null)
				for(Query q : matchQuerys)
					allQuerys.add(q);
			if(pitQuerys != null)
				for(Query q : pitQuerys)
					allQuerys.add(q);
			if(driverQuerys != null)
				for(Query q : driverQuerys)
					allQuerys.add(q);
					
			Query[] querys = allQuerys.toArray(new Query[0]);
			
			if(querys.length == 0) {
				data = dbManager.getCompiledEventData
					(Integer.parseInt(databaseValues[
					getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]), 
					new Query[0], sortKey);
				
			} else {
				try {
					data = dbManager.getCompiledEventData
							(Integer.parseInt(databaseValues[
							getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]), 
							querys, sortKey);
					
				} catch(NullPointerException e) {
					data = dbManager.getCompiledEventData
							(Integer.parseInt(databaseValues[
							getAddressOfDatabaseKey(DBContract.COL_EVENT_ID)]), 
							new Query[0], sortKey);
				}
			}
			
			MyTableRow staticDescriptorsRow = new MyTableRow(activity);
			MyTableRow descriptorsRow = new MyTableRow(activity);
			staticDescriptorsRow.addView(new MyTextView(activity, " ", 18));
			staticDescriptorsRow.addView(new MyTextView(activity, "Team", 18));
			descriptorsRow.addView(new MyTextView(activity, "M. Played", 18));
			descriptorsRow.addView(new MyTextView(activity, "Comments", 18));
			descriptorsRow.addView(new MyTextView(activity, "Pictures", 18));
			descriptorsRow.addView(new MyTextView(activity, "M. Data", 18));
			descriptorsRow.addView(new MyTextView(activity, "Lists", 18));
			
			MetricValue[] matchMetrics;
			Metric[] robotMetrics;
			MetricValue[] driverMetrics;
			
			if(data.length > 0) {
				matchMetrics = data[0].getCompiledMatchData();
				robotMetrics = data[0].getRobot().getMetrics();
				driverMetrics = data[0].getCompiledDriverData();
			} else {
				matchMetrics = new MetricValue[0];
				robotMetrics = new Metric[0];
				driverMetrics = new MetricValue[0];
			}
			
			//Add all the metric names to the descriptors row, but 
			//only if they are displayed.
			for(MetricValue m : matchMetrics)
				if(m.getMetric().isDisplayed())
					descriptorsRow.addView(new MyTextView(activity, 
						m.getMetric().getMetricName(), 18));
			
			for(Metric m : robotMetrics)
				if(m.isDisplayed())
					descriptorsRow.addView(new MyTextView(activity, 
							m.getMetricName(), 18));
			
			for(MetricValue m : driverMetrics)
				if(m.getMetric().isDisplayed())
					descriptorsRow.addView(new MyTextView(activity, 
						m.getMetric().getMetricName(), 18));
			
			publishProgress(staticDescriptorsRow, descriptorsRow);
			
			//Create a new row for each piece of data
			for(int dataCount = 0; dataCount < data.length; dataCount++) {
				if(isCancelled()) {
					break;
				}
				
				int color;
				int buttonColor;
				
				if(dataCount % 2 == 0) {
					color = GlobalSettings.ROW_COLOR;
					buttonColor = GlobalSettings.BUTTON_COLOR;
				} else {
					color = Color.TRANSPARENT;
					buttonColor = Color.rgb(30, 30, 30);
				}
				
				MyTableRow staticRow = new MyTableRow(activity, color);
				MyTableRow dataRow = new MyTableRow(activity, color);
				
				CheckBox checkBox = new CheckBox(activity);
				checkBox.setOnCheckedChangeListener(new CheckListener(
						data[dataCount].getEventID(), 
						data[dataCount].getRobot().getID()));
				checkBox.setChecked(data[dataCount].getRobot().isChecked());
				staticRow.addView(checkBox);
				
				MyButton commentsButton = new MyButton
						(activity, "Comments", activity);
				commentsButton.setId(COMMENT_BUTTON_ID);
				commentsButton.setTag(Integer.valueOf(dataCount));
				
				MyButton picturesButton = new MyButton
						(activity, "Pictures", activity);
				picturesButton.setId(PICTURE_BUTTON_ID);
				picturesButton.setTag(Integer.valueOf(dataCount));
				
				MyButton matchDataButton = new MyButton
						(activity, "M. Data", activity);
				matchDataButton.setId(MATCH_DATA_BUTTON_ID);
				matchDataButton.setTag(Integer.valueOf(dataCount));
				
				MyButton addToListButton = new MyButton(
						activity, "Add To List", activity);
				addToListButton.setId(ADD_TO_LIST_BUTTON_ID);
				addToListButton.setTag((Integer)data[dataCount].getRobot().getID());
				
				staticRow.addView(new MyTextView(activity, Integer.toString(
						data[dataCount].getRobot().getTeamNumber()), 18));
				dataRow.addView(new MyTextView(activity, Integer.toString(
						data[dataCount].getMatchesPlayed().length), 18));
				dataRow.addView(commentsButton);
				dataRow.addView(picturesButton);
				dataRow.addView(matchDataButton);
				dataRow.addView(addToListButton);
				
				//Get the data arrays for the robot, matches, and driver data
				MetricValue[] matchData = data[dataCount].getCompiledMatchData();
				MetricValue[] robotData = data[dataCount].getRobot().
						getMetricValues();
				MetricValue[] driverData = data[dataCount].getCompiledDriverData();
				
				for(int i = 0; i < matchData.length; i++) {
					if(matchData[i].getMetric().isDisplayed()) {
						if(matchData[i].getMetric().getType() != DBContract.CHOOSER) {
							dataRow.addView(new MyTextView(activity, 
									matchData[i].getValueAsHumanReadableString(), 18));
							
						} else {
							boolean isNumeric = true;
							
							try {
								for(int choiceCount = 0; choiceCount < matchData[i].
										getMetric().getRange().length; 
										choiceCount++) {
									Double.parseDouble((String)matchData[i].getMetric().
											getRange()[choiceCount]);
								}
							} catch(NumberFormatException e) {
								isNumeric = false;
							}

							if(isNumeric) {
								dataRow.addView(new MyTextView(QueryActivity.this, 
										matchData[i].getValueAsHumanReadableString(), 18));

							} else {
								int mostPickedAddress = 0;
								int mostPickedCounts = 0;

								for(int k = 0; k < matchData[i].getChooserCounts().length; k++) {
									if(matchData[i].getChooserCounts()[k] > mostPickedCounts) {
										mostPickedAddress = k;
										mostPickedCounts = matchData[i].getChooserCounts()[k];
									}
								}

								Button chooserButton = new Button(QueryActivity.this);

								if(matchData[i].getValue().length > 0)
									chooserButton.setText(matchData[i].getValue()[mostPickedAddress]);
								else
									chooserButton.setText("");

								chooserButton.setBackgroundColor(buttonColor);
								chooserButton.setTextColor(Color.LTGRAY);
								chooserButton.setTextSize(18);

								final MetricValue finalVal = matchData[i];
								final int teamNumber = data[dataCount].getRobot().getTeamNumber();
								chooserButton.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										AlertDialog.Builder builder = 
												new AlertDialog.Builder(QueryActivity.this);
										builder.setTitle("Team " + 
												teamNumber + "'s " + 
												finalVal.getMetric().getMetricName() + " Data");

										LinearLayout builderView = new LinearLayout
												(QueryActivity.this);
										builderView.setOrientation(LinearLayout.VERTICAL);
										for(int i = 0; i < finalVal.getValue().length; i++) {
											builderView.addView(new MyTextView(
													QueryActivity.this,
													finalVal.getValue()[i],
													18));
										}

										builder.setView(builderView);
										builder.setNeutralButton("Close", 
												new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
											}
										});
										builder.show();
									}
								});
								dataRow.addView(chooserButton);
							}
						}
					}
				}
				
				for(int i = 0; i < robotData.length; i++)
					if(robotData[i].getMetric().isDisplayed())
						dataRow.addView(new MyTextView(activity, 
								robotData[i].getValueAsHumanReadableString(), 18));
				
				for(int i = 0; i < driverData.length; i++)
					if(driverData[i].getMetric().isDisplayed())
						dataRow.addView(new MyTextView(activity, 
								driverData[i].getValueAsHumanReadableString(), 18));
				
				publishProgress(staticRow, dataRow);
				
				try {	//Wait for the UI to update
					Thread.sleep(150);
				} catch(InterruptedException e) {}
			}
			
			dataNum = data.length;
			return null;
		}
		
		protected void onProgressUpdate(MyTableRow... rows) {
			if(rows.length > 1) {
				table.addViewToStaticTable(rows[0]);
				table.addViewToMainTable(rows[1]);
				
			} else {
				table.addViewToMainTable(rows[0]);
			}
		}
		
		protected void onPostExecute(Void v) {
			((TextView)findViewById(R.id.compiledNumber)).setText(dataNum + " Robots");
			((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
		}
		
		protected void onCancelled(Void v) {
			((FrameLayout)findViewById(R.id.progressFrame)).removeAllViews();
		}
	}
	
	private class CheckListener implements CompoundButton.OnCheckedChangeListener {
		
		private int eventID;
		private int robotID;
		
		public CheckListener(int _eventID, int _robotID) {
			eventID = _eventID;
			robotID = _robotID;
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			dbManager.setRobotChecked(eventID, robotID, isChecked);
		}
	}
}
