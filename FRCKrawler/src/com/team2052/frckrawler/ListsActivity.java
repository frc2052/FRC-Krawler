package com.team2052.frckrawler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.List;
import com.team2052.frckrawler.gui.RobotListView;

public class ListsActivity extends StackableTabActivity implements OnClickListener {
	
	public static final int REQUEST_REFRESH = 1;
	public static final int REQUEST_NO_REFRESH = 2;
	public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";
	
	private int eventID;
	private DBManager dbManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists);
		
		eventID = getIntent().getIntExtra(EVENT_ID_EXTRA, -1);
		dbManager = DBManager.getInstance(this);
		
		findViewById(R.id.addList).setOnClickListener(this);
		
		new GetListsTask().execute();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.addList) {
			Intent i = new Intent(this, AddListDialogActivity.class);
			i.putExtra(AddListDialogActivity.EVENT_ID_EXTRA, eventID);
			startActivityForResult(i, AddListDialogActivity.REQUEST_REFRESH_CODE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == REQUEST_REFRESH) {
			new GetListsTask().execute();
		}
	}
	
	
	/*****
	 * Class: GetListsTask
	 * 
	 * @author Charles Hofer
	 *
	 * Description: Gets the lists from the database and puts it into the GUI
	 *****/
	
	private class GetListsTask extends AsyncTask<Void, RobotListView, Void> {
		
		private int numOfLists;
		private LinearLayout listContainor;
		
		@Override
		protected void onPreExecute() {
			numOfLists = 0;
			listContainor = (LinearLayout)findViewById(R.id.robotListContainor);
			listContainor.removeAllViews();
		}

		@Override
		protected Void doInBackground(Void... v) {
			List[] lists = dbManager.getListsByColumns(
					new String[] {DBContract.COL_EVENT_ID}, 
					new String[] {Integer.toString(eventID)});
			
			for(int i = 0; i < lists.length; i++) {
				publishProgress(new RobotListView(ListsActivity.this, lists[i]));
			}
			
			numOfLists = lists.length;
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(RobotListView... r) {
			listContainor.addView(r[0]);
			
			final int listID = r[0].getList().getListID();
			Button removeButton = new Button(ListsActivity.this);
			removeButton.setText("Remove List");
			removeButton.setLayoutParams(new LinearLayout.LayoutParams
					(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			removeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dbManager.removeList(listID);
					new GetListsTask().execute();
				}
			});
			listContainor.addView(removeButton);
		}
		
		@Override
		protected void onPostExecute(Void v) {
			((TextView)findViewById(R.id.listNumber)).setText(numOfLists + " Lists");
		}
	}
}
