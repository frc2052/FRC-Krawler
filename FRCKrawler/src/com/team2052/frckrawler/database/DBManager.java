package com.team2052.frckrawler.database;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.team2052.frckrawler.OptionsActivity;
import com.team2052.frckrawler.database.structures.Comment;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Contact;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.database.structures.List;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.MetricValue.MetricTypeMismatchException;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.SortKey;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.database.structures.User;

/*****
 * Class: DatabaseManager
 * 
 * Summary: A DatabaseManager handles all operations that could be done on the SQLite
 * database and the pictures in the file system. This class is a singleton. This gives
 * a global access point for the database. There is only one database in an
 * FRCKrawler app, so only one manager is needed. Methods in this class that access
 * the database should be declared 'synchronized'.
 * 
 * Components of the the FRCKrawler app should interface with this class, rather than
 * a DatabaseHelper directly.
 * 
 * @author Charles Hofer
 *****/

public class DBManager {
	
	private static DBManager instance = null;
	private DBHelper helper;
	private Context context;
	
	/*****
	 * Constructor
	 * 
	 * @param _context
	 * 
	 * Summary: This constructor is private because this class is a singleton. No other class should ever call
	 * the constructor. Calls should be made to getInstance() instead.
	 *****/
	
	private DBManager(Context _context) {
		
		//calling getApplicationContext() assures that no resources are held on to that should have been released.
		context = _context.getApplicationContext();
		helper = new DBHelper(context, new File
				(context.getFilesDir().getPath(), DBContract.DATABASE_NAME).getPath());
	}
	
	
	/*****
	 * Method: getInstance()
	 * 
	 * @param _context
	 * 
	 * Summary: Returns an instance of the DatabaseManager. This should always be
	 * called instead of the constructor.
	 *****/
	
	public static DBManager getInstance(Context _context) {
		if(instance == null){	//If a manager has not been created yet
			instance = new DBManager(_context.getApplicationContext());
		}
		return instance;
	}
	
	
	/*****
	 * Method: addUser
	 * 
	 * @param name
	 * @param superuser
	 * 
	 * Summary: Adds a user to the database. You can also specify if this user is
	 * a superuser or not.
	 *****/
	
	public void addUser(User user) {
		addUser(user.getName(), user.isSuperuser());
	}
	
	public synchronized void addUser(String name, boolean superuser) {
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_USER_ID, createID(DBContract.TABLE_USERS, 
				DBContract.COL_USER_ID));
		values.put(DBContract.COL_USER_NAME, name);
		values.put(DBContract.COL_SUPERUSER, superuser);
		
		helper.getWritableDatabase().insert(DBContract.TABLE_USERS, null, values);
		
