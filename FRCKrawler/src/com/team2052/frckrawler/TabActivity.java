package com.team2052.frckrawler;

/*****
 * Class: TabActivity
 * 
 * Summary: This class takes care of the tabs and their selection on the side of a
 * superuser's main activities. To make a new tab, the programmer must add a button,
 * a selection integer, a listener for the button, and the action of what to do when
 * it is pressed.
 *****/

import com.example.frckrawler.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TabActivity extends Activity {
	
	private static int selectedActivity = 0;
	
	protected TabListener listener;
	
	/*****
	 * Method: setContentView
	 * 
	 * Summary: This method overrides Activity's setContentView method so that it can put
	 * listeners on the tab buttons. 
	 *****/
	
	public void setContentView(int layoutResID) {
		
		super.setContentView(layoutResID);
		
		listener = new TabListener(this);
		
		try{
			((Button)findViewById(R.id.teamsSelectionButton)).setOnClickListener(listener);
			((Button)findViewById(R.id.usersSelectionButton)).setOnClickListener(listener);
			((Button)findViewById(R.id.gamesSelectionButton)).setOnClickListener(listener);
			((Button)findViewById(R.id.eventsSelectionButton)).setOnClickListener(listener);
			
		} catch(NullPointerException e) {
			
			System.out.println("Error: The tab listeners were not created. " +
					"The given layout did not have the proper IDs.");
		}
	}
	
	
	/*****
	 * Nested Class: TabListener
	 * 
	 * Summary: This class is used by the TabActivity as a listener. TabActivity uses
	 * this class rather than implementing OnClickListener directly so that children
	 * of TabActivity can implement TabActivity without worrying about calling super
	 * or overriding onClick improperly.
	 *****/

	protected class TabListener implements OnClickListener {
	
		public static final int TEAMS = 0;
		public static final int USERS = 1;
		public static final int GAMES = 2;
		public static final int EVENTS = 3;
	
		private Activity user;
	
		public TabListener(Activity _user) {
		
			user = _user;
		}
	
		public void onClick(View v) {
		
			Intent i;
		
			switch (v.getId()) {
				case R.id.teamsSelectionButton :
				
					if(selectedActivity != TEAMS) {
					
						i = new Intent(user, TeamsActivity.class);
						user.startActivity(i);
						selectedActivity = TEAMS;
						user.finish();
					}
				
					break;
				
				case R.id.usersSelectionButton :
				
					if(selectedActivity != USERS) {
					
						i = new Intent(user, UsersActivity.class);
						user.startActivity(i);
						selectedActivity = USERS;
						user.finish();
					}
				
					break;
				
				case R.id.gamesSelectionButton :
				
					if(selectedActivity != GAMES) {
					
						i = new Intent(user, GamesActivity.class);
						user.startActivity(i);
						selectedActivity = GAMES;
						user.finish();
					}
				
					break;
				
				case R.id.eventsSelectionButton :
				
					if(selectedActivity != EVENTS) {
					
						i = new Intent(user, EventsActivity.class);
						user.startActivity(i);
						selectedActivity = EVENTS;
						user.finish();
					}
				
					break;
			}
		}
	}
}
