package com.team2052.frckrawler;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.MyButton;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;

public class UsersActivity extends TabActivity implements OnClickListener{
	
	private DBManager dbManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		
		((Button)findViewById(R.id.addUser)).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		new GetUsersTask().execute();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		setNoRootActivitySelected();
	}
	
	public void postResults(User[] users) {
		
		TableLayout table = (TableLayout)findViewById(R.id.usersDataTable);
		table.removeAllViews();
		
		for(int i = 0; i < users.length; i++) {
			
			int color = Color.TRANSPARENT;
			
			if(i % 2 != 0)
				color = GlobalValues.ROW_COLOR;
			
			table.addView(new MyTableRow(this, new View[] {
					new MyButton(this, "Edit User", this, Integer.valueOf(users[i].getID())),
					new MyTextView(this, users[i].getName(), 18)
			}, color));
		}
	}
	
	/*****
	 * Method: onClick
	 * 
	 * Summary: This method is run when the user clicks a button.
	 * It should not be called by the programmer unless he or she
	 * is making a new implementation of Button.
	 *****/
	
	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.addUser) {
			
			Intent i = new Intent(this, AddUserDialogActivity.class);
			startActivity(i);
			
		} else {
			
			Intent i = new Intent(this, EditUserDialogActivity.class);
			i.putExtra(EditUserDialogActivity.USER_ID_EXTRA, ((Integer)v.getTag()).toString());
			startActivity(i);
		}
	}
	
	private class GetUsersTask extends AsyncTask<Void, Void, User[]> {
		
		@Override
		protected User[] doInBackground(Void... params) {
			return dbManager.getAllUsers();
		}
		
		@Override
		protected void onPostExecute(User[] users) {
			((TextView)findViewById(R.id.usersNum)).setText(users.length + " Users");
			postResults(users);
		}
	}
}