		helper.close();
	}
	
	
	/*****
	 * Method: removeUser
	 * 
	 * @param id
	 * 
	 * Summary: Removes a user from the database based on their ID. You may not remove
	 * users by name because the database allows for multiple users to have the same
	 * name.
	 *****/
	
	public void removeUser(User user) {
		removeUser(user.getID());
	}
	
	public synchronized void removeUser(int id) {
		helper.getWritableDatabase().delete(DBContract.TABLE_USERS, 
				DBContract.COL_USER_ID + " LIKE ?", new String[] {Integer.toString(id)});
		
		helper.close();
	}
	
	
	/*****
	 * Method: getAllUsers
	 * 
	 * Summary: Gets all users from the database and returns them to an array.
	 */
	
	public synchronized User[] getAllUsers() {
		Cursor c = helper.getReadableDatabase().rawQuery(
				"SELECT * FROM " + DBContract.TABLE_USERS, null);
		User[] u = new User[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			u[i] = new User(
					c.getString(c.getColumnIndex(DBContract.COL_USER_NAME)),
					c.getInt(c.getColumnIndex(DBContract.COL_SUPERUSER)),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID))
					);
		}
		
		c.close();
		helper.close();
		return u;
	}
	
	
	/*****
	 * Method: getUsersByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * Summary: This method gets all the users where the columns in the cols array
	 * equals the value in the vals array. The addresses of the strings must match up.
	 * So, cols[0] = 'name' and vals[0] = 'Bob' gets all the users with the name
	 * Bob.
	 */
	
	public synchronized User[] getUsersByColumns(String[] cols, String[] vals) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1 || vals.length < 1)
			return new User[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_USERS + 
				" WHERE " + cols[0] + " LIKE ?";
		
		for(int i = 1; 1 < cols.length; i++)
			queryString += " AND " + cols[i] + " LIKE ?";
		
		queryString += " ORDER BY " + DBContract.COL_USER_NAME + " ASC";
		
		Cursor c = helper.getReadableDatabase().rawQuery(queryString, vals);
		User[] u = new User[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			u[i] = new User(
					c.getString(c.getColumnIndex(DBContract.COL_USER_NAME)),
					Boolean.getBoolean(c.getString(c.getColumnIndex(DBContract.COL_SUPERUSER))),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID))
					);
		}
		
		return u;
	}
	
	/*****
	 * Method: updateUsers
	 * 
	 * @param queryCol
	 * @param queryVals
	 * @param updateCols
	 * @param updateVals
	 * @return
	 * 
	 * Summary: Sets the columns in the updateCols array equal to the values in the 
	 * updateVals array where the columns in the queryCols array equals the values
	 * in the queryVals array.
	 ******/
	
	public synchronized boolean updateUsers(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)	//Same here
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the user's id.
			if(s.equals(DBContract.COL_USER_ID))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateCols.length; i++)
			vals.put(updateCols[i], updateVals[i]);
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) {
			queryString += " AND " + queryCols[i] + " LIKE ?";
		}
		
		helper.getWritableDatabase().update(DBContract.TABLE_USERS, vals, 
				queryString, queryVals);
		helper.close();
		return true;
	}
	
	/*****
	 * Method: setSuperuser
	 * 
	 * @param user
	 * @param superuser
	 * 
	 * Summary: This sets whether the passed User parameter is a superuser
	 * or not.
	 *****/
	
	public synchronized void setSuperuer(String username, boolean superuser) {
		ContentValues vals = new ContentValues();
		vals.put(DBContract.COL_SUPERUSER, superuser);
		
		helper.getWritableDatabase().update(DBContract.TABLE_USERS, 
				vals, 
				DBContract.COL_USER_NAME + " LIKE ?", 
				new String[] {username});
		
		helper.close();
	}
	
	
	/*****
	 * Method: addTeam
	 * 
	 * @param number
	 * @param name
	 * @param school
	 * @param city
	 * @param rookieYear
	 * @param website
	 * @param statePostalCode
	 * @param colors
	 * 
	 * Summary: Adds a team to the database based on the parameters passed. The
	 * team number passed must be a number greater than or equal to zero. All 
	 * other values may be passed as null if so desired.
	 * 
	 * @return True if the team was added successfully. False if the team
	 * number was less than zero, or if the team number is already taken.
	 */
	public boolean addTeams(Team[] t) {
		boolean allAdded = true;
		
		for(int i = 0; i < t.length; i++) {
			
			if(!addTeam(t[i]))
				allAdded = false;
		}
		
		return allAdded;
	}
	
	public boolean addTeam(Team t) {
		return addTeam(t.getNumber(), t.getName(), t.getSchool(), t.getCity(), 
					t.getRookieYear(), t.getWebsite(), t.getStatePostalCode(), t.getColors());
	}
	
	public synchronized boolean addTeam(int number, String name, String school, String city,
			int rookieYear, String website, String statePostalCode, String colors) {
		if(number < 0)
			return false;
		
		//If there is not already a team with that number...
		if(!hasValue(DBContract.TABLE_TEAMS, 
				DBContract.COL_TEAM_NUMBER, Integer.toString(number))) {
			
			SQLiteDatabase db = helper.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(DBContract.COL_TEAM_NUMBER, number);
			values.put(DBContract.COL_TEAM_NAME, name);
			values.put(DBContract.COL_SCHOOL, school);
			values.put(DBContract.COL_CITY, city);
			values.put(DBContract.COL_ROOKIE_YEAR, rookieYear);
			values.put(DBContract.COL_WEBSITE, website);
			values.put(DBContract.COL_STATE_POSTAL_CODE, statePostalCode);
			values.put(DBContract.COL_COLORS, colors);
		
			db.insert(DBContract.TABLE_TEAMS, null, values);	//Add it to the database
			helper.close();
			
			SharedPreferences prefs = context.getSharedPreferences
					(OptionsActivity.PREFS_NAME, Context.MODE_PRIVATE);
			boolean generateRobot = prefs.getBoolean
					(OptionsActivity.PREFS_GENERATE_ROBOTS, false);
			String game = prefs.getString(OptionsActivity.PREFS_ROBOT_GAME, "");
			
			if(generateRobot) {
				addRobot(number, game, "", "", new MetricValue[0]);
			}
			
			return true;
		}
		
		helper.close();
		return false;
	}
	
	
	/*****
	 * Method: removeTeam
	 * 
	 * @param number
	 * 
	 * Summary: This method removes all information about a team from the database.
	 * Be very careful with this method! It deletes all match information, pit
	 * information, and any data associated with this team.
	 *****/
	
	public void removeTeam(Team t) {
		removeTeam(t.getNumber());
	}
	
	public synchronized void removeTeam(int number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_ROBOTS, 		//Find the team's robots
				new String[] {DBContract.COL_ROBOT_ID}, 
				DBContract.COL_TEAM_NUMBER + " LIKE ?", 
				new String[] {Integer.toString(number)},
				null, null, null);
		
		while(c.moveToNext()) {	//While this team still has robots...
			String[] value = {c.getString(c.getColumnIndex(DBContract.COL_ROBOT_ID))};
			db.delete(DBContract.TABLE_MATCH_PERF, DBContract.COL_ROBOT_ID + " LIKE ?", 
					value);
			
			//Get pictures from the file system based on robot ids and delete them
		}
		
		String[] value = {Integer.toString(number)};
		db.delete(DBContract.TABLE_CONTACTS, DBContract.COL_TEAM_NUMBER + " LIKE ?", value);
		db.delete(DBContract.TABLE_ROBOTS, DBContract.COL_TEAM_NUMBER + " LIKE ?", value);
		db.delete(DBContract.TABLE_COMMENTS, DBContract.COL_TEAM_NUMBER + " LIKE ?", value);
		db.delete(DBContract.TABLE_TEAMS, DBContract.COL_TEAM_NUMBER + " LIKE ?", value);
		helper.close();
	}
	
	
	/*****
	 * Method: getAllTeams
	 * 
	 * Summary: Returns an array all teams in the database. The data is stored
	 * in an array of Team objects.
	 *****/
	
	public synchronized Team[] getAllTeams() {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery(DBContract.SELECT_ALL_TEAM_DATA, null);
		Team[] t = new Team[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			t[i] = new Team(c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)), 
					c.getString(c.getColumnIndex(DBContract.COL_TEAM_NAME)), 
					c.getString(c.getColumnIndex(DBContract.COL_SCHOOL)),
					c.getString(c.getColumnIndex(DBContract.COL_CITY)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROOKIE_YEAR)),
					c.getString(c.getColumnIndex(DBContract.COL_WEBSITE)),
					c.getString(c.getColumnIndex(DBContract.COL_STATE_POSTAL_CODE)),
					c.getString(c.getColumnIndex(DBContract.COL_COLORS)));
		}
		
		c.close();
		helper.close();
		return t;
	}
	
	
	/*****
	 * Method: getTeamsByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * @throws SQLiteException - if the either array is null or does meet the criteria.
	 * 
	 * Summary: This gets all teams in the database based on the cols and vals arrays. All values
	 * in the cols array should be non-null Strings from DatabaseContract under the TABLE_TEAMS.
	 * Anything that is not will throw an exception. The Strings in the vals are used as the values
	 * for the columns. So, the array returned is all teams that have the String in vals[i] in the
	 * column vals[i]. Any team that does not have all the values in the proper columns is not
	 * returned.
	 */
	public synchronized Team[] getTeamsByColumns(String[] cols, String[] vals) {
		return getTeamsByColumns(cols, vals, false);
	}
	
	
	public synchronized Team[] getTeamsByColumns(String[] cols, String[] vals, boolean isOr) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new Team[0];
		
		String logic;
		
		if(isOr)
			logic = " OR ";
		else
			logic = " AND ";
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_TEAMS + " WHERE " + cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += logic + cols[i] + " LIKE ?";
		
		queryString += " ORDER BY " + DBContract.COL_TEAM_NUMBER + " ASC";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		Team[] t = new Team[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			t[i] = new Team(c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)), 
					c.getString(c.getColumnIndex(DBContract.COL_TEAM_NAME)), 
					c.getString(c.getColumnIndex(DBContract.COL_SCHOOL)),
					c.getString(c.getColumnIndex(DBContract.COL_CITY)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROOKIE_YEAR)),
					c.getString(c.getColumnIndex(DBContract.COL_WEBSITE)),
					c.getString(c.getColumnIndex(DBContract.COL_STATE_POSTAL_CODE)),
					c.getString(c.getColumnIndex(DBContract.COL_COLORS)));
		}
		
		c.close();
		helper.close();
		return t;
	}
	
	
	/*****
	 * Method: updateTeams
	 * 
	 * Summary: Updates all teams in the database based on the queryCol 
	 * passed as a parameter and the queryVals. It inserts the updateVals
	 * WHERE the queryCol has a value of the values in queryVals
	 *****/
	
	public synchronized boolean updateTeams(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(s.equals(DBContract.COL_TEAM_NUMBER))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateCols.length; i++)
			vals.put(updateCols[i], updateVals[i]);
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) {
			queryString += " AND " + queryCols[i] + " LIKE ?";
		}
		
		helper.getWritableDatabase().update(DBContract.TABLE_TEAMS, vals, 
				queryString, queryVals);
		
		helper.close();
		return true;
	}
	
	/*****
	 * Method: addCompetition
	 * 
	 * @param name
	 * @param location
	 * @param date
	 * 
	 * Summary: Adds a competition to the database. Any of these values may be
	 * passed as null without causing an error.
	 *****/
	
	public boolean addEvent(Event e) {
		return addEvent(e.getEventName(), e.getGameName(), e.getLocation(), e.getDateStamp());
	}
	
	public synchronized boolean addEvent(String name, String gameName, 
			String location, Date date) {
		if(!hasValue(DBContract.TABLE_GAMES, DBContract.COL_GAME_NAME, gameName))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		int newID = createID(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID);
		
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_EVENT_ID, newID);
		values.put(DBContract.COL_EVENT_NAME, name);
		values.put(DBContract.COL_GAME_NAME, gameName);
		values.put(DBContract.COL_LOCATION, location);
		
		if(date != null)	//If a date has been set...
			values.put(DBContract.COL_DATE_STAMP, date.getTime());
		
		db.insert(DBContract.TABLE_EVENTS, null, values);
		
		helper.close();
		return true;
	}
	
	
	/*****
	 * Method: removeCompetition
	 * 
	 * @param eventID
	 * 
	 * Summary: Deletes a competition and all data associated with it. This includes match data,
	 * so be careful about calling this method.
	 *****/
	
	public synchronized void removeEvent(Event e) {
		removeEvent(e.getEventID());
	}
	
	public synchronized void removeEvent(int eventID) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(DBContract.TABLE_MATCH_PERF, DBContract.COL_EVENT_ID + " LIKE ?", 
				new String[] {Integer.toString(eventID)});
		db.delete(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID + " LIKE ?", 
				new String[] {Integer.toString(eventID)});
		db.delete(DBContract.TABLE_EVENT_ROBOTS, DBContract.COL_EVENT_ID + " LIKE ?", 
				new String[] {Integer.toString(eventID)});
		helper.close();
	}
	
	
	/*****
	 * Method: getAllEvents
	 * 
	 * Summary: This method gets all events out of the database
	 * and returns them to the caller in an array.
	 *****/
	
	public synchronized Event[] getAllEvents() {
		Cursor c = helper.getReadableDatabase().rawQuery(
				"SELECT * FROM " + DBContract.TABLE_EVENTS, null);
		Event[] e = new Event[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			e[i] = new Event(
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_EVENT_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					new Date(c.getLong(c.getColumnIndex(DBContract.COL_DATE_STAMP))),
					c.getString(c.getColumnIndex(DBContract.COL_LOCATION)),
					c.getString(c.getColumnIndex(DBContract.COL_FMS_EVENT_ID))
					);
		}
		
		c.close();
		helper.close();
		return e;
	}
	
	
	/*****
	 * Method: getEventsByCol
	 * 
	 * Summary: This method returns an array of events based on the 
	 * column and value arrays passed as parameters.  This method
	 * works similarly to getTeamsByCol.
	 *****/
	
	
	public synchronized Event[] getEventsByColumns(String[] cols, String[] vals) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length == 0)
			return new Event[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_EVENTS + " WHERE";
		
		if(cols.length > 0)	//Special case for first entry because it should not include the AND
			queryString += " " + cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
			
		Cursor c = helper.getReadableDatabase().rawQuery(queryString, vals);
		Event[] e = new Event[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			e[i] = new Event(
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_EVENT_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					new Date(c.getLong(c.getColumnIndex(DBContract.COL_DATE_STAMP))),
					c.getString(c.getColumnIndex(DBContract.COL_LOCATION)),
					c.getString(c.getColumnIndex(DBContract.COL_FMS_EVENT_ID))
					);
		}
		
		c.close();
		helper.close();
		return e;
	}
	
	
	/*****
	 * Method: updateEvents
	 * 
	 * @param queryCols
	 * @param queryVals
	 * @param updateCols
	 * @param updateVals
	 * 
	 * @return True if the table was updated successfully, false if the query or
	 * update arrays are different lengths or if one of the values in the updateCols
	 * array is the event id.
	 * 
	 * Summary: This method updates the Events table similarly to how teams,
	 * and users are updated.
	 *****/
	
	public synchronized boolean updateEvents(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(s.equals(DBContract.COL_EVENT_ID))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateCols.length; i++)
			vals.put(updateCols[i], updateVals[i]);
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) {
			queryString += " AND " + queryCols[i] + " LIKE ?";
		}
		
		helper.getWritableDatabase().update(DBContract.TABLE_EVENTS, vals, 
				queryString, queryVals);
		helper.close();
		return true;
	}
	
	
	/*****
	 * Method: addComment
	 * 
	 * @param teamNumber
	 * @param userID
	 * @param comment
	 * @param dateStamp
	 * 
	 * Summary: Adds a new comment to the database. You may leave the Date
	 * parameter as null if you do not wish to include a date stamp.
	 *****/
	
	public void addComment(Comment c) {
		addComment(c.getTeamNumber(), c.getEventID(), c.getUserID(), c.getText(), c.getTimeStamp());
	}
	
	public synchronized void addComment(int teamNumber, int eventID, int userID, String comment, 
			Date dateStamp) {
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_TEAM_NUMBER, teamNumber);
		values.put(DBContract.COL_EVENT_ID, eventID);
		values.put(DBContract.COL_USER_ID, userID);
		values.put(DBContract.COL_COMMENT, comment);
		
		if(dateStamp != null) {
			values.put(DBContract.COL_DATE_STAMP, dateStamp.getTime());
		}
		
		helper.getWritableDatabase().insert(DBContract.TABLE_COMMENTS, null, values);
		helper.close();
	}
	
	
	/*****
	 * Method: removeComment
	 * 
	 * @param teamNumber
	 * @param dateStamp
	 * 
	 * Summary: Removes the comment made about the passed team 
	 * parameter, and the date that it was made. This method assumes
	 * that two date comments were not made within the same minute for the
	 * same team.
	 *****/
	
	public void removeComment(Comment c) {
		removeComment(c.getTeamNumber(), c.getTimeStamp());
	}
	
	public synchronized void removeComment(int teamNumber, Date dateStamp) {
		if(dateStamp != null) {
			helper.getWritableDatabase().execSQL("DELETE FROM " + 
					DBContract.TABLE_COMMENTS + " WHERE " + 
					DBContract.COL_TEAM_NUMBER + "='" + teamNumber + 
					"' AND " + DBContract.COL_DATE_STAMP + 
					"='" + dateStamp.getTime() + "'");
		}
	}
	
	
	/*****
	 * Method: getAllComments
	 * 
	 * Summary: Returns all comments from the database in a comments array.
	 *****/
	
	public synchronized Comment[] getAllComments() {
		Cursor c = helper.getReadableDatabase().rawQuery(
				"SELECT * FROM " + DBContract.TABLE_COMMENTS, null);
		Comment[] comments = new Comment[c.getCount()];
		
		for(int i = 0; i < c.getColumnCount(); i++) {
			c.moveToNext();
			comments[i] = new Comment(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENT)),
					new Date(c.getInt(c.getColumnIndex(DBContract.COL_DATE_STAMP)))
					);
		}
		
		helper.close();
		return comments;
	}
	
	public synchronized Comment[] getCommentsByColumns(String[] cols, String[] vals) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new Comment[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_COMMENTS + " WHERE " + cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		Comment[] comments = new Comment[c.getCount()];
		
		for(int i = 0; i < comments.length; i++) {
			c.moveToNext();
			comments[i] = new Comment(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENT)),
					new Date(c.getLong(c.getColumnIndex(DBContract.COL_DATE_STAMP)))
					);
		}
		
		helper.close();
		return comments;
	}
	
	/*****
	 * Method: addContact
	 * 
	 * @param teamNumber
	 * @param name
	 * @param email
	 * @param address
	 * @param phoneNumber
	 * 
	 * Summary: Adds a contact to the database.
	 */
	
	public void addContact(Contact c) {
		addContact(c.getTeamNumber(), c.getName(), c.getEmail(), c.getAddress(), c.getPhoneNumber());
	}
	
	public synchronized void addContact(int teamNumber, String name, String email, 
			String address, String phoneNumber) {
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_TEAM_NUMBER, teamNumber);
		values.put(DBContract.COL_CONTACT_ID, createID(DBContract.TABLE_CONTACTS, 
				DBContract.COL_CONTACT_ID));
		values.put(DBContract.COL_CONTACT_NAME, name);
		values.put(DBContract.COL_EMAIL, email);
		values.put(DBContract.COL_ADDRESS, address);
		values.put(DBContract.COL_PHONE_NUMBER, phoneNumber);
		helper.getWritableDatabase().insert(DBContract.TABLE_CONTACTS, null, values);
		helper.close();
	}
	
	
	/*****
	 * Method: removeContact
	 * 
	 * @param id
	 * 
	 * Summary: Removes a contact from the contact list based on ID.
	 *****/
	
	public void removeContact(Contact c) {
		removeContact(c.getContactID());
	}
	
	public synchronized void removeContact(int id) {
		helper.getWritableDatabase().delete(DBContract.TABLE_CONTACTS, 
				DBContract.COL_CONTACT_ID + " LIKE ?", 
				new String[] {Integer.toString(id)});
	}
	
	
	/*****
	 * Method: getAllContacts
	 * 
	 * Summary: Gets all contacts from the database.
	 *****/
	
	public synchronized Contact[] getAllContacts() {
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + 
						DBContract.TABLE_CONTACTS, null);
		Contact[] contacts = new Contact[c.getCount()];
		
		for(int i = 0; i < contacts.length; i++) {
			c.moveToNext();
			contacts[i] = new Contact(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_CONTACT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_CONTACT_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_EMAIL)),
					c.getString(c.getColumnIndex(DBContract.COL_ADDRESS)),
					c.getString(c.getColumnIndex(DBContract.COL_PHONE_NUMBER))
					);
		}
		
		helper.close();
		return contacts;
	}
	
	
	/*****
	 * Method: getContactsByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return an array of contacts or null if the passed arrays
	 * were not the same length
	 * 
	 * Summary: Gets an array of contacts based on the cols and vals arrays
	 *****/
	
	public synchronized Contact[] getContactsByColumns(String[] cols, String[] vals) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new Contact[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_CONTACTS + " WHERE " 
				+ cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		Contact[] contacts = new Contact[c.getCount()];
		
		for(int i = 0; i < contacts.length; i++) {
			c.moveToNext();
			contacts[i] = new Contact(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_CONTACT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_CONTACT_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_EMAIL)),
					c.getString(c.getColumnIndex(DBContract.COL_ADDRESS)),
					c.getString(c.getColumnIndex(DBContract.COL_PHONE_NUMBER))
					);
		}
		
		helper.close();
		return contacts;
	}
	
	
	public synchronized boolean updateContacts(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(s.equals(DBContract.COL_CONTACT_ID) || 
					s.equals(DBContract.COL_TEAM_NUMBER))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateCols.length; i++)
			vals.put(updateCols[i], updateVals[i]);
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) {
			queryString += " AND " + queryCols[i] + " LIKE ?";
		}
		
		helper.getWritableDatabase().update(DBContract.TABLE_CONTACTS, vals, 
				queryString, queryVals);
		helper.close();
		return true;
	}
	
	
	/*****
	 * Method: addGame
	 * 
	 * @param name
	 * 
	 * Summary: Adds a new game to the database and creates a unique ID for it.
	 * 
	 * @return True if the game was added. False if this game name is already
	 * taken.
	 *****/
	
	public boolean addGame(Game g) {
		return addGame(g.getName());
	}
	
	public synchronized boolean addGame(String name) {
		if(!hasValue(DBContract.TABLE_GAMES, DBContract.COL_GAME_NAME, name)) {
			//If the name is not taken...
			ContentValues values = new ContentValues();	//Add it to the
			values.put(DBContract.COL_GAME_NAME, name);	//games table
			helper.getWritableDatabase().insert(DBContract.TABLE_GAMES, null, values);
			helper.close();
			return true;
		}
		
		helper.close();
		return false;
	}
	
	
	/*****
	 * Method: removeGame
	 * 
	 * @param name
	 * 
	 * Summary: Removes a game from the database based on the id parameter passed.
	 * Be careful using this method. It also removes all competitions, matches, 
	 * pictures, and robots that are associated with this game. This removes a whole
	 * season's worth of data from the database.
	 * 
	 * @return True if the game was removed, false if it was not. This happens
	 * if the string passed as a parameter was not found in the database.
	 *****/
	
	public boolean removeGame(Game g) {
		return removeGame(g.getName());
	}
	
	public synchronized boolean removeGame(String name) {
		if(!hasValue(DBContract.TABLE_GAMES, DBContract.COL_GAME_NAME, name))	
			//If this is not the name of a real game...
			return false;	//tell the caller that the operation failed.
		
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.query(DBContract.TABLE_ROBOTS, 
				new String[] {DBContract.COL_ROBOT_ID}, 
				DBContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {name}, 
				null,null, null);
		
		for(int robotCount = 0; robotCount < c.getCount(); robotCount++) {
			c.moveToNext();
			removeRobot(c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)));
		}
		
		db = helper.getWritableDatabase();	//Reopen because the removeRobot method closed it.
		db.delete(DBContract.TABLE_MATCH_PERF_METRICS, 
				DBContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {name});
		
		db.delete(DBContract.TABLE_ROBOT_METRICS, 
				DBContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {name});
		
		db.delete(DBContract.TABLE_EVENTS, 
				DBContract.COL_GAME_NAME + 
				" LIKE ?", new String[] {name});
		
		db.delete(DBContract.TABLE_GAMES, 
				DBContract.COL_GAME_NAME + 
				" LIKE ?", new String[] {name});
		
		helper.close();
		
		SharedPreferences prefs = context.getSharedPreferences
				(OptionsActivity.PREFS_NAME, Context.MODE_PRIVATE);
		if(name.equals(prefs.getString(OptionsActivity.PREFS_ROBOT_GAME, "none"))) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(OptionsActivity.PREFS_GENERATE_ROBOTS, false);
			editor.putString(OptionsActivity.PREFS_ROBOT_GAME, "none");
			editor.commit();
		}
		
		return true;
	}
	
	
	/*****
	 * Method: getAllGames
	 * 
	 * Summary: Gets all games from the database and returns them 
	 * in a Game array.
	 *****/
	
	public synchronized Game[] getAllGames() {
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.TABLE_GAMES + " ORDER BY " + 
				DBContract.COL_GAME_NAME + " ASC", null);
		Game[] g = new Game[c.getCount()];
		
		for(int i = 0; i < g.length; i++) {
			c.moveToNext();
			g[i] = new Game(c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)));
		}
		
		c.close();
		helper.close();
		return g;
	}
	
	/*****
	 * Method: addRobot
	 * 
	 * @param teamNumber
	 * @param gameName
	 * @param numberOfWheels
	 * @param wheelType
	 * @param driveTrain
	 * @return
	 * 
	 * Summary: Adds a robot to the database with the passed parameters. If
	 * the gameName string must be a valid game name in the database.
	 * 
	 * @return True if the robot was added. False if it was not, because the 
	 * passed gameName string was not an actual game name.
	 *****/
	
	public synchronized boolean addRobot(Robot robot) {
		return addRobot(robot.getTeamNumber(), robot.getGame(), 
				robot.getComments(), robot.getImagePath(), robot.getOPR(), robot.getMetricValues());
	}
	
	public synchronized boolean addRobot(int teamNumber, String gameName, String comments, 
			String imagePath, MetricValue[] vals) {
		return addRobot(teamNumber, gameName, comments, imagePath, -1, vals);
	}
	
	public synchronized boolean addRobot(int teamNumber, String gameName, String comments, 
			String imagePath, double opr, MetricValue[] vals) {
		if(!hasValue(DBContract.TABLE_GAMES, DBContract.COL_GAME_NAME, gameName))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_TEAM_NUMBER, teamNumber);
		values.put(DBContract.COL_GAME_NAME, gameName);
		values.put(DBContract.COL_ROBOT_ID, createID(DBContract.TABLE_ROBOTS, 
				DBContract.COL_ROBOT_ID));
		values.put(DBContract.COL_COMMENTS, comments);
		values.put(DBContract.COL_IMAGE_PATH, imagePath);
		values.put(DBContract.COL_OPR, opr);
		
		if(vals != null) {
			for(MetricValue v : vals) {
				String valString = v.getValueAsDBReadableString();
				values.put(v.getMetric().getKey(), valString);
			}
		}
		
		db.insert(DBContract.TABLE_ROBOTS, null, values);
		helper.close();
		return true;
	}
	
	
	/*****
	 * Method: removeRobot
	 * 
	 * @param robotID
	 * 
	 * Summary: Removes a robot from the table based on its ID. Be careful,
	 * this will remove a whole season worth of data on a robot.
	 * 
	 * @return True if the robot was removed successfully. False if
	 * the robot was not removed from the table because a valid ID
	 * was not passed in the parameter.
	 *****/
	public synchronized boolean removeRobot(int robotID) {
		if(!hasValue(DBContract.TABLE_ROBOTS, DBContract.COL_ROBOT_ID, 
				Integer.toString(robotID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		//Remove all pictures
		db.delete(DBContract.TABLE_MATCH_PERF, DBContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(robotID)});
		db.delete(DBContract.TABLE_ROBOTS, DBContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(robotID)});
		db.delete(DBContract.TABLE_EVENT_ROBOTS, 
				DBContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(robotID)});
		helper.close();
		return true;
	}
	
	
	/*****
	 * Method: getRobotsByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * Summary: returns a list of robots created by the cols
	 * and vals specified.
	 */
	public synchronized Robot[] getRobotsByColumns(String[] cols, String[] vals) {
		return getRobotsByColumns(cols, vals, false);
	}
	
	public synchronized Robot[] getRobotsByColumns(String[] cols, String[] vals, 
			boolean isOr) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new Robot[0];
		
		String logic;
		
		if(isOr)
			logic = " OR ";
		else
			logic = " AND ";
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_ROBOTS + 
				" WHERE " + cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with cols
			queryString += logic + cols[i] + " LIKE ?";
		
		queryString += " ORDER BY " + DBContract.COL_TEAM_NUMBER + " ASC " + ", " + 
				DBContract.COL_GAME_NAME + " ASC";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		Robot[] r = new Robot[c.getCount()];
		helper.close();
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			String thisRobotGame = c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME));
			ArrayList<MetricValue> metricVals = new ArrayList<MetricValue>();
			Metric[] metrics = new Metric[0];
			
			metrics = this.getRobotMetricsByColumns
				(new String[] {DBContract.COL_GAME_NAME}, new String[] {thisRobotGame});
				
			for(int metricCount = 0; metricCount < metrics.length; metricCount++) {
				String valString = c.getString
						(c.getColumnIndex(metrics[metricCount].getKey()));
				ArrayList<String> valsArr = new ArrayList<String>();
				String workingString = new String();

				if(valString == null)
					valString = new String();

				for(int charCount = 0; charCount < valString.length(); charCount++) {
					if(valString.charAt(charCount) != ':'){
						workingString += valString.substring(charCount, charCount + 1);
					} else {
						valsArr.add(workingString);
						workingString = new String();
					}
				}

				try {
					metricVals.add(
							new MetricValue(
									metrics[metricCount],
									valsArr.toArray(new String[0])
									));
				} catch(MetricTypeMismatchException e) {
					Log.e("FRCKrawler", e.getMessage());
				}
			}
			
			r[i] = new Robot(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					c.getString(c.getColumnIndex(DBContract.COL_IMAGE_PATH)),
					c.getDouble(c.getColumnIndex(DBContract.COL_OPR)),
					metricVals.toArray(new MetricValue[0])
					);
		}
		
		c.close();
		return r;
	}
	
	/*****
	 * Method: addRobotMetric
	 * 
	 * @param name - the name of the new metric
	 * @param game - what game this belongs to
	 * @param type - what type of metric it is. See DatabaseContract scouting metric types
	 * @param range - if the metric is a CHOOSER, these are the values that can be picked,
	 * if the metric is a SLIDER, this is the maximum and minimum values.
	 * @param description - a short description of the metric
	 * 
	 * Summary: Adds a new metric based off of the parameters passed. If this metric is a
	 * CHOOSER or a SLIDER, the range parameter should not be null.
	 * 
	 * @return True if the metric was added to the database correctly. False if there is already
	 * a metric with this name for this game, this game is not in the database, the type passed
	 * is not a valid type, or the limit of 16 metrics has been reached.
	 */
	public synchronized boolean addRobotMetric(Metric m) {
		
		return addRobotMetric(m.getMetricName(), m.getGameName(), m.getType(), m.getRange(),
				m.getDescription(), m.isDisplayed());
	}
	
	public synchronized boolean addRobotMetric(String name, String game, int type, 
			Object[] range, String description, boolean displayed) {
		
		if(!hasValue(DBContract.TABLE_GAMES, DBContract.COL_GAME_NAME, game))
			return false;	//This game is not in the database.
		
		if(type < 0 || type > DBContract.HIGHEST_TYPE)
			return false;	//This is not a real type.
		
		String rangeInput = new String();
		
		if(range != null) {
			
			for(Object r :  range)	//Create the string for the range.
				rangeInput += r.toString() + ":";	//The range is stored in one cell.
		}
		
		ContentValues values = new ContentValues();	//New values for the ROBOT_METRICS table
		values.put(DBContract.COL_METRIC_ID, createID(
				DBContract.TABLE_ROBOT_METRICS, DBContract.COL_METRIC_ID));
		values.put(DBContract.COL_METRIC_NAME, name);
		values.put(DBContract.COL_GAME_NAME, game);
		values.put(DBContract.COL_TYPE, type);
		values.put(DBContract.COL_RANGE, rangeInput);
		values.put(DBContract.COL_DESCRIPTION, description);
		values.put(DBContract.COL_DISPLAY, displayed);
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_ROBOT_METRICS,	//Check to see who has
							new String[] {DBContract.COL_METRIC_KEY, 
									DBContract.COL_POSITION}, //what keys and positions already
							DBContract.COL_GAME_NAME + " LIKE ?", 
							new String[] {game}, 
							null, null, 
							DBContract.COL_METRIC_KEY + " ASC");
		
		for(int key = 0; key < DBContract.COL_KEYS.length; key++) {	//Cycle through
																			//all possible keys.
			if(!c.moveToNext() ||
					!DBContract.COL_KEYS[key].equals(c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)))) {
				
				values.put(DBContract.COL_METRIC_KEY, //Assign this key to the new
						DBContract.COL_KEYS[key]);	//metric
				
				ContentValues nullValue = new ContentValues();	//Make a null CV
				nullValue.putNull(DBContract.COL_KEYS[key]);
				
				db.update(DBContract.TABLE_ROBOTS,	//Put the null
						nullValue, 							//value into the
						DBContract.COL_GAME_NAME + " LIKE ?",//robots table
						new String[] {game});
				
				break;	//exit the loop, no need to continue
			}
			
			if(key == DBContract.COL_KEYS.length)	//Return false because no more metrics can
				return false;	//be added. Limit reached.
		}
		
		c.moveToFirst();
		int biggestPosition = 0;
		
		for(int i = 0; i < c.getCount(); i++) {
			int currentPos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
			
			if(biggestPosition < currentPos)
				biggestPosition = currentPos;
			
			c.moveToNext();
		}
		
		values.put(DBContract.COL_POSITION, biggestPosition + 1);
		
		db.insert(DBContract.TABLE_ROBOT_METRICS, null, values);
		db.close();
		
		return true;
	}
	
	
	/*****
	 * Method: removeRobotMetric
	 * 
	 * @param name
	 * @param game
	 * 
	 * Summary: Removes the metric from the specified game.
	 * 
	 * @return True if the metric was removed. False if this was not a valid game or
	 * not a valid metric, i.e. it isn't in the database.
	 *****/
	public synchronized boolean removeRobotMetric(int metricID) {
		
		if(!hasValue(DBContract.TABLE_ROBOT_METRICS, DBContract.COL_METRIC_ID, 
				Integer.toString(metricID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_ROBOT_METRICS, 
							new String[] {DBContract.COL_METRIC_KEY, 
								DBContract.COL_GAME_NAME}, 
							DBContract.COL_METRIC_ID + " LIKE ?", 
							new String[] {Integer.toString(metricID)}, 
							null, null, null);
		
		c.moveToFirst();
		String key = c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY));
		String game = c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME));
		
		ContentValues nullVal = new ContentValues();
		nullVal.putNull(key);
		
		db.update(DBContract.TABLE_ROBOTS, nullVal, 
					DBContract.COL_GAME_NAME + " LIKE ?", new String[] {game});
		
		db.delete(DBContract.TABLE_ROBOT_METRICS, 
				  DBContract.COL_METRIC_ID + " LIKE ?", 
				  new String[] {Integer.toString(metricID)});
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: flipMatchMetricPostion
	 * 
	 * Summary: Flips the two position values for the specified metrics
	 *****/
	public synchronized boolean flipMatchMetricPosition(int metricID1, 
			int metricID2) {
		
		if(!hasValue(DBContract.TABLE_MATCH_PERF_METRICS, DBContract.COL_METRIC_ID, 
				Integer.toString(metricID1)) || !hasValue(DBContract.TABLE_MATCH_PERF_METRICS, 
						DBContract.COL_METRIC_ID, Integer.toString(metricID2)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_MATCH_PERF_METRICS, 
				new String[] {DBContract.COL_METRIC_ID, DBContract.COL_POSITION}, 
				DBContract.COL_METRIC_ID + " LIKE ? OR " + DBContract.COL_METRIC_ID + 
						" LIKE ?",
				new String[] {Integer.toString(metricID1), Integer.toString(metricID2)},
				null,
				null,
				null);
		
		if(c.getCount() < 2)
			return false;
		
		c.moveToFirst();
		int firstID = c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID));
		int firstPos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
		c.moveToNext();
		int secondID = c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID));
		int secondPos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
		
		ContentValues firstVals = new ContentValues();
		firstVals.put(DBContract.COL_POSITION, secondPos);
		db.update(DBContract.TABLE_MATCH_PERF_METRICS, firstVals, 
				DBContract.COL_METRIC_ID + " LIKE " + firstID, null);
		
		ContentValues secondVals = new ContentValues();
		secondVals.put(DBContract.COL_POSITION, firstPos);
		db.update(DBContract.TABLE_MATCH_PERF_METRICS, secondVals, 
				DBContract.COL_METRIC_ID + " LIKE " + secondID, null);
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: getRobotMetricsByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * Summary: Gets all robot metrics based on the columns and values passed as 
	 * parameters.
	 */
	public synchronized Metric[] getRobotMetricsByColumns(String[] cols, String[] vals) {
		return getRobotMetricsByColumns(cols, vals, false);
	}
	
	public synchronized Metric[] getRobotMetricsByColumns(String[] cols, String[] vals, 
			boolean isOr) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new Metric[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_ROBOT_METRICS + 
				" WHERE " + cols[0] + " LIKE ?";
		String orAnd;
		
		if(isOr)
			orAnd = " OR ";
		else
			orAnd = " AND ";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with coos
			queryString += orAnd + cols[i] + " LIKE ?";
		
		queryString += " ORDER BY " + DBContract.COL_POSITION + " ASC";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		Metric[] m = new Metric[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			String rangeString = c.getString(c.getColumnIndex(DBContract.COL_RANGE));
			ArrayList<Object> rangeArrList = new ArrayList<Object>();
			String currentRangeValString = new String();
			
			for(int character = 0; character < rangeString.length(); character++) {
				if(rangeString.charAt(character) != ':')
					currentRangeValString += rangeString.charAt(character);
				else {
					rangeArrList.add(currentRangeValString);
					currentRangeValString = new String();
				}
			}
			
			m[i] = new Metric(
					c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_DESCRIPTION)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)),
					c.getInt(c.getColumnIndex(DBContract.COL_TYPE)),
					rangeArrList.toArray(),
					(c.getInt(c.getColumnIndex(DBContract.COL_DISPLAY)) > 0),
					c.getInt(c.getColumnIndex(DBContract.COL_POSITION))
					);
		}
		
		c.close();
		helper.close();
		return m;
	}
	
	
	/*****
	 * Method: updateRobotMetrics
	 * 
	 * Summary: Updates the specified columns in the rows taken from the query
	 *****/
	public synchronized boolean updateRobotMetrics(Metric[] metrics) {
		boolean allUpdated = true;
		
		String[] queryCols = new String[] {DBContract.COL_METRIC_ID};
		
		String[] updateCols = new String[5];
		updateCols[0] = DBContract.COL_METRIC_NAME;
		updateCols[1] = DBContract.COL_DESCRIPTION;
		updateCols[2] = DBContract.COL_DISPLAY;
		updateCols[3] = DBContract.COL_RANGE;
		updateCols[4] = DBContract.COL_TYPE;
		
		for(int i = 0; i < metrics.length; i++) {
			String[] queryVals = new String[] {Integer.toString(metrics[i].getID())};
			
			String[] updateVals = new String[5];
			updateVals[0] = metrics[i].getMetricName();
			updateVals[1] = metrics[i].getDescription();
			if(metrics[i].isDisplayed())
				updateVals[2] = "1";
			else
				updateVals[2] = "0";
			updateVals[4] = Integer.toString(metrics[i].getType());
			
			updateVals[3] = new String();
			
			if(metrics[i].getRange() != null) {
				for(Object r :  metrics[i].getRange())	//Create the string for the range.
					updateVals[3] += r.toString() + ":";	//The range is stored in one cell.
			}
			
			if(!updateRobotMetrics(queryCols, queryVals, updateCols, updateVals))
				allUpdated = false;
		}
		
		return allUpdated;
	}
	
	public synchronized boolean updateRobotMetrics(String queryCols[], 
			String queryVals[], String updateCols[], String updateVals[]) {
		
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(DBContract.COL_METRIC_ID.equals(s))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateVals.length; i++) {
			vals.put(updateCols[i], updateVals[i]);
		}
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) 
			queryString += " AND " + queryCols[i] + " LIKE ?";
		
		helper.getWritableDatabase().update(DBContract.TABLE_ROBOT_METRICS, vals, 
				queryString, queryVals);
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: flipRobotMetricPostion
	 * 
	 * Summary: Flips the two position values for the specified metrics
	 *****/
	
	public synchronized boolean flipRobotMetricPosition(int metricID1, 
			int metricID2) {
		
		if(!hasValue(DBContract.TABLE_ROBOT_METRICS, DBContract.COL_METRIC_ID, 
				Integer.toString(metricID1)) || !hasValue(DBContract.TABLE_ROBOT_METRICS, 
						DBContract.COL_METRIC_ID, Integer.toString(metricID2)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_ROBOT_METRICS, 
				new String[] {DBContract.COL_METRIC_ID, DBContract.COL_POSITION}, 
				DBContract.COL_METRIC_ID + " LIKE ? OR " + DBContract.COL_METRIC_ID + 
				" LIKE ?",
				new String[] {Integer.toString(metricID1), Integer.toString(metricID2)},
				null,
				null,
				null);
		
		if(c.getCount() < 2)
			return false;
		
		c.moveToFirst();
		int firstID = c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID));
		int firstPos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
		c.moveToNext();
		int secondID = c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID));
		int secondPos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
		
		ContentValues firstVals = new ContentValues();
		firstVals.put(DBContract.COL_POSITION, secondPos);
		db.update(DBContract.TABLE_ROBOT_METRICS, firstVals, 
				DBContract.COL_METRIC_ID + " LIKE " + firstID, null);
		
		ContentValues secondVals = new ContentValues();
		secondVals.put(DBContract.COL_POSITION, firstPos);
		db.update(DBContract.TABLE_ROBOT_METRICS, secondVals, 
				DBContract.COL_METRIC_ID + " LIKE " + secondID, null);
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: addDriverMetric
	 * 
	 * @param name
	 * @param game
	 * @param type
	 * @param range
	 * @param description
	 * @param displayed
	 * @return
	 * 
	 * Summary: Adds a driver metric to the database.
	 */
	
	public synchronized boolean addDriverMetric(Metric m) {
		
		return addDriverMetric(m.getMetricName(), m.getGameName(), m.getType(), m.getRange(),
				m.getDescription(), m.isDisplayed());
	}
	
	public synchronized boolean addDriverMetric(String name, String game, int type, 
			Object[] range, String description, boolean displayed) {
		
		if(!hasValue(DBContract.TABLE_GAMES, DBContract.COL_GAME_NAME, game))
			return false;	//This game is not in the database.
		
		if(type < 0 || type > DBContract.HIGHEST_TYPE)
			return false;	//This is not a real type.
		
		String rangeInput = new String();
		
		if(range != null) {
			
			for(Object r :  range)	//Create the string for the range.
				rangeInput += r.toString() + ":";	//The range is stored in one cell.
		}
		
		ContentValues values = new ContentValues();	//New values for the ROBOT_METRICS table
		values.put(DBContract.COL_METRIC_ID, createID(
				DBContract.TABLE_DRIVER_METRICS, DBContract.COL_METRIC_ID));
		values.put(DBContract.COL_METRIC_NAME, name);
		values.put(DBContract.COL_GAME_NAME, game);
		values.put(DBContract.COL_TYPE, type);
		values.put(DBContract.COL_RANGE, rangeInput);
		values.put(DBContract.COL_DESCRIPTION, description);
		values.put(DBContract.COL_DISPLAY, displayed);
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_DRIVER_METRICS,	//Check to see who has
							new String[] {DBContract.COL_METRIC_KEY}, //what keys already
							DBContract.COL_GAME_NAME + " LIKE ?", 
							new String[] {game}, 
							null, null, 
							DBContract.COL_METRIC_KEY + " ASC");
		
		for(int key = 0; key < DBContract.COL_KEYS.length; key++) {	//Cycle through
																			//all possible keys.
			if(!c.moveToNext() ||
					!DBContract.COL_KEYS[key].equals(c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)))) {
				
				values.put(DBContract.COL_METRIC_KEY, //Assign this key to the new
						DBContract.COL_KEYS[key]);	//metric
				
				break;	//exit the loop, no need to continue
			}
			
			if(key == DBContract.COL_KEYS.length)	//Return false because no more metrics can
				return false;	//be added. Limit reached.
		}
		
		db.insert(DBContract.TABLE_DRIVER_METRICS, null, values);
		db.close();
		
		return true;
	}
	
	
	/*****
	 * Method: removeDriverMetric
	 * 
	 * Summary: removes a driver metric from the database
	 *****/
	
	public synchronized boolean removeDriverMetric(int metricID) {
		
		if(!hasValue(DBContract.TABLE_DRIVER_METRICS, 
				DBContract.COL_METRIC_ID, Integer.toString(metricID)))
			return false;	//If this is not a real game or metric...
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_DRIVER_METRICS,
				new String[] {DBContract.COL_METRIC_KEY, DBContract.COL_GAME_NAME}, 
				DBContract.COL_METRIC_ID + " LIKE ?", 
				new String[] {Integer.toString(metricID)}, 
				null, null, null);
		c.moveToFirst();//Gets the key for this metric
		String key = c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY));
		String game = c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME));
		
		ContentValues nullVal = new ContentValues();//Make a null CV for it
		nullVal.putNull(key);
		
		c = db.query(DBContract.TABLE_EVENTS, 
				new String[] {DBContract.COL_EVENT_ID}, 
				DBContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {game}, 
				null, null, null);//Get the competitions for this game
		
		while(c.moveToNext()) {//While there are still more competitions...
			
			db.update(DBContract.TABLE_DRIVER_DATA, nullVal, 
					DBContract.COL_EVENT_ID + " LIKE ?", 
					new String[] {//Set the metric at the key to null
						c.getString(c.getColumnIndex(DBContract.COL_EVENT_ID))
					});
		}
		
		db.delete(DBContract.TABLE_DRIVER_METRICS, 
				  DBContract.COL_METRIC_ID + " LIKE ?", 
				  new String[] {Integer.toString(metricID)});//Delete the metric
		
		helper.close();
		
		return true;
	}
	
	/*****
	 * Method: addMatchPerformanceMetric
	 * 
	 * @param name - cannot be used in this game already
	 * @param game - must be a game in the database
	 * @param type - must be integers 0 to 4, see the DatabaseContract class
	 * @param range
	 * @param description
	 * 
	 * Summary: Adds a match performance metric to the database based on the parameters
	 * passed.
	 * 
	 * @return True if the game was added successfully. False if the name was already
	 * used or was null, the game was not a real game, or the type was not a number 0
	 * through 4.
	 */
	
	public synchronized boolean addMatchPerformanceMetric(Metric metric) {
		
		return addMatchPerformanceMetric(metric.getMetricName(), metric.getGameName(),
				metric.getType(), metric.getRange(), metric.getDescription(), 
				metric.isDisplayed());
	}
	
	public synchronized boolean addMatchPerformanceMetric(String name, String game, 
			int type, Object[] range, String description, boolean displayed) {
		
		if(!hasValue(DBContract.TABLE_GAMES, DBContract.COL_GAME_NAME, game))
			return false;	//This game is not in the database.
		
		if(type < 0 || type > DBContract.HIGHEST_TYPE)
			return false;	//This is not a real type.
		
		String rangeInput = new String();
		
		if(range != null) {
			for(Object r :  range)	//Create the string for the range.
				rangeInput += r.toString() + ":";	//The range is stored in one cell.
		}
		
		ContentValues values = new ContentValues();	//New values for the MATCH_PERF_METRICS
		values.put(DBContract.COL_METRIC_ID, createID(
				DBContract.TABLE_MATCH_PERF_METRICS, DBContract.COL_METRIC_ID));
		values.put(DBContract.COL_METRIC_NAME, name);
		values.put(DBContract.COL_GAME_NAME, game);
		values.put(DBContract.COL_TYPE, type);
		values.put(DBContract.COL_RANGE, rangeInput);
		values.put(DBContract.COL_DESCRIPTION, description);
		values.put(DBContract.COL_DISPLAY, displayed);
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_MATCH_PERF_METRICS,	//Check to see who has
							new String[] {DBContract.COL_METRIC_KEY, 
									DBContract.COL_POSITION}, //what keys and positions already
							DBContract.COL_GAME_NAME + " LIKE ?", 
							new String[] {game}, 
							null, null, 
							DBContract.COL_METRIC_KEY + " ASC");
		
		boolean wasAssigned = false;
		
		for(int key = 0; key < DBContract.COL_KEYS.length; key++) {	//Cycle through
																	//all possible keys.
			boolean isTaken = false;
			c.moveToFirst();
			
			for(int metricCount = 0; metricCount < c.getCount(); metricCount++) {
				if(metricCount != 0)
					c.moveToNext();
				
				if(DBContract.COL_KEYS[key].equals(c.getString
						(c.getColumnIndex(DBContract.COL_METRIC_KEY))))
					isTaken = true;
			}
			
			if(!isTaken) {
				values.put(DBContract.COL_METRIC_KEY, //Assign this key to the new
						DBContract.COL_KEYS[key]);	//metric
				
				Cursor eventCursor = db.query(DBContract.TABLE_EVENTS, //Select the competitions for this game
											  new String[] {DBContract.COL_EVENT_ID}, 
											  DBContract.COL_GAME_NAME + " LIKE ?", 
											  new String[] {game}, 
											  null, null, null);
				
				ContentValues nullValue = new ContentValues();	//Make a null CV
				nullValue.putNull(DBContract.COL_KEYS[key]);
				
				for(int eventCount = 0; eventCount < eventCursor.getCount(); eventCount++) {	//Cycle through all the comps
					
					eventCursor.moveToNext();
					
					db.update(DBContract.TABLE_MATCH_PERF, //Set the key value to null in the match table
							nullValue, 
							DBContract.COL_EVENT_ID + " LIKE ?", 
							new String[] {eventCursor.getString(
									eventCursor.getColumnIndex(DBContract.COL_EVENT_ID))});
				}
				
				wasAssigned = true;
				break;	//No need to continue
			}
		}
		
		c.moveToFirst();
		int biggestPosition = 0;
		
		for(int i = 0; i < c.getCount(); i++) {
			int currentPos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
			
			if(biggestPosition < currentPos)
				biggestPosition = currentPos;
			
			c.moveToNext();
		}
		
		values.put(DBContract.COL_POSITION, biggestPosition + 1);
		
		if(wasAssigned)
			db.insert(DBContract.TABLE_MATCH_PERF_METRICS, null, values);
		
		db.close();
		
		return true;
	}
	
	
	/*****
	 * Method: removeMatchPerformanceMetric
	 * 
	 * @param name
	 * @param game
	 * 
	 * Summary: Removes the metric based on the name and game parameters passed.
	 * This also removes any data associated with this metric, so call carefully.
	 * 
	 * @return True if the metric was removed successfully. False if the game
	 * given was not a valid game, or the name given was no the name of a metric.
	 *****/
	
	public synchronized boolean removeMatchPerformaceMetric(int metricID) {
		
		if(!hasValue(DBContract.TABLE_MATCH_PERF_METRICS, 
				DBContract.COL_METRIC_ID, Integer.toString(metricID)))
			return false;	//If this is not a real game or metric...
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DBContract.TABLE_MATCH_PERF_METRICS,
				new String[] {DBContract.COL_METRIC_KEY, DBContract.COL_GAME_NAME}, 
				DBContract.COL_METRIC_ID + " LIKE ?", 
				new String[] {Integer.toString(metricID)}, 
				null, null, null);
		c.moveToFirst();//Gets the key for this metric
		String key = c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY));
		String game = c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME));
		
		ContentValues nullVal = new ContentValues();//Make a null CV for it
		nullVal.putNull(key);
		
		c = db.query(DBContract.TABLE_EVENTS, 
				new String[] {DBContract.COL_EVENT_ID}, 
				DBContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {game}, 
				null, null, null);//Get the competitions for this game
		
		while(c.moveToNext()) {//While there are still more competitions...
			
			db.update(DBContract.TABLE_MATCH_PERF, nullVal, 
					DBContract.COL_EVENT_ID + " LIKE ?", 
					new String[] {//Set the metric at the key to null
						c.getString(c.getColumnIndex(DBContract.COL_EVENT_ID))
					});
		}
		
		db.delete(DBContract.TABLE_MATCH_PERF_METRICS, 
				  DBContract.COL_METRIC_ID + " LIKE ?", 
				  new String[] {Integer.toString(metricID)});//Delete the metric
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: getMatchPerfMetricsByColumns
	 * 
	 * Summary: 
	 */
	public synchronized Metric[] getMatchPerformanceMetricsByColumns(String[] cols, 
			String[] vals) {
		return getMatchPerformanceMetricsByColumns(cols, vals, false);
	}
	
	public synchronized Metric[] getMatchPerformanceMetricsByColumns
			(String[] cols, String[] vals, boolean isOr) {
		
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new Metric[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_MATCH_PERF_METRICS + 
				" WHERE " + cols[0] + " LIKE ?";
		String orAnd;
		
		if(isOr) {
			orAnd = " OR ";
		} else {
			orAnd = " AND ";
		}
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += orAnd + cols[i] + " LIKE ?";
		
		queryString += " ORDER BY " + DBContract.COL_POSITION + " ASC";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		Metric[] metrics = new Metric[c.getCount()];
		
		for(int i = 0; i < metrics.length; i++) {
			c.moveToNext();
			
			String rangeString = c.getString(c.getColumnIndex(DBContract.COL_RANGE));
			ArrayList<Object> rangeArrList = new ArrayList<Object>();
			
			String currentRangeValString = new String();
			
			for(int character = 0; character < rangeString.length(); character++) {
				
				if(rangeString.charAt(character) != ':')
					currentRangeValString += rangeString.charAt(character);
				
				else {
					rangeArrList.add(currentRangeValString);
					currentRangeValString = new String();
				}
			}
			
			metrics[i] = new Metric(
					c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_DESCRIPTION)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)),
					c.getInt(c.getColumnIndex(DBContract.COL_TYPE)),
					rangeArrList.toArray(),
					(c.getInt(c.getColumnIndex(DBContract.COL_DISPLAY)) > 0),
					c.getInt(c.getColumnIndex(DBContract.COL_POSITION))
					);
		}
		
		c.close();
		helper.close();
		
		return metrics;
	}
	
	
	/*****
	 * Method: updateMatchPerformanceMetric
	 * 
	 * Summary: Updates the specified columns in the rows taken from the query
	 *****/
	public synchronized boolean updateMatchPerformanceMetrics(Metric[] metrics) {
		boolean allUpdated = true;
		
		String[] queryCols = new String[] {DBContract.COL_METRIC_ID};
		
		String[] updateCols = new String[5];
		updateCols[0] = DBContract.COL_METRIC_NAME;
		updateCols[1] = DBContract.COL_DESCRIPTION;
		updateCols[2] = DBContract.COL_DISPLAY;
		updateCols[3] = DBContract.COL_RANGE;
		updateCols[4] = DBContract.COL_TYPE;
		
		for(int i = 0; i < metrics.length; i++) {
			String[] queryVals = new String[] {Integer.toString(metrics[i].getID())};
			
			String[] updateVals = new String[5];
			updateVals[0] = metrics[i].getMetricName();
			updateVals[1] = metrics[i].getDescription();
			if(metrics[i].isDisplayed())
				updateVals[2] = "1";
			else
				updateVals[2] = "0";
			updateVals[4] = Integer.toString(metrics[i].getType());
			
			updateVals[3] = new String();
			
			if(metrics[i].getRange() != null) {
				for(Object r :  metrics[i].getRange())	//Create the string for the range.
					updateVals[3] += r.toString() + ":";	//The range is stored in one cell.
			}
			
			if(!updateMatchPerformanceMetrics(queryCols, queryVals, updateCols, updateVals))
				allUpdated = false;
		}
		
		return allUpdated;
	}
	
	public synchronized boolean updateMatchPerformanceMetrics(String queryCols[], 
			String queryVals[], String updateCols[], String updateVals[]) {
		
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(DBContract.COL_METRIC_ID.equals(s))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateVals.length; i++) {
			vals.put(updateCols[i], updateVals[i]);
		}
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) 
			queryString += " AND " + queryCols[i] + " LIKE ?";
		
		helper.getWritableDatabase().update(DBContract.TABLE_MATCH_PERF_METRICS, vals, 
				queryString, queryVals);
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: updateRobot
	 * 
	 * @param queryCols
	 * @param queryVals
	 * @param updateCols
	 * @param updateVals
	 * @return
	 * 
	 */
	public synchronized boolean updateRobots(Robot[] robots) {
		boolean allUpdated = true;
		
		String[] queryCols = new String[] {DBContract.COL_ROBOT_ID};
		
		for(int i = 0; i < robots.length; i++) {
			String[] queryVals = new String[] {Integer.toString(robots[i].getID())};
			
			ArrayList<String> updateCols = new ArrayList<String>();
			ArrayList<String> updateVals = new ArrayList<String>();
			
			updateCols.add(DBContract.COL_COMMENTS);
			updateVals.add(robots[i].getComments());
			
			for(int k = 0; k < robots[i].getMetricValues().length; k++) {
				updateCols.add(robots[i].getMetricValues()[k].getMetric().getKey());
				updateVals.add(robots[i].getMetricValues()[k].getValueAsDBReadableString());
			}
			
			if(!updateRobots(queryCols, queryVals, 
					updateCols.toArray(new String[0]), 
					updateVals.toArray(new String[0])))
				allUpdated = false;
		}
		
		return allUpdated;
	}
	
	public synchronized boolean updateRobots(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(DBContract.COL_ROBOT_ID.equals(s))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateVals.length; i++) {
			vals.put(updateCols[i], updateVals[i]);
		}
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) 
			queryString += " AND " + queryCols[i] + " LIKE ?";
		
		helper.getWritableDatabase().update(DBContract.TABLE_ROBOTS, vals, 
				queryString, queryVals);
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: addRobotToEvent
	 * 
	 * @param eventID
	 * @param robotID
	 * @return
	 * 
	 * Summary: Adds the specified robot to the specified event.
	 */
	
	public synchronized boolean addRobotToEvent(int eventID, int robotID) {
		
		if(!hasValue(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID, Integer.toString(eventID)) || 
				!hasValue(DBContract.TABLE_ROBOTS, DBContract.COL_ROBOT_ID, Integer.toString(robotID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor enteredCheck = db.rawQuery("SELECT * FROM " + DBContract.TABLE_EVENT_ROBOTS +
				" WHERE " + DBContract.COL_EVENT_ID + " LIKE ?" +
				" AND " + DBContract.COL_ROBOT_ID + " LIKE ?" ,
				new String[] {Integer.toString(eventID), Integer.toString(robotID)});
		
		if(enteredCheck.getCount() > 0) {
			helper.close();
			return false;
		}
		
		String robotGameName;
		String eventGameName;
		
		Cursor eventCursor = db.rawQuery("SELECT " + DBContract.COL_GAME_NAME + 
				" FROM " + DBContract.TABLE_EVENTS + 
				" WHERE " + DBContract.COL_EVENT_ID + 
				" LIKE ?", new String[] {Integer.toString(eventID)});
		eventCursor.moveToFirst();
		eventGameName = eventCursor.getString(eventCursor.getColumnIndex(DBContract.COL_GAME_NAME));
		
		Cursor robotCursor = db.rawQuery("SELECT " + DBContract.COL_GAME_NAME + 
				" FROM " + DBContract.TABLE_ROBOTS + 
				" WHERE " + DBContract.COL_ROBOT_ID + 
				" LIKE ?", new String[] {Integer.toString(robotID)});
		robotCursor.moveToFirst();
		robotGameName = robotCursor.getString(robotCursor.getColumnIndex(DBContract.COL_GAME_NAME));
		
		if(!eventGameName.equals(robotGameName)) {
			helper.close();
			return false;
		}
		
		ContentValues vals = new ContentValues();
		vals.put(DBContract.COL_EVENT_ID, eventID);
		vals.put(DBContract.COL_ROBOT_ID, robotID);
		vals.put(DBContract.COL_IS_CHECKED, false);
		
		Log.d("FRCKrawler", "" + db.insert(DBContract.TABLE_EVENT_ROBOTS, null, vals));
		robotCursor.close();
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: removeRobotFrom Event
	 * 
	 * @param eventID
	 * @param robotID
	 * @return
	 * 
	 * Summary: Removes the specified robot from the specified event
	 */
	
	public synchronized boolean removeRobotFromEvent(int eventID, int robotID) {
		List[] lists = getListsByColumns(
				new String[] {DBContract.COL_EVENT_ID}, 
				new String[] {Integer.toString(eventID)});
		
		SQLiteDatabase db = helper.getWritableDatabase();
		int affectedRows = db.delete(
				DBContract.TABLE_EVENT_ROBOTS, 
				DBContract.COL_EVENT_ID + " LIKE ? AND " +
				DBContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(eventID), Integer.toString(robotID)});
		
		for(List l : lists) {
			db.delete(DBContract.TABLE_LIST_ENTRIES, 
					DBContract.COL_ROBOT_ID + " LIKE ? AND " + DBContract.COL_LIST_ID + " LIKE ?",
					new String[] {Integer.toString(robotID), Integer.toString(l.getListID())});
		}
		
		return affectedRows > 0;
	}
	
	
	/*****
	 * Method: getRobotsAtEvent
	 * 
	 * @param eventID
	 * @return
	 */
	
	public synchronized Robot[] getRobotsAtEvent(int eventID) {
		if(!hasValue(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID, 
				Integer.toString(eventID)))
			return null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		Cursor c = db.rawQuery("SELECT * " +
				" FROM " + DBContract.TABLE_EVENT_ROBOTS + 
				" WHERE " + DBContract.COL_EVENT_ID + " LIKE ? ", 
				new String[] {Integer.toString(eventID)});
		
		String[] colsArr = new String[c.getCount()];
		String[] valArr = new String[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			
			valArr[i] = c.getString(c.getColumnIndex(DBContract.COL_ROBOT_ID));
			colsArr[i] = DBContract.COL_ROBOT_ID;
		}
		
		Robot[] robots = getRobotsByColumns(colsArr, valArr, true);
		c.moveToFirst();
		
		for(int i = 0; i < c.getCount(); i++) {
			for(int k = 0; k < robots.length; k++)
				if(c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)) == robots[k].getID()) {
					if(c.getInt(c.getColumnIndex(DBContract.COL_IS_CHECKED)) == 0)
						robots[k].setChecked(false);
					else
						robots[k].setChecked(true);
					
					break;
			}
				
			c.moveToNext();
		}
		
		c.close();
		return robots;
	}
	
	
	/*****
	 * Method: getEventsByRobot
	 * 
	 * @param robotID
	 * @return
	 * 
	 * Summary: gets all events out of the database that this robot is in
	 */
	
	public Event[] getEventsByRobot(int robotID) {
		
		String robotIDString = Integer.toString(robotID);
		
		if(!hasValue(DBContract.TABLE_ROBOTS, DBContract.COL_ROBOT_ID, robotIDString))
			return null;
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT " + DBContract.COL_EVENT_ID +
				" FROM " + DBContract.TABLE_EVENT_ROBOTS + 
				" WHERE " + DBContract.COL_ROBOT_ID + 
				" LIKE ?"
				, new String[] {robotIDString});
		
		String[] colArray = new String[c.getCount()];
		String[] robotIDs = new String[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			colArray[i] = DBContract.COL_EVENT_ID;
			robotIDs[i] = c.getString(c.getColumnIndex(DBContract.COL_EVENT_ID));
		}
		
		helper.close();
		
		if(colArray.length > 0)
			return getEventsByColumns(colArray, robotIDs);
		else
			return new Event[0];
			
	}
	
	
	/*****
	 * Method: setRobotCheckedState
	 * 
	 * Summary: Sets a robot's checked state in the specified event.
	 * This state only applies to the specified event
	 */
	
	public synchronized boolean setRobotChecked(int eventID, int robotID, boolean checked) {
		if(!hasValue(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID, 
				Integer.toString(eventID)) || !hasValue(DBContract.TABLE_ROBOTS, 
						DBContract.COL_ROBOT_ID, Integer.toString(robotID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		ContentValues vals = new ContentValues();
		vals.put(DBContract.COL_IS_CHECKED, checked);
		db.update(DBContract.TABLE_EVENT_ROBOTS, vals, DBContract.COL_EVENT_ID + 
				" LIKE ? AND " + DBContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(eventID), Integer.toString(robotID)});
		
		helper.close();
		
		return true;
	}
	
	/*****
	 * Method: insertMatchData
	 * 
	 * @param data
	 * @return true if the data was entered successfully, false
	 * if the user, event, or robot did not exist.
	 * 
	 * Summary: Inserts match data from the data given.
	 */
	
	public synchronized boolean insertMatchData(MatchData data) {
		
		return insertMatchData(data.getEventID(), data.getMatchNumber(), data.getRobotID(),
				data.getUserID(), data.getMatchType(), data.getMetricValues(), 
				data.getComments());
	}
	
	public synchronized boolean insertMatchData(int eventID, int matchNumber, int robotID, 
			int userID, String matchType, MetricValue[] vals, String comments) {
		
		if(!hasValue(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID, Integer.toString(eventID)))
			return false;
		if(!hasValue(DBContract.TABLE_ROBOTS, DBContract.COL_ROBOT_ID, Integer.toString(robotID)))
			return false;
		/*if(!hasValue(DBContract.TABLE_USERS, DBContract.COL_USER_ID, Integer.toString(userID)))
			return false;*/
		
		ContentValues values = new ContentValues();
		
		values.put(DBContract.COL_DATA_ID, createID(DBContract.TABLE_MATCH_PERF, 
				DBContract.COL_DATA_ID));
		values.put(DBContract.COL_EVENT_ID, Integer.toString(eventID));
		values.put(DBContract.COL_MATCH_NUMBER, Integer.toString(matchNumber));
		values.put(DBContract.COL_ROBOT_ID, Integer.toString(robotID));
		values.put(DBContract.COL_USER_ID, Integer.toString(userID));
		values.put(DBContract.COL_MATCH_TYPE, matchType);
		values.put(DBContract.COL_COMMENTS, comments);
		Log.d("FRCKrawler", "Inserted match data.");
		for(MetricValue val : vals) {
			if(val != null) {
				String[] arr = val.getValue();
				String valString = new String();
			
				for(int i = 0; i < arr.length; i++)
					valString += arr[i] + ":";
				
				Log.d("FRCKrawler", valString);
			
				values.put(val.getMetric().getKey(), valString);
			} else {
				Log.e("FRCKrawler", "A MetricValue was null on insert match data.");
			}
		}
		
		helper.getWritableDatabase().insert(DBContract.TABLE_MATCH_PERF, null, values);
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: removeMatchDataByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * Summary: removes match data where cols at n is equal to vals at n
	 *****/
	public synchronized boolean removeMatchData(int matchDataID) {
		
		return removeMatchDataByColumns(new String[] {DBContract.COL_DATA_ID}, 
				new String[] {Integer.toString(matchDataID)});
	}
	
	public synchronized boolean removeMatchDataByColumns(String[] cols, String[] vals) {
		
		if(cols.length != vals.length || cols.length < 1)
			return false;
		
		String colsString = cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			colsString += " AND " + cols[i] + " LIKE ?";
		
		helper.getWritableDatabase().delete(DBContract.TABLE_MATCH_PERF, 
				colsString, vals);
		
		return true;
	}
	
	
	/*****
	 * Method: getMatchDataByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * Summary: gets the match data by the columns and values passed in the arrays
	 *****/
	
	public synchronized MatchData[] getMatchDataByColumns(String[] cols, String[] vals) {
		
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new MatchData[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_MATCH_PERF + " WHERE " + 
				cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
		
		queryString += " ORDER BY " + DBContract.COL_MATCH_TYPE + " DESC, " +
				DBContract.COL_MATCH_NUMBER + " ASC";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		MatchData[] d = new MatchData[c.getCount()];
		
		helper.close();
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			
			Event[] eventsArr = getEventsByColumns(new String[] 
					{DBContract.COL_EVENT_ID}, 
					new String[] {c.getString(c.getColumnIndex(DBContract.COL_EVENT_ID))});
			
			Metric [] metricArr = getMatchPerformanceMetricsByColumns
					(new String[] {DBContract.COL_GAME_NAME}, 
					new String[] {eventsArr[0].getGameName()});
			
			MetricValue[] dataArr = new MetricValue[metricArr.length];
			
			for(int k = 0; k < metricArr.length; k++) {
				
				ArrayList<String> valuesList = new ArrayList<String>();
				String valueString = c.getString(c.getColumnIndex(metricArr[k].getKey()));
				
				String currentValsString = new String();
				
				if(valueString != null) {
					for(int character = 0; character < valueString.length(); character++) {
					
						if(valueString.charAt(character) != ':')
							currentValsString += valueString.charAt(character);
					
						else {
							valuesList.add(currentValsString);
							currentValsString = new String();
						}
					}
				}
				
				try {
					dataArr[k] = new MetricValue(metricArr[k], valuesList.toArray(new String[0]));
				} catch (MetricTypeMismatchException e) {
					try {
						dataArr[k] = new MetricValue(metricArr[k], new String[] {"0"});
					} catch(MetricTypeMismatchException ex) {}
				}
			}
			
			d[i] = new MatchData(
					c.getInt(c.getColumnIndex(DBContract.COL_DATA_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_MATCH_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_MATCH_TYPE)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					dataArr
					);
		}
		
		c.close();
		helper.close();
		
		return d;
	}
	
	public synchronized MatchData[] getMatchDataByColumns(String[] cols, String[] vals, 
															int minMatchNum, int maxMatchNum) {
		
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new MatchData[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_MATCH_PERF + " WHERE " + 
				cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
		
		queryString += " AND " + DBContract.COL_MATCH_NUMBER + ">=" + minMatchNum;
		queryString += " AND " + DBContract.COL_MATCH_NUMBER + "<=" + maxMatchNum;
		
		queryString += " ORDER BY " + DBContract.COL_MATCH_TYPE + " DESC, " +
				DBContract.COL_MATCH_NUMBER + " ASC";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		MatchData[] d = new MatchData[c.getCount()];
		
		helper.close();
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			
			Event[] eventsArr = getEventsByColumns(new String[] 
					{DBContract.COL_EVENT_ID}, 
					new String[] {c.getString(c.getColumnIndex(DBContract.COL_EVENT_ID))});
			
			Metric [] metricArr = getMatchPerformanceMetricsByColumns
					(new String[] {DBContract.COL_GAME_NAME}, 
					new String[] {eventsArr[0].getGameName()});
			
			MetricValue[] dataArr = new MetricValue[metricArr.length];
			
			for(int k = 0; k < metricArr.length; k++) {
				
				ArrayList<String> valuesList = new ArrayList<String>();
				String valueString = c.getString(c.getColumnIndex(metricArr[k].getKey()));
				
				String currentValsString = new String();
				
				if(valueString != null) {
					for(int character = 0; character < valueString.length(); character++) {
					
						if(valueString.charAt(character) != ':')
							currentValsString += valueString.charAt(character);
					
						else {
							valuesList.add(currentValsString);
							currentValsString = new String();
						}
					}
				}
				
				try {
					dataArr[k] = new MetricValue(metricArr[k], valuesList.toArray(new String[0]));
				} catch (MetricTypeMismatchException e) {
					try {
						dataArr[k] = new MetricValue(metricArr[k], new String[] {"0"});
					} catch(MetricTypeMismatchException ex) {}
				}
			}
			
			d[i] = new MatchData(
					c.getInt(c.getColumnIndex(DBContract.COL_DATA_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_MATCH_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_MATCH_TYPE)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					dataArr
					);
		}
		
		helper.close();
		
		return d;
	}
	
	
	/*****
	 * Method: updateMatchDataByColumns
	 * 
	 * Summary: updates the match data based on the passed query columns
	 *****/
	
	public synchronized boolean updateMatchData(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(s.equals(DBContract.COL_DATA_ID))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateCols.length; i++)
			vals.put(updateCols[i], updateVals[i]);
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) {
			queryString += " AND " + queryCols[i] + " LIKE ?";
		}
		
		helper.getWritableDatabase().update(DBContract.TABLE_MATCH_PERF, vals, 
				queryString, queryVals);
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: getCompiledEventData
	 * 
	 * @param e
	 * @param database
	 * 
	 * Summary: gets the averaged and counted data from every robot's match
	 * WARNING! This method could be a lengthy operation, even for small
	 * numbers of robots, this should NEVER be called in the UI thread. An
	 * AsyncTask or other worker thread is highly recomended.
	 */
	
	public CompiledData[] getCompiledEventData(Event e, Query[] querys, SortKey sortKey) {
		return getCompiledEventData(e.getEventID(), querys, sortKey);
	}
	
	public CompiledData[] getCompiledEventData(int eventID, Query[] querys, SortKey sortKey) {
		if(!hasValue(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID, 
				Integer.toString(eventID)))
			return null;
		//Get the weighting value from the settings
		SharedPreferences prefs = context.getSharedPreferences
				(OptionsActivity.PREFS_NAME, Context.MODE_PRIVATE);
		float weightingRatio = prefs.getFloat(OptionsActivity.PREFS_COMPILE_WEIGHT, 1);
		
		//Get the event arr
		Event[] eventArr = this.getEventsByColumns(new String[] {DBContract.COL_EVENT_ID}, 
				new String[] {Integer.toString(eventID)});
		
		//See if the given ID exists and assign it to an event object
		if(eventArr.length < 1)
			return null;
		
		Event event = eventArr[0];
		
		//Get the robots, and our metrics, and make CompiledData for each robot
		Robot[] robots = getRobotsAtEvent(eventID);
		Metric[] metrics = this.getMatchPerformanceMetricsByColumns
				(new String[] {DBContract.COL_GAME_NAME}, new String[] {event.getGameName()});
		CompiledData[] compiledData = new CompiledData[robots.length];
		
		for(int robotCount = 0; robotCount < robots.length; robotCount++) {
			//Create our array for compiled data, and get our match data for this robot
			MetricValue[] metricVals = new MetricValue[metrics.length];
			MatchData[] matchData = this.getMatchDataByColumns
					(new String[] {DBContract.COL_ROBOT_ID}, 
							new String[] {Integer.toString(robots[robotCount].getID())});
			
			//Array for compiling comments
			boolean isCommentsFilled = false;
			String[] comments = new String[matchData.length];
			
			//Array to keep track of matches played in
			boolean isMatchesPlayedFilled = false;
			int[] matchesPlayed = new int[matchData.length];
			
			//Compile the match data for every metric
			for(int metricCount = 0; metricCount < metricVals.length; metricCount++) {
				String[] compiledValue = new String[0];
				int[] chooserCounts = new int[0];
				
				if(matchData.length != 0) {
					switch(metrics[metricCount].getType()) {
						case DBContract.BOOLEAN:
							double yes = 0;
							double no = 0;
						
							for(int matchCount = 0; matchCount < matchData.length; 
									matchCount++) {
								//Comments
								if(!isCommentsFilled)
									comments[matchCount] = matchData[matchCount].
										getComments();
								
								//Matches played
								if(!isMatchesPlayedFilled)
									matchesPlayed[matchCount] = matchData[matchCount].
										getMatchNumber();
								
								String stringValue = null;
								
								try {
									stringValue = matchData[matchCount].
										getMetricValues()[metricCount].getValue()[0];
								} catch(ArrayIndexOutOfBoundsException e) {}
								
								if(stringValue != null && 
										!stringValue.equals("") && 
										!stringValue.equals("null")) {
									boolean value = Boolean.parseBoolean(stringValue);
									double weight = Math.pow(weightingRatio, matchCount);
									if(value)
										yes += weight;
									else
										no += weight;
								}
							}
							
							double compiledRatio = (yes / (yes + no)) * 100;
							compiledValue = new String[] {"" + compiledRatio};
							break;
						
						case DBContract.CHOOSER:
							boolean isNumeric = true;
							Object[] range = metrics[metricCount].getRange();
							
							//Check to see if these metrics are numeric
							try {
								for(int choiceCount = 0; choiceCount < range.length; 
										choiceCount++) {
									Double.parseDouble((String)range[choiceCount]);
								}
							} catch(NumberFormatException e) {
								isNumeric = false;
							}
							
							if(isNumeric) { //Find the weighted average
								
								double numerator = 0;
								double denominator = 0;
								boolean valueIsNull = true;
							
								for(int matchCount = 0; matchCount < matchData.length; 
										matchCount++) {
									
									//Comments
									if(!isCommentsFilled)
										comments[matchCount] = matchData[matchCount].
											getComments();
									
									//Matches played
									if(!isMatchesPlayedFilled)
										matchesPlayed[matchCount] = matchData[matchCount].
											getMatchNumber();
									
									String[] valueArray = matchData[matchCount].
											getMetricValues()[metricCount].getValue();
									
									if(valueArray.length > 0) {
										
										valueIsNull = false;
										
										int value = Integer.parseInt(valueArray[0]);
										double weight = Math.pow
												(weightingRatio, 
														matchCount);
									
										numerator += value * weight;
										denominator += weight;
									}
								}
								
								if(valueIsNull) {
									compiledValue = new String[] {""};
								} else {
									if(denominator == 0)
										denominator = 1;
										
									double weightedAverage = numerator / denominator;
									compiledValue = new String[] {Double.toString(weightedAverage)};
								}
								
							} else { //Find the ratios for each choice
								double[] counts = new double[range.length];
								for(int matchCount = 0; matchCount < matchData.length; 
										matchCount++) {
									
									//Comments
									if(!isCommentsFilled)
										comments[matchCount] = matchData[matchCount].
											getComments();
									
									//Matches played
									if(!isMatchesPlayedFilled)
										matchesPlayed[matchCount] = matchData[matchCount].
											getMatchNumber();
									
									if(matchData[matchCount].
											getMetricValues()[metricCount].getValue().length > 0) {
										String value = matchData[matchCount].
												getMetricValues()[metricCount].getValue()[0];

										int rangeAddress = -1;
										//Find out which range number this choice is
										for(int choiceCount = 0; choiceCount < range.length;
												choiceCount++) {
											if(value.equals(range[choiceCount]))
												rangeAddress = choiceCount;
										}

										if(rangeAddress != -1)
											counts[rangeAddress]++;
									}
								}
								
								compiledValue = new String[range.length];
								chooserCounts = new int[range.length];
								
								for(int choiceCount = 0; choiceCount < compiledValue.length;
										choiceCount++) {
									DecimalFormat format = new DecimalFormat("0.0");
									compiledValue[choiceCount] = range[choiceCount] + " " +
											format.format((counts[choiceCount] / 
													matchData.length) * 100) + "%";

									chooserCounts[choiceCount] = (int)counts[choiceCount];
								}
							}
							
							break;
						
						case DBContract.SLIDER:
							//Do the same things for sliders and counters
						case DBContract.COUNTER:
						
							double numerator = 0;
							double denominator = 0;
							boolean valueIsNull = true;
						
							for(int matchCount = 0; matchCount < matchData.length; 
									matchCount++) {
								
								//Comments
								if(!isCommentsFilled)
									comments[matchCount] = matchData[matchCount].
										getComments();
								
								//Matches played
								if(!isMatchesPlayedFilled)
									matchesPlayed[matchCount] = matchData[matchCount].
										getMatchNumber();
								
								String[] valueArray = matchData[matchCount].
										getMetricValues()[metricCount].getValue();
								
								if(valueArray.length > 0) {
									valueIsNull = false;
									
									int value = Integer.parseInt(valueArray[0]);
									double weight = Math.pow
											(weightingRatio, 
													matchCount);
								
									numerator += value * weight;
									denominator += weight;
								}
							}
							
							if(valueIsNull) {
								//compiledValue = new String[] {"-1"};
							} else {
								if(denominator == 0)
									denominator = 1;
								
								double weightedAverage = numerator / denominator;
								compiledValue = new String[] {Double.toString
										(weightedAverage)};
							}
							
							break;
						
						case DBContract.MATH:
							int nonNullMatchCount = 0;
							double mathNumerator = 0;
							double mathDenominator = 0;
							boolean allValuesAreNull = true;
							boolean matchValuesAreNull = true;
							Metric mathMetric = metrics[metricCount];
							
							for(int matchCount = 0; matchCount < matchData.length; 
									matchCount++) {
								double matchVal = 0;
								matchValuesAreNull = true;
								
								for(int mathMetricCount = 0; mathMetricCount < 
										mathMetric.getRange().length; mathMetricCount++) {
									MetricValue val = null;
									MetricValue[] mArr = matchData[matchCount].
											getMetricValues();
									for(MetricValue m : mArr)
										if(m.getMetric().getID() == Integer.parseInt
											((String)mathMetric.getRange()
													[mathMetricCount])) {
											val = m;
											break;
										}
									
									double thisVal = 0;
									
									try {
										if(val != null) {
											thisVal = Double.parseDouble(val.getValue()[0]);
											allValuesAreNull = false;
											matchValuesAreNull = false;
											nonNullMatchCount++;
										}
									} catch(NumberFormatException e) {}
									catch(ArrayIndexOutOfBoundsException e) {
										e.printStackTrace();
									}
									
									matchVal += thisVal;
								}
								
								if(!matchValuesAreNull) {
									double weight = Math.pow
											(weightingRatio, nonNullMatchCount);
									mathNumerator += matchVal * weight;
									mathDenominator += weight;
								}
							}
							
							if(allValuesAreNull) {
								
							} else {
								if(mathDenominator == 0)
									mathDenominator = 1;

								double weightedAverage = mathNumerator / mathDenominator;
								compiledValue = new String[] {Double.toString
										(weightedAverage)};
							}

							break;
							
						case DBContract.TEXT:
							
							compiledValue = new String[matchData.length];
							
							for(int matchCount = 0; matchCount < matchData.length; 
									matchCount++) {
								
								//Comments
								if(!isCommentsFilled)
									comments[matchCount] = matchData[matchCount].
										getComments();
								
								//Matches played
								if(!isMatchesPlayedFilled)
									matchesPlayed[matchCount] = matchData[matchCount].
										getMatchNumber();
								
								String stringValue = matchData[matchCount].
										getMetricValues()[metricCount].
										getValueAsHumanReadableString();
								
								if(stringValue != null)
									compiledValue[matchCount] = stringValue;
							}
					}
				}
				
				try {
					metricVals[metricCount] = new MetricValue
							(metrics[metricCount], compiledValue, chooserCounts);
				} catch(MetricTypeMismatchException e) {
					e.printStackTrace();
				}
				
				isCommentsFilled = true;
				isMatchesPlayedFilled = true;
			}
			
			compiledData[robotCount] = new CompiledData(
					eventID,
					matchesPlayed,
					robots[robotCount],
					comments,
					metricVals,
					new MetricValue[0]
					);
			
		}
		
		if(querys != null && querys.length != 0) {
			//Create an ArrayList for teams that fit the query
			ArrayList<CompiledData> selectedRobots = new ArrayList<CompiledData>();

			for(CompiledData robot : compiledData) {
				boolean passed = true;

				for(Query query : querys) {
					//Get the MetricValue to compare against our cuttoff
					MetricValue metricValue = null;

					switch(query.getType()) {
					case Query.TYPE_ROBOT:
						for(int i = 0; i < robot.getRobot().getMetricValues().
								length; i++) {
							if(robot.getRobot().getMetricValues()[i].getMetric().
									getID() == query.getMetricID()) {
								metricValue = robot.getRobot().getMetricValues()[i];
								break;
							}
						}

						break;

					case Query.TYPE_MATCH_DATA:
						for(int i = 0; i < robot.getCompiledMatchData().
								length; i++) {
							if(robot.getCompiledMatchData()[i].getMetric().
									getID() == query.getMetricID()) {
								metricValue = robot.getCompiledMatchData()[i];
								break;
							}
						}
						break;

					case Query.TYPE_DRIVER_DATA:
						for(int i = 0; i < robot.getCompiledDriverData().
								length; i++) {
							if(robot.getCompiledMatchData()[i].getMetric().
									getID() == query.getMetricID()) {
								metricValue = robot.getCompiledDriverData()[i];
								break;
							}
						}
						break;
					}

					//Compare with the correct comparison
					boolean chooserIsNumeric = true;
					if(metricValue.getMetric().getType() == 
							DBContract.CHOOSER) {
						for(Object o : metricValue.getMetric().getRange())
							try {
								Double.parseDouble(o.toString());
							} catch(NumberFormatException e) {
								chooserIsNumeric = false;
								break;
							}
					} else {
						chooserIsNumeric = false;
					}
					
					switch(query.getComparison()) {
					case Query.COMPARISON_EQUAL_TO:
						if(metricValue.getMetric().getType() == DBContract.COUNTER 
						|| metricValue.getMetric().getType() == DBContract.SLIDER 
						|| metricValue.getMetric().getType() == DBContract.MATH
						|| (metricValue.getMetric().getType() == DBContract.BOOLEAN 
							&& query.getType() == Query.TYPE_MATCH_DATA)
						|| chooserIsNumeric) {

							try {
								double checkValue = Double.parseDouble
										(query.getMetricValue());
								double robotValue = Double.parseDouble
										(metricValue.getValueAsHumanReadableString());

								if(checkValue != robotValue)
									passed = false;

							} catch(NumberFormatException e) {
								passed = false;
							}

						} else {
							if(!metricValue.getValueAsHumanReadableString().
									equalsIgnoreCase(query.getMetricValue()))
								passed = false;
						}
						break;

					case Query.COMPARISON_LESS_THAN:
						if(metricValue.getMetric().getType() == DBContract.COUNTER 
						|| metricValue.getMetric().getType() == DBContract.SLIDER 
						|| metricValue.getMetric().getType() == DBContract.MATH
						|| (metricValue.getMetric().getType() == DBContract.BOOLEAN 
							&& query.getType() == Query.TYPE_MATCH_DATA)
						|| chooserIsNumeric) {

							try {
								double checkValue = Double.parseDouble
										(query.getMetricValue());
								double robotValue = Double.parseDouble
										(metricValue.getValueAsHumanReadableString());

								if(checkValue <= robotValue)
									passed = false;

							} catch(NumberFormatException e) {
								passed = false;
							}
						}
						break;

					case Query.COMPARISON_GREATER_THAN:
						if(metricValue.getMetric().getType() == DBContract.COUNTER 
						|| metricValue.getMetric().getType() == DBContract.SLIDER 
						|| metricValue.getMetric().getType() == DBContract.MATH
						|| (metricValue.getMetric().getType() == DBContract.BOOLEAN 
							&& query.getType() == Query.TYPE_MATCH_DATA)
						|| chooserIsNumeric) {
							try{
								double checkValue = Double.parseDouble
										(query.getMetricValue());
								double robotValue = Double.parseDouble
										(metricValue.getValueAsHumanReadableString());

								if(checkValue >= robotValue)
									passed = false;

							} catch(NumberFormatException e) {
								passed = false;
							}
						}
						break;
						
					case Query.COMPARISON_CHOOSER_COMPARE:
						if(metricValue.getMetric().getType() == DBContract.CHOOSER) {
							Object[] range = metricValue.getMetric().getRange();
							int[] chooserCounts = metricValue.getChooserCounts();
							int totalCounts = 0;
							int highestCountValue = -1;
							int highestCountPos = -1;
							
							for(int i = 0; i < chooserCounts.length; i++) {
								totalCounts += chooserCounts[i];
								if(highestCountValue < chooserCounts[i]) {
									highestCountValue = chooserCounts[i];
									highestCountPos = i;
								}
							}
							
							if(highestCountPos != -1 && totalCounts > 0 &&
									totalCounts / 2 <= highestCountValue && 
									query.getMetricValue().equals(range[highestCountPos])) {
								passed = true;
							} else {
								passed = false;
							}
						}
						break;
					}
				}

				if(passed)
					selectedRobots.add(robot);
			}
			
			compiledData = selectedRobots.toArray(new CompiledData[0]);
		}
		
		//Sort selectedRobots according to the specified key
		if(sortKey != null && compiledData.length > 0) {
			double[] sortValues = new double[compiledData.length];
			
			switch(sortKey.getMetricType()) {
				case SortKey.MATCH_METRIC_TYPE:
					int metricAdress = -1;
					for(int i = 0; i < compiledData[0].getCompiledMatchData().length; i++) {
						if(compiledData[0].getCompiledMatchData()[i].
								getMetric().getID() == sortKey.getMetricID()) {
							metricAdress = i;
							break;
						}
					}
					
					if(metricAdress == -1)
						break;	//Break if this metric is not in the comp data
						
					for(int i = 0; i < compiledData.length; i++) {
						try {
							sortValues[i] = Double.parseDouble(compiledData[i].
									getCompiledMatchData()[metricAdress].getValue()[0]);
						} catch(ArrayIndexOutOfBoundsException e) {
							sortValues[i] = -1.0;
						}
					}
					break;
					
				case SortKey.PIT_METRIC_TYPE:
					int mAddress = -1;
					for(int i = 0; i < compiledData[0].getRobot().getMetricValues().length; i++) {
						if(compiledData[0].getRobot().getMetricValues()[i].
								getMetric().getID() == sortKey.getMetricID()) {
							mAddress = i;
							break;
						}
					}
					Log.d("FRCKrawler", "mAddress: " + mAddress);
					
					if(mAddress == -1)
						break;	//Break if this metric is not in the comp data
						
					for(int i = 0; i < compiledData.length; i++) {
						try {
							sortValues[i] = Double.parseDouble(compiledData[i].getRobot()
									.getMetricValues()[mAddress].getValue()[0]);
						} catch(ArrayIndexOutOfBoundsException e) {
							sortValues[i] = -1.0;
							Log.e("FRCKrawler", e.getMessage());
						}
					}
					break;
					
				case SortKey.OPR_TYPE:
					for(int i = 0; i < compiledData.length; i++)
						sortValues[i] = compiledData[i].getRobot().getOPR();
					break;
			}
			
			CompiledDataSorter sorter = new CompiledDataSorter();
			sorter.sort(sortValues, compiledData);
		}
		
		
		return compiledData;
	}
	
	
	
	/*****
	 * Method: printQuery
	 * 
	 * Summary: This method is used for the purpose of debugging. It allows the developer to make queries to the SQLite
	 * database and it prints the results in the console.
	 *****/
	
	public synchronized void printQuery(String query, String[] selectionArgs) {
		
		try {
			
			Cursor c = helper.getReadableDatabase().rawQuery(query, selectionArgs);	//Make the query
			
			String cols = new String();	//String to store the column names
			
			for(int colCount = 0; colCount < c.getColumnCount(); colCount++)	//Cylce through all columns
				cols += c.getColumnName(colCount) + " : ";	//Add each column name to the string
			
			System.out.println(cols);	//Print it
			
			for(int rowCount = 0; rowCount < c.getCount(); rowCount++) {	//Cycle through all rows
				
				c.moveToPosition(rowCount);
				String output = new String();	//Stores the each columns data
				
				for(int colCount = 0; colCount < c.getColumnCount(); colCount++)//Cycle through all columns
					output += c.getString(colCount) + " : ";	//Add this piece of data to the row output
				
				System.out.println(output);	//Print it
			}
			
		} catch(CursorIndexOutOfBoundsException exception) {
			Log.e("FRCKrawler", "No results returned from query.");
			
		} catch(Exception e) {
			Log.e("FRCKrawler", "Error in qeury.");
			
		} finally {
			
			helper.close();
		}
	}
	
	
	/*****
	 * Method: createID
	 * 
	 * @param table
	 * @param column
	 * @return
	 * 
	 * Summary: Creates a unique integer ID from numbers 0 to 999. Used to 
	 * create competition IDs and user IDs.
	 * 
	 * @return A new unique ID, or -1 if there was an error in its
	 * creation.
	 *****/
	
	protected int createID(String table, String column) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		try {
			
			Cursor c = db.query(table, 	//Get IDs that are already used
					new String[] {column}, 
					null, null, null, null, null);
		
			int newID;
			boolean isTaken;
		
			do {	//Check the ID at least once
			
				newID = (int)(Math.random() * 10000000);
				isTaken = false;
				c.moveToPosition(-1);
			
				while(c.moveToNext()) {	//While there are still IDs to check...
				
					if(c.getInt(c.getColumnIndex(column)) == newID)	//If the ID is taken...
					{
						isTaken = true;	//Set isTaken to true so that the a new ID is made
						break;
					}
				}
			
			} while(isTaken);
			
			return newID;
			
		} catch(SQLiteException e) {
			
			return -1;
			
		} catch(Exception e) {
			
			return -1;
		}
	}
	
	
	/*****
	 * Method: hasValue
	 * 
	 * @param table
	 * @param column
	 * @param value
	 * 
	 * Summary: Checks to see if the entered table has the value in the column.
	 * 
	 * @return True if the value is in the column, false if it is not.
	 *****/
	
	protected boolean hasValue(String table, String column, String value) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(table, 
				new String[] {column}, 
				column + " LIKE ?", 
				new String[] {value},
				null, null, null);
		
		return c.moveToFirst();
	}
	
	
	/*****
	 * Method: addList
	 * 
	 * Summary: Enters the List object into the database.
	 *****/
	
	public synchronized boolean addList(List list) {
		if(!hasValue(DBContract.TABLE_EVENTS, DBContract.COL_EVENT_ID, 
				Integer.toString(list.getEventID())))
			return false;
		
		for(Robot r : list.getRobots()) {
			if(!hasValue(DBContract.TABLE_ROBOTS, DBContract.COL_ROBOT_ID, 
					Integer.toString(r.getID())))
				return false;
		}
		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_EVENT_ID, list.getEventID());
		values.put(DBContract.COL_LIST_ID, this.createID(DBContract.TABLE_LISTS, 
				DBContract.COL_LIST_ID));
		values.put(DBContract.COL_LIST_NAME, list.getName());
		values.put(DBContract.COL_DESCRIPTION, list.getDescription());
		db.insert(DBContract.TABLE_LISTS, null, values);
		
		for(Robot r : list.getRobots()) {
			addRobotToList(r.getID(), list.getListID());
		}
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: removeList
	 * 
	 * Summary: removes the specified list from the database
	 ****/
	
	public synchronized boolean removeList(int listID) {
		if(!hasValue(DBContract.TABLE_LISTS, DBContract.COL_LIST_ID, 
				Integer.toString(listID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(DBContract.TABLE_LISTS, DBContract.COL_LIST_ID + " LIKE " + listID, null);
		db.delete(DBContract.TABLE_LIST_ENTRIES, DBContract.COL_LIST_ID + " LIKE " + listID, 
				null);
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: addRobotToList
	 * 
	 * Summary: Adds the specified robot to the specified list. The robot must
	 * be at the same event as the list and the list must exist. Returns false
	 * if the robot could not be added.
	 *****/
	
	public synchronized boolean addRobotToList(int robotID, int listID) {
		if(!hasValue(DBContract.TABLE_ROBOTS, DBContract.COL_ROBOT_ID, 
				Integer.toString(robotID)) || !hasValue(DBContract.TABLE_LISTS,
						DBContract.COL_LIST_ID, Integer.toString(listID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if(db.rawQuery("SELECT * FROM " + DBContract.TABLE_LIST_ENTRIES + 
				" WHERE " + DBContract.COL_LIST_ID + " LIKE " + listID + 
				" AND " + DBContract.COL_ROBOT_ID + " LIKE " + robotID, null).getCount() > 0)
			return false;
	
		Cursor c = db.rawQuery("SELECT * FROM " + DBContract.TABLE_LIST_ENTRIES + 
				" WHERE " + DBContract.COL_LIST_ID + " LIKE " + listID, null);
		
		int highestListPos = 0;
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			
			if(c.getInt(c.getColumnIndex(DBContract.COL_POSITION)) > highestListPos)
				highestListPos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
		}
		
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_LIST_ID, listID);
		values.put(DBContract.COL_ROBOT_ID, robotID);
		values.put(DBContract.COL_POSITION, highestListPos + 1);
		
		db.insert(DBContract.TABLE_LIST_ENTRIES, null, values);
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: removeRobotFromList
	 * 
	 * Summary: Removes the specified robot from the specified list
	 *****/
	
	public synchronized void removeRobotFromList(int robotID, int listID) {
		helper.getWritableDatabase().delete(DBContract.TABLE_LIST_ENTRIES, 
				DBContract.COL_ROBOT_ID + " LIKE ? AND " + DBContract.COL_LIST_ID + " LIKE ?", 
				new String[] {Integer.toString(robotID), Integer.toString(listID)});
		helper.close();
	}
	
	
	/*****
	 * Method: getListsByColumns
	 * 
	 * Summary: Gets the lists from the database based on the cols and vals array
	 ******/
	
	public synchronized List[] getListsByColumns(String[] cols, String[] vals) {
		return getListsByColumns(cols, vals, false);
	}
	
	public synchronized List[] getListsByColumns(String[] cols, String[] vals, boolean isOr) {
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new List[0];
		
		String queryString = "SELECT * FROM " + DBContract.TABLE_LISTS + 
				" WHERE " + cols[0] + " LIKE ?";
		String orAnd;
		
		if(isOr)
			orAnd = " OR ";
		else
			orAnd = " AND ";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with coos
			queryString += orAnd + cols[i] + " LIKE ?";
			
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery(queryString, vals);
		List[] lists = new List[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			db = helper.getWritableDatabase();
			c.moveToNext();
			
			Cursor robotC = db.rawQuery("SELECT * FROM " + DBContract.TABLE_LIST_ENTRIES + 
					" WHERE " + DBContract.COL_LIST_ID + " LIKE " + 
					c.getInt(c.getColumnIndex(DBContract.COL_LIST_ID)) +
					" ORDER BY " + DBContract.COL_POSITION, null);
			Robot[] robots = new Robot[robotC.getCount()];
			
			for(int k = 0; k < robots.length; k++) {
				robotC.moveToNext();
				
				Robot[] r = getRobotsByColumns(new String[] {DBContract.COL_ROBOT_ID},
						new String[] {robotC.getString(
								robotC.getColumnIndex(DBContract.COL_ROBOT_ID))});
				
				if(r.length > 0)
					robots[k] = r[0];
			}
			
			lists[i] = new List(c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_LIST_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_LIST_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_DESCRIPTION)),
					robots);
		}
		
		helper.close();
		return lists;
	}
	
	
	/*****
	 * Method: flipRobotPositionInList
	 * 
	 * Summary: Flips the position value of the two robots in the specified list.
	 *****/
	
	public synchronized boolean flipRobotPositionInList(int listID, 
			int robotID1, int robotID2) {
		if(!hasValue(DBContract.TABLE_LISTS, DBContract.COL_LIST_ID, Integer.toString(listID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + DBContract.TABLE_LIST_ENTRIES + 
				" WHERE (" + DBContract.COL_ROBOT_ID + " LIKE " + robotID1 + 
				" OR " + DBContract.COL_ROBOT_ID + " LIKE " + robotID2 + ") AND " + 
				DBContract.COL_LIST_ID + " LIKE " + listID, null);
		
		if(c.getCount() != 2)
			return false;
		
		int robot1ID;
		int robot1Pos;
		int robot2ID;
		int robot2Pos;
		
		c.moveToNext();
		robot1ID = c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID));
		robot1Pos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
		
		c.moveToNext();
		robot2ID = c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID));
		robot2Pos = c.getInt(c.getColumnIndex(DBContract.COL_POSITION));
		
		ContentValues robot1Vals = new ContentValues();
		robot1Vals.put(DBContract.COL_POSITION, robot2Pos);
		db.update(DBContract.TABLE_LIST_ENTRIES, robot1Vals, 
				DBContract.COL_ROBOT_ID + " LIKE " + robot1ID + 
				" AND " + DBContract.COL_LIST_ID + " LIKE " + listID, null);
		
		ContentValues robot2Vals = new ContentValues();
		robot2Vals.put(DBContract.COL_POSITION, robot1Pos);
		db.update(DBContract.TABLE_LIST_ENTRIES, robot2Vals, 
				DBContract.COL_ROBOT_ID + " LIKE " + robot2ID + 
				" AND " + DBContract.COL_LIST_ID + " LIKE " + listID, null);
		
		return true;
	}
	
	
	
	/************************************************************************
	 * SCOUT METHODS
	 * 
	 * Summary: These methods interact with the scout tables rather than
	 * the main database tables. The data in these tables is the data 
	 * displayed when using the individual scout's interface.
	 ************************************************************************/
	
	public synchronized Event scoutGetEvent() {
		
		Event event;
		
		Cursor c = helper.getReadableDatabase().
				rawQuery("SELECT * FROM " + DBContract.SCOUT_TABLE_EVENT, null);
		if(c.moveToFirst()) {
		
			event = new Event(
				c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
				c.getString(c.getColumnIndex(DBContract.COL_EVENT_NAME)),
				c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
				new Date(c.getLong(c.getColumnIndex(DBContract.COL_DATE_STAMP))),
				c.getString(c.getColumnIndex(DBContract.COL_LOCATION)),
				c.getString(c.getColumnIndex(DBContract.COL_FMS_EVENT_ID))
				);
			
			return event;
		}
		
		helper.close();
		return null;
	}
	
	
	/*****
	 * Method: scoutUpdateEvent
	 * 
	 * @param e
	 * 
	 * Summary: Sets the event in the scout's event table
	 * to the specified event.
	 */
	
	public synchronized void scoutReplaceEvent(Event e) {
		
		if(e == null)
			return;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		db.execSQL("DELETE FROM " + DBContract.SCOUT_TABLE_EVENT);
		
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_EVENT_ID, e.getEventID());
		values.put(DBContract.COL_EVENT_NAME, e.getEventName());
		values.put(DBContract.COL_GAME_NAME, e.getGameName());
		values.put(DBContract.COL_LOCATION, e.getLocation());
		
		if(e.getDateStamp() != null)	//If a date has been set...
			values.put(DBContract.COL_DATE_STAMP, e.getDateStamp().getTime());
		
		db.insert(DBContract.SCOUT_TABLE_EVENT, null, values);
		
		helper.close();
	}
	
	
	/*****
	 * Method: scoutGetAllUsers
	 * 
	 * @return
	 * 
	 * Summary: Gets all the users in the scout's user table
	 */
	
	public synchronized User[] scoutGetAllUsers() {
		
		Cursor c = helper.getReadableDatabase().rawQuery(
				"SELECT * FROM " + DBContract.SCOUT_TABLE_USERS, null);
		User[] u = new User[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			u[i] = new User(
					c.getString(c.getColumnIndex(DBContract.COL_USER_NAME)),
					false,
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID))
					);
		}
		
		helper.close();
		
		return u;
	}
	
	
	/*****
	 * Method: scoutUpdateUsers
	 * @param name
	 * @param superuser
	 *****/
	
	public synchronized void scoutReplaceUsers(User[] users) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		db.delete(DBContract.SCOUT_TABLE_USERS, null, null);
		
		for(int i = 0; i < users.length; i++) {
			
			ContentValues values = new ContentValues();
			values.put(DBContract.COL_USER_ID, users[i].getID());
			values.put(DBContract.COL_USER_NAME, users[i].getName());
			
			db.insert(DBContract.SCOUT_TABLE_USERS, null, values);
		}
		
		helper.close();
	}
	
	
	public synchronized String[] scoutGetAllTeamNames() {
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT " + 
				DBContract.COL_TEAM_NAME + " FROM " + 
				DBContract.SCOUT_TABLE_ROBOTS + 
				" ORDER BY " + DBContract.COL_TEAM_NUMBER + " ASC",
				null);
		String[] names = new String[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			names[i] = c.getString(c.getColumnIndex(DBContract.COL_TEAM_NAME));
		}
		
		c.close();
		return names;
	}
	
	
	/*****
	 * Method: scoutGetAllRobots
	 * 
	 * Summary: gets all of the teams from the scout's team table
	 */
	
	public synchronized Robot[] scoutGetAllRobots() {
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.SCOUT_TABLE_ROBOTS + " ORDER BY " +
				DBContract.COL_TEAM_NUMBER + " ASC", null);
		Robot[] r = new Robot[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			ArrayList<MetricValue> metricVals = new ArrayList<MetricValue>();
			Metric[] metrics = new Metric[0];
			
			metrics = scoutGetAllRobotMetrics();
				
			//Un-indent this stuff
			for(int metricCount = 0; metricCount < metrics.length; metricCount++) {
				String valString = c.getString
						(c.getColumnIndex(metrics[metricCount].getKey()));
				ArrayList<String> valsArr = new ArrayList<String>();
				String workingString = new String();
					
				if(valString == null)
					valString = new String();
					
				for(int charCount = 0; charCount < valString.length(); charCount++) {
						
					if(valString.charAt(charCount) != ':'){
						workingString += valString.substring(charCount, charCount + 1);
							
					} else {
						valsArr.add(workingString);
						workingString = new String();
					}
				}
					
				try {
					metricVals.add(
						new MetricValue(
							metrics[metricCount],
							valsArr.toArray(new String[0])
						));
				} catch(MetricTypeMismatchException e) {
					System.out.println(e.getMessage());
				}
			}
			
			
			r[i] = new Robot(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					c.getString(c.getColumnIndex(DBContract.COL_IMAGE_PATH)),
					c.getDouble(c.getColumnIndex(DBContract.COL_OPR)),
					metricVals.toArray(new MetricValue[0])
					);
		}
		
		c.close();
		return r;
	}
	
	
	/*****
	 * Method: scoutGetUpdatedRobots
	 * 
	 * Summary: gets all robots in the scout's database who's
	 * wasupdated column is true
	 */
	
	public synchronized Robot[] scoutGetUpdatedRobots() {
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.SCOUT_TABLE_ROBOTS + " WHERE " + 
				DBContract.COL_WAS_UPDATED + " LIKE ?", 
				new String[] {"1"});
		Robot[] robots = new Robot[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			ArrayList<MetricValue> metricVals = new ArrayList<MetricValue>();
			Metric[] metrics = new Metric[0];
			
			metrics = scoutGetAllRobotMetrics();
				
			//Un-indent this stuff
			for(int metricCount = 0; metricCount < metrics.length; metricCount++) {
				
				String valString = c.getString
						(c.getColumnIndex(metrics[metricCount].getKey()));
				ArrayList<String> valsArr = new ArrayList<String>();
				String workingString = new String();
				
				if(valString == null)
					valString = new String();
				
				for(int charCount = 0; charCount < valString.length(); charCount++) {
					if(valString.charAt(charCount) != ':'){
						workingString += valString.substring(charCount, charCount + 1);
							
					} else {
						valsArr.add(workingString);
						workingString = new String();
					}
				}
				
				try {
					metricVals.add(
						new MetricValue(
							metrics[metricCount],
							valsArr.toArray(new String[0])
						));
				} catch(MetricTypeMismatchException e) {
					System.out.println(e.getMessage());
				}
			}
			
			robots[i] = new Robot(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					c.getString(c.getColumnIndex(DBContract.COL_IMAGE_PATH)),
					c.getDouble(c.getColumnIndex(DBContract.COL_OPR)),
					metricVals.toArray(new MetricValue[0])
					);
		}
		
		helper.close();
		return robots;
	}
	
	
	/*****
	 * Method: scoutReplaceAllRobots
	 * 
	 * Summary: removes all the robots in the scout's database and replaces
	 * them with the specified list of robots
	 */
	
	public synchronized boolean scoutReplaceRobots(Robot[] robots, String[] teamNames) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		db.execSQL("DELETE FROM " + DBContract.SCOUT_TABLE_ROBOTS);
		
		for(int i = 0; i < robots.length; i++) {
			
			ContentValues values = new ContentValues();
			values.put(DBContract.COL_TEAM_NUMBER, robots[i].getTeamNumber());
			values.put(DBContract.COL_GAME_NAME, robots[i].getGame());
			values.put(DBContract.COL_ROBOT_ID, robots[i].getID());
			values.put(DBContract.COL_COMMENTS, robots[i].getComments());
			values.put(DBContract.COL_IMAGE_PATH, robots[i].getImagePath());
			values.put(DBContract.COL_WAS_UPDATED, "0");
			values.put(DBContract.COL_TEAM_NAME, teamNames[i]);
			values.put(DBContract.COL_OPR, robots[i].getOPR());
			
			for(MetricValue v : robots[i].getMetricValues()) {
				
				String valString = new String();
				
				for(String val : v.getValue())
					valString += val + ":";
				
				values.put(v.getMetric().getKey(), valString);
			}
			
			db.insert(DBContract.SCOUT_TABLE_ROBOTS, null, values);
		}
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: scoutUpdateRobots
	 * 
	 * @param queryCols
	 * @param queryVals
	 * @param updateCols
	 * @param updateVals
	 * @return
	 * 
	 * Summary: updates robots on the scout's database
	 */
	
	//! This method does not update the game, id, or team number of the robot
	public synchronized boolean scoutUpdateRobot(Robot robot) {
		
		if(robot == null)
			return false;
		
		ArrayList<String> updateCols = new ArrayList<String>();
		ArrayList<String> updateVals = new ArrayList<String>();
		
		updateCols.add(DBContract.COL_COMMENTS);
		updateVals.add(robot.getComments());
		
		MetricValue[] metricVals = robot.getMetricValues();
		
		for(int i = 0; i < metricVals.length; i++) {
			updateVals.add(metricVals[i].getValueAsDBReadableString());
			updateCols.add(metricVals[i].getMetric().getKey());
		}
		
		return scoutUpdateRobots(
				new String[] {DBContract.COL_ROBOT_ID},
				new String[] {Integer.toString(robot.getID())},
				updateCols.toArray(new String[0]),
				updateVals.toArray(new String[0])
				);
	}
	
	public synchronized boolean scoutUpdateRobots(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) //Does not allow the caller to update the team number.
			if(DBContract.COL_ROBOT_ID.equals(s))
				return false;
		
		ContentValues vals = new ContentValues();
		vals.put(DBContract.COL_WAS_UPDATED, "1");
		
		for(int i = 0; i < updateVals.length; i++)
			vals.put(updateCols[i], updateVals[i]);
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) 
			queryString += " AND " + queryCols[i] + " LIKE ?";
		
		helper.getWritableDatabase().update(DBContract.SCOUT_TABLE_ROBOTS, vals, 
				queryString, queryVals);
		helper.close();
		
		printQuery("SELECT * FROM " + DBContract.SCOUT_TABLE_ROBOTS, null);
		
		return true;
	}
	
	
	/*****
	 * Method: scoutReplaceRobotMetrics
	 * 
	 * Summary: replaces all robot metrics in the scout's database
	 */
	
	public synchronized void scoutReplaceRobotMetrics(Metric[] metrics) {
		
		helper.getWritableDatabase().execSQL("DELETE FROM " + 
				DBContract.SCOUT_TABLE_ROBOT_METRICS);
		
		for(int i = 0; i < metrics.length; i++) {
			
			String rangeInput = new String();
			
			if(metrics[i].getRange() != null) {
			
				for(Object r :  metrics[i].getRange())	//Create the string for the range.
					rangeInput += r.toString() + ":";	//The range is stored in one cell.
			}
			
			ContentValues values = new ContentValues();	//New values for the MATCH_PERF_METRICS
			values.put(DBContract.COL_METRIC_ID, metrics[i].getID());
			values.put(DBContract.COL_METRIC_NAME, metrics[i].getMetricName());
			values.put(DBContract.COL_GAME_NAME, metrics[i].getGameName());
			values.put(DBContract.COL_TYPE, metrics[i].getType());
			values.put(DBContract.COL_RANGE, rangeInput);
			values.put(DBContract.COL_DESCRIPTION, metrics[i].getDescription());
			values.put(DBContract.COL_DISPLAY, metrics[i].isDisplayed());
			values.put(DBContract.COL_METRIC_KEY, metrics[i].getKey());
			values.put(DBContract.COL_POSITION, metrics[i].getPosition());
			
			helper.getWritableDatabase().insert
					(DBContract.SCOUT_TABLE_ROBOT_METRICS, null, values);
		}
		
		helper.close();
	}
	
	
	/*****
	 * Method: scoutGetAllRobotMetrics
	 * 
	 * Summary: gets all the metrics from the scout's part of the database
	 */
	
	public synchronized Metric[] scoutGetAllRobotMetrics() {
		
		Cursor c = helper.getReadableDatabase().rawQuery
				("SELECT * FROM " + DBContract.SCOUT_TABLE_ROBOT_METRICS
				 + " ORDER BY " + DBContract.COL_POSITION + " ASC", null);
		Metric[] m = new Metric[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToNext();
			
			String rangeString = c.getString(c.getColumnIndex(DBContract.COL_RANGE));
			ArrayList<Object> rangeArrList = new ArrayList<Object>();
			
			String currentRangeValString = new String();
			
			for(int character = 0; character < rangeString.length(); character++) {
				
				if(rangeString.charAt(character) != ':')
					currentRangeValString += rangeString.charAt(character);
				
				else {
					rangeArrList.add(currentRangeValString);
					currentRangeValString = new String();
				}
			}
			
			m[i] = new Metric(
					c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_DESCRIPTION)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)),
					c.getInt(c.getColumnIndex(DBContract.COL_TYPE)),
					rangeArrList.toArray(),
					(c.getInt(c.getColumnIndex(DBContract.COL_DISPLAY)) > 0),
					c.getInt(c.getColumnIndex(DBContract.COL_POSITION))
					);
		}
		
		helper.close();
		
		return m;
	}
	
	
	/*****
	 * Method: scoutReplaceMatchMetrics
	 * 
	 * @param metrics
	 * 
	 * Summary: replaces all the match metrics in the scouting database
	 */
	
	public synchronized void scoutReplaceMatchMetrics(Metric[] metrics) {
		
		helper.getWritableDatabase().execSQL("DELETE FROM " + 
				DBContract.SCOUT_TABLE_MATCH_PERF_METRICS);
		
		for(int i = 0; i < metrics.length; i++) {
			
			String rangeInput = new String();
			
			if(metrics[i].getRange() != null) {
			
				for(Object r :  metrics[i].getRange())	//Create the string for the range.
					rangeInput += r.toString() + ":";	//The range is stored in one cell.
			}
			
			ContentValues values = new ContentValues();	//New values for the MATCH_PERF_METRICS
			values.put(DBContract.COL_METRIC_ID, metrics[i].getID());
			values.put(DBContract.COL_METRIC_NAME, metrics[i].getMetricName());
			values.put(DBContract.COL_GAME_NAME, metrics[i].getGameName());
			values.put(DBContract.COL_TYPE, metrics[i].getType());
			values.put(DBContract.COL_RANGE, rangeInput);
			values.put(DBContract.COL_DESCRIPTION, metrics[i].getDescription());
			values.put(DBContract.COL_DISPLAY, metrics[i].isDisplayed());
			values.put(DBContract.COL_METRIC_KEY, metrics[i].getKey());
			values.put(DBContract.COL_POSITION, metrics[i].getPosition());
			
			helper.getWritableDatabase().insert
					(DBContract.SCOUT_TABLE_MATCH_PERF_METRICS, null, values);
		}
		
		helper.close();
	}
	
	
	/*****
	 * Method: scoutGetAllMatchMetrics
	 * 
	 * Summary: gets all match metrics from the scouting database
	 */
	public synchronized Metric[] scoutGetAllMatchMetrics() {
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.SCOUT_TABLE_MATCH_PERF_METRICS + 
				" ORDER BY " + DBContract.COL_POSITION + " ASC", null);
		Metric[] metrics = new Metric[c.getCount()];
		
		for(int i = 0; i < metrics.length; i++) {
			c.moveToNext();
			
			String rangeString = c.getString(c.getColumnIndex(DBContract.COL_RANGE));
			ArrayList<Object> rangeArrList = new ArrayList<Object>();
			
			String currentRangeValString = new String();
			
			for(int character = 0; character < rangeString.length(); character++) {
				
				if(rangeString.charAt(character) != ':')
					currentRangeValString += rangeString.charAt(character);
				
				else {
					rangeArrList.add(currentRangeValString);
					currentRangeValString = new String();
				}
			}
			
			metrics[i] = new Metric(
					c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_DESCRIPTION)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)),
					c.getInt(c.getColumnIndex(DBContract.COL_TYPE)),
					rangeArrList.toArray(),
					(c.getInt(c.getColumnIndex(DBContract.COL_DISPLAY)) > 0),
					c.getInt(c.getColumnIndex(DBContract.COL_POSITION))
					);
		}
		
		helper.close();
		return metrics;
	}
	
	
	/*****
	 * Method: scoutGetAllMatchData
	 * 
	 * @return
	 * 
	 * Summary: gets all of the scout's match data from the database
	 */
	
	public synchronized MatchData[] scoutGetAllMatchData() {
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.SCOUT_TABLE_MATCH_PERF, null);
		MatchData[] d = new MatchData[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			Metric [] metricArr = scoutGetAllMatchMetrics();
			MetricValue[] dataArr = new MetricValue[metricArr.length];
			
			for(int k = 0; k < metricArr.length; k++) {
				ArrayList<String> valuesList = new ArrayList<String>();
				String valueString = c.getString(c.getColumnIndex(metricArr[k].getKey()));
				
				String currentValsString = new String();
				
				if(valueString != null) {
					for(int character = 0; character < valueString.length(); character++) {
					
						if(valueString.charAt(character) != ':')
							currentValsString += valueString.charAt(character);
						else {
							valuesList.add(currentValsString);
							currentValsString = new String();
						}
					}
				}
				
				try {
					dataArr[k] = new MetricValue(metricArr[k], valuesList.toArray(new String[0]));
				} catch (MetricTypeMismatchException e) {
					e.printStackTrace();
				}
			}
			
			d[i] = new MatchData(
					c.getInt(c.getColumnIndex(DBContract.COL_DATA_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_MATCH_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_MATCH_TYPE)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					dataArr
					);
		}
		
		return d;
	}
	
	
	/*****
	 * Method: scoutInsertMatchData
	 * 
	 * @param data
	 * @return
	 * 
	 * Summary: puts the given match data into the scout's database
	 */
	
	public synchronized boolean scoutInsertMatchData(MatchData data) {
		
		return scoutInsertMatchData(data.getEventID(), data.getMatchNumber(), data.getRobotID(),
				data.getUserID(), data.getMatchType(), data.getMetricValues(), 
				data.getComments());
	}
	
	public synchronized boolean scoutInsertMatchData(int eventID, int matchNumber, int robotID, 
			int userID, String matchType, MetricValue[] vals, String comments) {
		
		ContentValues values = new ContentValues();
		
		values.put(DBContract.COL_DATA_ID, createID(DBContract.SCOUT_TABLE_MATCH_PERF, 
				DBContract.COL_DATA_ID));
		values.put(DBContract.COL_EVENT_ID, Integer.toString(eventID));
		values.put(DBContract.COL_MATCH_NUMBER, Integer.toString(matchNumber));
		values.put(DBContract.COL_ROBOT_ID, Integer.toString(robotID));
		values.put(DBContract.COL_USER_ID, Integer.toString(userID));
		values.put(DBContract.COL_MATCH_TYPE, matchType);
		values.put(DBContract.COL_COMMENTS, comments);
		
		for(MetricValue val : vals) {
			
			String[] arr = val.getValue();
			String valString = new String();
			
			for(int i = 0; i < arr.length; i++)
				valString += arr[i] + ":";
			
			values.put(val.getMetric().getKey(), valString);
		}
		
		helper.getWritableDatabase().insert(DBContract.SCOUT_TABLE_MATCH_PERF, null, values);
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: scoutGetMatchData
	 * 
	 * Summary: Gets the piece of match data by the specified MatchData ID
	 *****/
	
	public synchronized MatchData scoutGetMatchData(int matchID) {
		if(!this.hasValue(DBContract.SCOUT_TABLE_MATCH_PERF, 
				DBContract.COL_DATA_ID, Integer.toString(matchID)))
			return null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + DBContract.SCOUT_TABLE_MATCH_PERF + 
				" WHERE " + DBContract.COL_DATA_ID + " LIKE ?",
				new String[] {Integer.toString(matchID)});
		c.moveToFirst();
		
		Metric[] metricArr = scoutGetAllMatchMetrics();
		MetricValue[] dataArr = new MetricValue[metricArr.length];
		
		for(int k = 0; k < metricArr.length; k++) {
			ArrayList<String> valuesList = new ArrayList<String>();
			String valueString = c.getString(c.getColumnIndex(metricArr[k].getKey()));
			String currentValsString = new String();
			
			if(valueString != null) {
				for(int character = 0; character < valueString.length(); character++) {
					if(valueString.charAt(character) != ':')
						currentValsString += valueString.charAt(character);
					else {
						valuesList.add(currentValsString);
						currentValsString = new String();
					}
				}
			}
			
			try {
				dataArr[k] = new MetricValue(metricArr[k], valuesList.toArray(new String[0]));
			} catch (MetricTypeMismatchException e) {
				e.printStackTrace();
			}
		}
		
		MatchData data = new MatchData(matchID, 
				c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
				c.getInt(c.getColumnIndex(DBContract.COL_MATCH_NUMBER)),
				c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
				c.getInt(c.getColumnIndex(DBContract.COL_USER_ID)),
				c.getString(c.getColumnIndex(DBContract.COL_MATCH_TYPE)),
				c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
				dataArr
				);
		
		helper.close();
		return data;
	}
	
	
	/*****
	 * Method: scoutRemoveMatchData
	 * 
	 * Summary: Removes one match data entry based on the match id specified
	 *****/
	
	public synchronized void scoutRemoveMatchData(int matchID) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(DBContract.SCOUT_TABLE_MATCH_PERF, 
				DBContract.COL_DATA_ID + " LIKE ?",
				new String[] {Integer.toString(matchID)});
		helper.close();
	}
	
	
	/*****
	 * Method: scoutUpdateMatchData
	 * 
	 * Summary: Updates the passed MatchData object in the database
	 *****/
	
	public synchronized boolean scoutUpdateMatchData(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(s.equals(DBContract.COL_DATA_ID))
				return false;
		}
		
		ContentValues vals = new ContentValues();
		
		for(int i = 0; i < updateCols.length; i++)
			vals.put(updateCols[i], updateVals[i]);
		
		String queryString = new String();
		
		if(queryCols.length > 0)
			queryString += queryCols[0] + " LIKE ?";
		
		for(int i = 1; i < queryCols.length; i++) {
			queryString += " AND " + queryCols[i] + " LIKE ?";
		}
		
		helper.getWritableDatabase().update(DBContract.SCOUT_TABLE_MATCH_PERF, vals, 
				queryString, queryVals);
		helper.close();
		return true;
	}
	
	
	/*****
	 * Method: scoutClearMatchData
	 * 
	 * Summary: removes all match data from the scout's match data
	 * table. Call with care!
	 */
	
	public synchronized void scoutClearMatchData() {
		
		helper.getWritableDatabase().execSQL("DELETE FROM " + 
				DBContract.SCOUT_TABLE_MATCH_PERF);
		helper.close();
	}
	
	
	/*****
	 * Method: scoutClearDriverData
	 * 
	 * Summary: removes all driver data from the scout's database
	 * Call with caution!
	 */
	
	public synchronized void scoutClearDriverData() {
		
		helper.getWritableDatabase().execSQL("DELETE FROM " + 
				DBContract.SCOUT_TABLE_DRIVER_DATA);
		helper.close();
	}
	
	/**********
	 * SUMMARY METHODS
	 * 
	 * These methods handle the database tables where summary data synced
	 * from a server is stored.
	 **********/
	
	
	/*****
	 * Method: summaryGetEvent
	 * 
	 * Summary: gets the sumary's event out of the database
	 *****/
	
	public synchronized Event summaryGetEvent() {
		
		Event event;
		
		Cursor c = helper.getReadableDatabase().
				rawQuery("SELECT * FROM " + DBContract.SUMMARY_TABLE_EVENT, null);
		if(c.moveToFirst()) {
		
			event = new Event(
				c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
				c.getString(c.getColumnIndex(DBContract.COL_EVENT_NAME)),
				c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
				new Date(c.getLong(c.getColumnIndex(DBContract.COL_DATE_STAMP))),
				c.getString(c.getColumnIndex(DBContract.COL_LOCATION)),
				c.getString(c.getColumnIndex(DBContract.COL_FMS_EVENT_ID))
				);
			
			helper.close();
			return event;
		}
		
		helper.close();
		return null;
	}
	
	
	/*****
	 * Method: summarySetEvent
	 * 
	 * @param e
	 * 
	 * Summary: Sets the event in the scout's event table
	 * to the specified event.
	 */
	
	public synchronized void summarySetEvent(Event e) {
		
		if(e == null)
			return;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		db.execSQL("DELETE FROM " + DBContract.SUMMARY_TABLE_EVENT);
		
		ContentValues values = new ContentValues();
		values.put(DBContract.COL_EVENT_ID, e.getEventID());
		values.put(DBContract.COL_EVENT_NAME, e.getEventName());
		values.put(DBContract.COL_GAME_NAME, e.getGameName());
		values.put(DBContract.COL_LOCATION, e.getLocation());
		
		if(e.getDateStamp() != null)	//If a date has been set...
			values.put(DBContract.COL_DATE_STAMP, e.getDateStamp().getTime());
		
		db.insert(DBContract.SUMMARY_TABLE_EVENT, null, values);
		
		helper.close();
	}
	
	
	/*****
	 * Method: summarySetCompiledData
	 * 
	 * Summary: erases all other compiled data from the summary part of the
	 * database and replaces it with the passed information
	 *****/
	
	public synchronized void summarySetCompiledData(CompiledData[] compiledData) {
		
		Robot[] robots = new Robot[compiledData.length];
		
		if(robots.length > 0) {
			//Create the match metrics array
			Metric[] matchMetrics = new Metric[compiledData[0].
			                                   getCompiledMatchData().length];
			for(int i = 0; i < matchMetrics.length; i++)
				matchMetrics[i] = compiledData[0].
					getCompiledMatchData()[i].getMetric();
				
			//Create the robot metrics array
			Metric[] robotMetrics = compiledData[0].getRobot().getMetrics();
			
			//Fill the robots array
			for(int i = 0; i < robots.length; i++)
				robots[i] = compiledData[i].getRobot();
			
			//Create the compiled match data array
			MetricValue[][] compiledMatchData = new MetricValue[compiledData.length][];
			int[][] matchesPlayed = new int[compiledData.length][];
			for(int i = 0; i < compiledData.length; i++) {
				matchesPlayed[i] = compiledData[i].getMatchesPlayed();
				ArrayList<MetricValue> vals = new ArrayList<MetricValue>();
				
				for(MetricValue v : compiledData[i].getCompiledMatchData())
					vals.add(v);
				
				compiledMatchData[i] = vals.toArray(new MetricValue[0]);
			}
			
			summarySetRobotMetrics(robotMetrics);
			summarySetMatchMetrics(matchMetrics);
			summarySetRobots(robots);
			summarySetCompiledMatchData(robots, compiledMatchData);
			summarySetMatchesPlayed(robots, matchesPlayed);
		}
	}
	
	
	/*****
	 * Method: summaryGetCompiledData
	 * 
	 * Summary: gets all compiled data from the summary database
	 */
	
	public synchronized CompiledData[] summaryGetCompiledData(Query[] queries) {
		
		Event event = summaryGetEvent();
		Robot[] robots = summaryGetRobots();
		MetricValue[][] matchData = summaryGetCompiledMatchData();
		
		CompiledData[] compiledData = new CompiledData[robots.length];
		
		for(int i = 0; i < compiledData.length; i++) {
			int[] matchesPlayed = summaryGetMatchesPlayed(robots[i].getID());
			
			compiledData[i] = new CompiledData(
					event.getEventID(),
					matchesPlayed,
					robots[i],
					new String[0],
					matchData[i],
					new MetricValue[0]
					);
		}
		
		return compiledData;
	}
	
	/*****
	 * Method: summarySetMatchMetrics
	 * 
	 * @param metrics
	 * 
	 * Summary: erases all metrics from the summary match metrics table
	 * and adds the passed metrics to the table.
	 */
	
	private synchronized void summarySetMatchMetrics(Metric[] metrics) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBContract.SUMMARY_TABLE_MATCH_PERF_METRICS);
		
		for(int i = 0; i < metrics.length; i++) {
			
			String rangeInput = new String();
			
			if(metrics[i].getRange() != null) {
			
				for(Object r : metrics[i].getRange())	//Create the string for the range.
					rangeInput += r.toString() + ":";	//The range is stored in one cell.
			}
			
			ContentValues values = new ContentValues();
			values.put(DBContract.COL_METRIC_ID, metrics[i].getID());
			values.put(DBContract.COL_METRIC_NAME, metrics[i].getMetricName());
			values.put(DBContract.COL_GAME_NAME, metrics[i].getGameName());
			values.put(DBContract.COL_TYPE, metrics[i].getType());
			values.put(DBContract.COL_RANGE, rangeInput);
			values.put(DBContract.COL_DESCRIPTION, metrics[i].getDescription());
			values.put(DBContract.COL_DISPLAY, metrics[i].isDisplayed());
			values.put(DBContract.COL_METRIC_KEY, metrics[i].getKey());
			values.put(DBContract.COL_POSITION, metrics[i].getPosition());
			db.insert(DBContract.SUMMARY_TABLE_MATCH_PERF_METRICS, null, values);
		}
		
		helper.close();
	}
	
	
	/*****
	 * Method: summaryGetMatchMetrics
	 * 
	 * @return Metric[]
	 * 
	 * Summary: returns all match metrics that were put into tha database by the
	 * summarySetMatchMetrics function.
	 */
	
	private synchronized Metric[] summaryGetMatchMetrics() {
		
		Cursor c = helper.getWritableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.SUMMARY_TABLE_MATCH_PERF_METRICS + 
				" ORDER BY " + DBContract.COL_POSITION + " ASC", null);
		Metric[] metrics = new Metric[c.getCount()];
		
		for(int i = 0; i < metrics.length; i++) {
			
			c.moveToNext();
			
			String rangeString = c.getString(c.getColumnIndex(DBContract.COL_RANGE));
			ArrayList<Object> rangeArrList = new ArrayList<Object>();
			
			String currentRangeValString = new String();
			
			for(int character = 0; character < rangeString.length(); character++) {
				if(rangeString.charAt(character) != ':')
					currentRangeValString += rangeString.charAt(character);
				
				else {
					rangeArrList.add(currentRangeValString);
					currentRangeValString = new String();
				}
			}
			
			metrics[i] = new Metric(
					c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_DESCRIPTION)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)),
					c.getInt(c.getColumnIndex(DBContract.COL_TYPE)),
					rangeArrList.toArray(),
					(c.getInt(c.getColumnIndex(DBContract.COL_DISPLAY)) > 0),
					c.getInt(c.getColumnIndex(DBContract.COL_POSITION))
					);
		}
		
		helper.close();
		
		return metrics;
	}
	
	
	/*****
	 * Method: summarySetRobotMetrics
	 * 
	 * @param metrics
	 * 
	 * Summary: erases all metrics from the summary match metrics table
	 * and adds the passed metrics to the table.
	 */
	
	private synchronized void summarySetRobotMetrics(Metric[] metrics) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBContract.SUMMARY_TABLE_ROBOT_METRICS);
		
		for(int i = 0; i < metrics.length; i++) {
			
			String rangeInput = new String();
			
			if(metrics[i].getRange() != null) {
			
				for(Object r : metrics[i].getRange())	//Create the string for the range.
					rangeInput += r.toString() + ":";	//The range is stored in one cell.
			}
			
			ContentValues values = new ContentValues();
			values.put(DBContract.COL_METRIC_ID, metrics[i].getID());
			values.put(DBContract.COL_METRIC_NAME, metrics[i].getMetricName());
			values.put(DBContract.COL_GAME_NAME, metrics[i].getGameName());
			values.put(DBContract.COL_TYPE, metrics[i].getType());
			values.put(DBContract.COL_RANGE, rangeInput);
			values.put(DBContract.COL_DESCRIPTION, metrics[i].getDescription());
			values.put(DBContract.COL_DISPLAY, metrics[i].isDisplayed());
			values.put(DBContract.COL_METRIC_KEY, metrics[i].getKey());
			values.put(DBContract.COL_POSITION, metrics[i].getPosition());
			
			db.insert(DBContract.SUMMARY_TABLE_ROBOT_METRICS, null, values);
		}
		
		helper.close();
	}
	
	
	/*****
	 * Method: summaryGetRobotMetrics
	 * 
	 * @return Metric[]
	 * 
	 * Summary: returns all match metrics that were put into tha database by the
	 * summarySetMatchMetrics function.
	 *****/
	
	private synchronized Metric[] summaryGetRobotMetrics() {
		
		Cursor c = helper.getWritableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.SUMMARY_TABLE_ROBOT_METRICS + 
				" ORDER BY " + DBContract.COL_POSITION + " ASC", null);
		Metric[] metrics = new Metric[c.getCount()];
		
		for(int i = 0; i < metrics.length; i++) {
			
			c.moveToNext();
			
			String rangeString = c.getString(c.getColumnIndex(DBContract.COL_RANGE));
			ArrayList<Object> rangeArrList = new ArrayList<Object>();
			
			String currentRangeValString = new String();
			
			for(int character = 0; character < rangeString.length(); character++) {
				
				if(rangeString.charAt(character) != ':')
					currentRangeValString += rangeString.charAt(character);
				
				else {
					rangeArrList.add(currentRangeValString);
					currentRangeValString = new String();
				}
			}
			
			metrics[i] = new Metric(
					c.getInt(c.getColumnIndex(DBContract.COL_METRIC_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_DESCRIPTION)),
					c.getString(c.getColumnIndex(DBContract.COL_METRIC_KEY)),
					c.getInt(c.getColumnIndex(DBContract.COL_TYPE)),
							rangeArrList.toArray(),
					(c.getInt(c.getColumnIndex(DBContract.COL_DISPLAY)) > 0),
					c.getInt(c.getColumnIndex(DBContract.COL_POSITION))
					);
		}
		
		helper.close();
		
		return metrics;
	}
	
	
	/*****
	 * Method: summarySetRobots
	 * 
	 * @param robots
	 * 
	 * Summary: erases all data from the summary robots table and replaces it with 
	 * the passed robot array
	 */
	
	private synchronized void summarySetRobots(Robot[] robots) {
		if(robots.length == 0)
			return;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBContract.SUMMARY_TABLE_ROBOTS);
		
		for(int i = 0; i < robots.length; i++) {
			
			ContentValues values = new ContentValues();
			values.put(DBContract.COL_TEAM_NUMBER, robots[i].getTeamNumber());
			values.put(DBContract.COL_GAME_NAME, robots[i].getGame());
			values.put(DBContract.COL_ROBOT_ID, robots[i].getID());
			values.put(DBContract.COL_COMMENTS, robots[i].getComments());
			values.put(DBContract.COL_IMAGE_PATH, robots[i].getImagePath());
			values.put(DBContract.COL_OPR, robots[i].getOPR());
			
			for(MetricValue v : robots[i].getMetricValues()) {
				
				String valString = v.getValueAsDBReadableString();
				values.put(v.getMetric().getKey(), valString);
			}
			
			db.insert(DBContract.SUMMARY_TABLE_ROBOTS, null, values);
		}
		
		helper.close();
	}
	
	
	/*****
	 * Method: summaryGetRobots
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * Summary: gets all robots from the summary part of the database
	 */
	
	private synchronized Robot[] summaryGetRobots() {
			
		Cursor c = helper.getWritableDatabase().rawQuery("SELECT * FROM " + 
					DBContract.SUMMARY_TABLE_ROBOTS, null);
		Robot[] r = new Robot[c.getCount()];
		
		helper.close();
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			ArrayList<MetricValue> metricVals = new ArrayList<MetricValue>();
			Metric[] metrics = new Metric[0];
			
			metrics = summaryGetRobotMetrics();
				
			//Un-indent this stuff later
				for(int metricCount = 0; metricCount < metrics.length; metricCount++) {
					String valString = c.getString
							(c.getColumnIndex(metrics[metricCount].getKey()));
					ArrayList<String> valsArr = new ArrayList<String>();
					String workingString = new String();
					
					if(valString == null)
						valString = new String();
					
					for(int charCount = 0; charCount < valString.length(); charCount++) {
						if(valString.charAt(charCount) != ':'){
							workingString += valString.substring(charCount, charCount + 1);
							
						} else {
							valsArr.add(workingString);
							workingString = new String();
						}
					}
					
					try {
						metricVals.add(
							new MetricValue(
								metrics[metricCount],
								valsArr.toArray(new String[0])
							));
					} catch(MetricTypeMismatchException e) {
						System.out.println(e.getMessage());
					}
				}
			
			
			r[i] = new Robot(
					c.getInt(c.getColumnIndex(DBContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_GAME_NAME)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					c.getString(c.getColumnIndex(DBContract.COL_IMAGE_PATH)),
					c.getDouble(c.getColumnIndex(DBContract.COL_OPR)),
					metricVals.toArray(new MetricValue[0])
					);
		}
		
		return r;
	}
	
	
	/*****
	 * Method: summarySetCompiledMatchData
	 * 
	 * @param matchData
	 */
	
	private synchronized boolean summarySetCompiledMatchData(Robot[] robots, 
			MetricValue[][] matchData) {
		
		if(robots.length != matchData.length)
			return false;
		
		if(matchData.length == 0)
			return true;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBContract.SUMMARY_TABLE_COMPILED_MATCH_DATA);
		
		for(int i = 0; i <robots.length; i++) {
			ContentValues values = new ContentValues();
			values.put(DBContract.COL_ROBOT_ID, robots[i].getID());
			
			for(MetricValue v : matchData[i])
				values.put(v.getMetric().getKey(), v.getValueAsDBReadableString());
			
			db.insert(DBContract.SUMMARY_TABLE_COMPILED_MATCH_DATA, null, values);
		}
		
		helper.close();
		
		return true;
	}
	
	public synchronized MetricValue[][] summaryGetCompiledMatchData() {
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + 
				DBContract.SUMMARY_TABLE_COMPILED_MATCH_DATA, null);
		MetricValue[][] values = new MetricValue[c.getCount()][];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			Metric [] metricArr = summaryGetMatchMetrics();
			MetricValue[] dataArr = new MetricValue[metricArr.length];
			
			for(int k = 0; k < metricArr.length; k++) {
				ArrayList<String> valuesList = new ArrayList<String>();
				String valueString = c.getString(c.getColumnIndex(metricArr[k].getKey()));
				
				String currentValsString = new String();
				
				if(valueString != null) {
					for(int character = 0; character < valueString.length(); character++) {
					
						if(valueString.charAt(character) != ':')
							currentValsString += valueString.charAt(character);
					
						else {
							valuesList.add(currentValsString);
							currentValsString = new String();
						}
					}
				}
				
				try {
					dataArr[k] = new MetricValue(metricArr[k], valuesList.toArray(new String[0]));
				} catch (MetricTypeMismatchException e) {
					try {
						dataArr[k] = new MetricValue(metricArr[k], new String[] {"0"});
					} catch(MetricTypeMismatchException ex) {}
				}
			}
			
			values[i] = dataArr;
		}
		
		return values;
	}
	
	
	/*****
	 * Method: summarySetRawMatchData
	 * 
	 * @param matchData
	 * 
	 * Summary: replaces the old summary match data with the new match data
	 */
	
	public synchronized void summarySetRawMatchData(MatchData[] matchData) {
		
		if(matchData.length == 0)
			return;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBContract.SUMMARY_TABLE_MATCH_DATA);
		
		for(int i = 0; i < matchData.length; i++) {
			ContentValues values = new ContentValues();
			
			values.put(DBContract.COL_DATA_ID, Integer.toString(matchData[i].getMatchID()));
			values.put(DBContract.COL_EVENT_ID, Integer.toString(matchData[i].getEventID()));
			values.put(DBContract.COL_MATCH_NUMBER, Integer.toString(matchData[i].getMatchNumber()));
			values.put(DBContract.COL_ROBOT_ID, Integer.toString(matchData[i].getRobotID()));
			values.put(DBContract.COL_USER_ID, Integer.toString(matchData[i].getUserID()));
			values.put(DBContract.COL_MATCH_TYPE, matchData[i].getMatchType());
			values.put(DBContract.COL_COMMENTS, matchData[i].getComments());
			for(MetricValue val : matchData[i].getMetricValues()) {
				if(val != null) {
					String[] arr = val.getValue();
					String valString = new String();
				
					for(int k = 0; k < arr.length; k++)
						valString += arr[k] + ":";
				
					values.put(val.getMetric().getKey(), valString);
				}
			}
			
			db.insert(DBContract.SUMMARY_TABLE_MATCH_DATA, null, values);
		}
		
		db.close();
	}
	
	
	/*****
	 * Method: getMatchDataByColumns
	 * 
	 * @param cols
	 * @param vals
	 * @return
	 * 
	 * Summary: gets the match data by the columns and values passed in the arrays
	 *****/
	
	public synchronized MatchData[] summaryGetMatchDataByColumns(String[] cols, String[] vals) {
		
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new MatchData[0];
		
		String queryString = "SELECT * FROM " + DBContract.SUMMARY_TABLE_MATCH_DATA + " WHERE " + 
				cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
		
		queryString += " ORDER BY " + DBContract.COL_MATCH_TYPE + " DESC, " +
				DBContract.COL_MATCH_NUMBER + " ASC";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		MatchData[] d = new MatchData[c.getCount()];
		
		helper.close();
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			Metric [] metricArr = summaryGetMatchMetrics();
			MetricValue[] dataArr = new MetricValue[metricArr.length];
			
			for(int k = 0; k < metricArr.length; k++) {
				
				ArrayList<String> valuesList = new ArrayList<String>();
				String valueString = c.getString(c.getColumnIndex(metricArr[k].getKey()));
				
				String currentValsString = new String();
				
				if(valueString != null) {
					for(int character = 0; character < valueString.length(); character++) {
					
						if(valueString.charAt(character) != ':')
							currentValsString += valueString.charAt(character);
					
						else {
							valuesList.add(currentValsString);
							currentValsString = new String();
						}
					}
				}
				
				try {
					dataArr[k] = new MetricValue(metricArr[k], valuesList.toArray(new String[0]));
				} catch (MetricTypeMismatchException e) {
					try {
						dataArr[k] = new MetricValue(metricArr[k], new String[] {"0"});
					} catch(MetricTypeMismatchException ex) {}
				}
			}
			
			d[i] = new MatchData(
					c.getInt(c.getColumnIndex(DBContract.COL_DATA_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_EVENT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_MATCH_NUMBER)),
					c.getInt(c.getColumnIndex(DBContract.COL_ROBOT_ID)),
					c.getInt(c.getColumnIndex(DBContract.COL_USER_ID)),
					c.getString(c.getColumnIndex(DBContract.COL_MATCH_TYPE)),
					c.getString(c.getColumnIndex(DBContract.COL_COMMENTS)),
					dataArr
					);
		}
		
		helper.close();
		
		return d;
	}
	
	
	/*****
	 * Method: summarySetMatchesPlayed
	 * 
	 * @param robotID
	 * @return
	 * 
	 * Summary: gets the list of match numbers that this robot has played
	 */
	
	private synchronized int[] summaryGetMatchesPlayed(int robotID) {
		
		Cursor c = helper.getReadableDatabase().rawQuery(
				"SELECT * FROM " + DBContract.SUMMARY_TABLE_MATCHES_PLAYED + 
				" WHERE " + DBContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(robotID)});
		int[] matchesPlayed = new int[c.getCount()];
		
		for(int i = 0; i < matchesPlayed.length; i++) {
			c.moveToNext();
			matchesPlayed[i] = c.getInt(c.getColumnIndex(DBContract.COL_MATCH_NUMBER));
		}
		
		return matchesPlayed;
	}
	
	
	/*****
	 * Method: summarySetMatchesPlayed
	 * 
	 * @param robots
	 * @param matchesPlayed
	 * @return
	 * 
	 * Summary: erases the old matches played table and replaces it with the data passed
	 * in by the parameters
	 */
	
	private synchronized boolean summarySetMatchesPlayed(Robot[] robots, int[][] matchesPlayed) {
		
		if(robots.length != matchesPlayed.length)
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBContract.SUMMARY_TABLE_MATCHES_PLAYED);
		
		if(robots.length == 0)
			return true;
		
		for(int i = 0; i < robots.length; i++) {
			for(int matchNum : matchesPlayed[i]) {
				ContentValues row = new ContentValues();
				row.put(DBContract.COL_ROBOT_ID, robots[i].getID());
				row.put(DBContract.COL_MATCH_NUMBER, matchNum);
				
				db.insert(DBContract.SUMMARY_TABLE_MATCHES_PLAYED, null, row);
			}
		}
		
		helper.close();
		
		return true;
	}
}
