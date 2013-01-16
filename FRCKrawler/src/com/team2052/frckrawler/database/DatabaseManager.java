package com.team2052.frckrawler.database;

import java.util.*;

import com.team2052.frckrawler.database.structures.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;

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

public class DatabaseManager {
	
	
	private static DatabaseManager instance = null;
	
	private DatabaseHelper helper;
	private Context context;
	
	/*****
	 * Constructor
	 * 
	 * @param _context
	 * 
	 * Summary: This constructor is private because this class is a singleton. No other class should ever call
	 * the constructor. Calls should be made to getInstance() instead.
	 *****/
	
	private DatabaseManager(Context _context) {
		
		//calling getApplicationContext() assures that no resources are held on to that should have been released.
		context = _context.getApplicationContext();
		
		helper = new DatabaseHelper(context);
	}
	
	
	/*****
	 * Method: getInstance()
	 * 
	 * @param _context
	 * 
	 * Summary: Returns an instance of the DatabaseManager. This should always be
	 * called instead of the constructor.
	 *****/
	
	public static DatabaseManager getInstance(Context _context) {
		
		if(instance == null){	//If a manager has not been created yet
			
			instance = new DatabaseManager(_context.getApplicationContext());
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
		values.put(DatabaseContract.COL_USER_ID, createID(DatabaseContract.TABLE_USERS, 
				DatabaseContract.COL_USER_ID));
		values.put(DatabaseContract.COL_USER_NAME, name);
		values.put(DatabaseContract.COL_SUPERUSER, superuser);
		
		helper.getWritableDatabase().insert(DatabaseContract.TABLE_USERS, null, values);
		
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
		
		helper.getWritableDatabase().delete(DatabaseContract.TABLE_USERS, 
				DatabaseContract.COL_USER_ID + " LIKE ?", new String[] {Integer.toString(id)});
		
		helper.close();
	}
	
	
	/*****
	 * Method: getAllUsers
	 * 
	 * Summary: Gets all users from the database and returns them to an array.
	 */
	
	public synchronized User[] getAllUsers() {
		
		Cursor c = helper.getReadableDatabase().rawQuery(
				"SELECT * FROM " + DatabaseContract.TABLE_USERS, null);
		User[] u = new User[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			u[i] = new User(
					c.getString(c.getColumnIndex(DatabaseContract.COL_USER_NAME)),
					c.getInt(c.getColumnIndex(DatabaseContract.COL_SUPERUSER)),
					c.getInt(c.getColumnIndex(DatabaseContract.COL_USER_ID))
					);
		}
		
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
		
		String queryString = "SELECT * FROM " + DatabaseContract.TABLE_USERS + 
				" WHERE " + cols[0] + " LIKE ?";
		
		for(int i = 1; 1 < cols.length; i++)
			queryString += " AND " + cols[i] + " LIKE ?";
		
		Cursor c = helper.getReadableDatabase().rawQuery(queryString, vals);
		User[] u = new User[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			u[i] = new User(
					c.getString(c.getColumnIndex(DatabaseContract.COL_USER_NAME)),
					Boolean.getBoolean(c.getString(c.getColumnIndex(DatabaseContract.COL_SUPERUSER))),
					c.getInt(c.getColumnIndex(DatabaseContract.COL_USER_ID))
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
			if(s.equals(DatabaseContract.COL_USER_ID))
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
		
		helper.getWritableDatabase().update(DatabaseContract.TABLE_USERS, vals, 
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
		vals.put(DatabaseContract.COL_SUPERUSER, superuser);
		
		helper.getWritableDatabase().update(DatabaseContract.TABLE_USERS, 
				vals, 
				DatabaseContract.COL_USER_NAME + " LIKE ?", 
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
	
	public boolean addTeam(Team t) {
		
		if(t.getRookieYear() != null)
			return addTeam(t.getNumber(), t.getName(), t.getSchool(), t.getCity(), 
					Integer.parseInt(t.getRookieYear()), t.getWebsite(), t.getStatePostalCode(), t.getColors());
		else
			return addTeam(t.getNumber(), t.getName(), t.getSchool(), t.getCity(), 
					-1, t.getWebsite(), t.getStatePostalCode(), t.getColors());
	}
	
	public synchronized boolean addTeam(int number, String name, String school, String city,
			int rookieYear, String website, String statePostalCode, String colors) {
		
		if(number < 0)
			return false;
		
		//If there is not already a team with that number...
		if(!hasValue(DatabaseContract.TABLE_TEAMS, 
				DatabaseContract.COL_TEAM_NUMBER, Integer.toString(number))) {
			
			SQLiteDatabase db = helper.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseContract.COL_TEAM_NUMBER, number);
			values.put(DatabaseContract.COL_TEAM_NAME, name);
			values.put(DatabaseContract.COL_SCHOOL, school);
			values.put(DatabaseContract.COL_CITY, city);
			values.put(DatabaseContract.COL_ROOKIE_YEAR, rookieYear);
			values.put(DatabaseContract.COL_WEBSITE, website);
			values.put(DatabaseContract.COL_STATE_POSTAL_CODE, statePostalCode);
			values.put(DatabaseContract.COL_COLORS, colors);
		
			db.insert(DatabaseContract.TABLE_TEAMS, null, values);	//Add it to the database
			helper.close();
			
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
		
		Cursor c = db.query(DatabaseContract.TABLE_ROBOTS, 		//Find the team's robots
				new String[] {DatabaseContract.COL_ROBOT_ID}, 
				DatabaseContract.COL_TEAM_NUMBER + " LIKE ?", 
				new String[] {Integer.toString(number)},
				null, null, null);
		
		while(c.moveToNext()) {	//While this team still has robots...
			
			String[] value = {c.getString(c.getColumnIndex(DatabaseContract.COL_ROBOT_ID))};
			
			db.delete(DatabaseContract.TABLE_MATCH_PERF, DatabaseContract.COL_ROBOT_ID + " LIKE ?", 
					value);
			
			//Get pictures from the file system based on robot ids and delete them
		}
		
		String[] value = {Integer.toString(number)};
		
		db.delete(DatabaseContract.TABLE_CONTACTS, DatabaseContract.COL_TEAM_NUMBER + " LIKE ?", value);
		db.delete(DatabaseContract.TABLE_ROBOTS, DatabaseContract.COL_TEAM_NUMBER + " LIKE ?", value);
		db.delete(DatabaseContract.TABLE_COMMENTS, DatabaseContract.COL_TEAM_NUMBER + " LIKE ?", value);
		db.delete(DatabaseContract.TABLE_TEAMS, DatabaseContract.COL_TEAM_NUMBER + " LIKE ?", value);
		
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
		Cursor c = db.rawQuery(DatabaseContract.SELECT_ALL_TEAM_DATA, null);
		Team[] t = new Team[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			t[i] = new Team(c.getInt(c.getColumnIndex(DatabaseContract.COL_TEAM_NUMBER)), 
					c.getString(c.getColumnIndex(DatabaseContract.COL_TEAM_NAME)), 
					c.getString(c.getColumnIndex(DatabaseContract.COL_SCHOOL)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_CITY)),
					c.getInt(c.getColumnIndex(DatabaseContract.COL_ROOKIE_YEAR)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_WEBSITE)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_STATE_POSTAL_CODE)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_COLORS)));
		}
		
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
		
		if(cols.length != vals.length)
			return null;
		
		if(cols.length < 1)
			return new Team[0];
		
		String queryString = "SELECT * FROM " + DatabaseContract.TABLE_TEAMS + " WHERE " + cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
			
		Cursor c = helper.getWritableDatabase().rawQuery(queryString, vals);
		Team[] t = new Team[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			t[i] = new Team(c.getInt(c.getColumnIndex(DatabaseContract.COL_TEAM_NUMBER)), 
					c.getString(c.getColumnIndex(DatabaseContract.COL_TEAM_NAME)), 
					c.getString(c.getColumnIndex(DatabaseContract.COL_SCHOOL)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_CITY)),
					c.getInt(c.getColumnIndex(DatabaseContract.COL_ROOKIE_YEAR)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_WEBSITE)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_STATE_POSTAL_CODE)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_COLORS)));
		}
		
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
			if(s.equals(DatabaseContract.COL_TEAM_NUMBER))
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
		
		helper.getWritableDatabase().update(DatabaseContract.TABLE_TEAMS, vals, 
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
	
	public void addEvent(Event e) {
		
		addEvent(e.getEventName(), e.getGameName(), e.getLocation(), e.getDateStamp());
	}
	
	public synchronized void addEvent(String name, String gameName, 
			String location, Date date) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		int newID = createID(DatabaseContract.TABLE_COMPETITIONS, DatabaseContract.COL_EVENT_ID);
		
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.COL_EVENT_ID, newID);
		values.put(DatabaseContract.COL_EVENT_NAME, name);
		values.put(DatabaseContract.COL_GAME_NAME, gameName);
		values.put(DatabaseContract.COL_LOCATION, location);
		
		if(date != null)	//If a date has been set...
			values.put(DatabaseContract.COL_DATE_STAMP, date.getTime());
		
		db.insert(DatabaseContract.TABLE_COMPETITIONS, null, values);
		
		helper.close();
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
		
		db.delete(DatabaseContract.TABLE_MATCH_PERF, DatabaseContract.COL_EVENT_ID + " LIKE ?", 
				new String[] {Integer.toString(eventID)});
		db.delete(DatabaseContract.TABLE_COMPETITIONS, DatabaseContract.COL_EVENT_ID + " LIKE ?", 
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
				"SELECT * FROM " + DatabaseContract.TABLE_COMPETITIONS, null);
		
		Event[] e = new Event[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			e[i] = new Event(
					c.getInt(c.getColumnIndex(DatabaseContract.COL_EVENT_ID)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_EVENT_NAME)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_GAME_NAME)),
					new Date(c.getLong(c.getColumnIndex(DatabaseContract.COL_DATE_STAMP))),
					c.getString(c.getColumnIndex(DatabaseContract.COL_LOCATION)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_FMS_EVENT_ID))
					);
		}
		
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
	
	
	public synchronized Event[] getEventsByCol(String[] cols, String[] vals) {
		
		String queryString = "SELECT * FROM " + DatabaseContract.TABLE_COMPETITIONS + " WHERE";
		
		if(cols.length > 0)	//Special case for first entry because it should not include the AND
			queryString += " " + cols[0] + " LIKE ?";
		
		for(int i = 1; i < cols.length; i++) //Builds a string for the query with the cols values
			queryString += " AND " + cols[i] + " LIKE ?";
			
		Cursor c = helper.getReadableDatabase().rawQuery(queryString, vals);
		Event[] e = new Event[c.getCount()];
		
		for(int i = 0; i < c.getCount(); i++) {
			
			c.moveToNext();
			
			e[i] = new Event(
					c.getInt(c.getColumnIndex(DatabaseContract.COL_EVENT_ID)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_EVENT_NAME)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_GAME_NAME)),
					new Date(c.getLong(c.getColumnIndex(DatabaseContract.COL_DATE_STAMP))),
					c.getString(c.getColumnIndex(DatabaseContract.COL_LOCATION)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_FMS_EVENT_ID))
					);
		}
		
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
			if(s.equals(DatabaseContract.COL_EVENT_ID))
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
		
		helper.getWritableDatabase().update(DatabaseContract.TABLE_COMPETITIONS, vals, 
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
		values.put(DatabaseContract.COL_TEAM_NUMBER, teamNumber);
		values.put(DatabaseContract.COL_EVENT_ID, eventID);
		values.put(DatabaseContract.COL_USER_ID, userID);
		values.put(DatabaseContract.COL_COMMENT, comment);
		
		if(dateStamp != null) {
			values.put(DatabaseContract.COL_DATE_STAMP, dateStamp.getTime());
		}
		
		helper.getWritableDatabase().insert(DatabaseContract.TABLE_COMMENTS, null, values);
		
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
					DatabaseContract.TABLE_COMMENTS + " WHERE " + 
					DatabaseContract.COL_TEAM_NUMBER + "='" + teamNumber + 
					"' AND " + DatabaseContract.COL_DATE_STAMP + 
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
				"SELECT * FROM " + DatabaseContract.TABLE_COMMENTS, null);
		
		Comment[] comments = new Comment[c.getCount()];
		
		for(int i = 0; i < c.getColumnCount(); i++) {
			
			c.moveToNext();
			
			comments[i] = new Comment(
					c.getInt(c.getColumnIndex(DatabaseContract.COL_TEAM_NUMBER)),
					c.getInt(c.getColumnIndex(DatabaseContract.COL_USER_ID)),
					c.getInt(c.getColumnIndex(DatabaseContract.COL_EVENT_ID)),
					c.getString(c.getColumnIndex(DatabaseContract.COL_COMMENT)),
					new Date(c.getInt(c.getColumnIndex(DatabaseContract.COL_DATE_STAMP)))
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
		values.put(DatabaseContract.COL_TEAM_NUMBER, teamNumber);
		values.put(DatabaseContract.COL_CONTACT_ID, createID(DatabaseContract.TABLE_CONTACTS, 
				DatabaseContract.COL_CONTACT_ID));
		values.put(DatabaseContract.COL_CONTACT_NAME, name);
		values.put(DatabaseContract.COL_EMAIL, email);
		values.put(DatabaseContract.COL_ADDRESS, address);
		values.put(DatabaseContract.COL_PHONE_NUMBER, phoneNumber);
		
		helper.getWritableDatabase().insert(DatabaseContract.TABLE_CONTACTS, null, values);
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
		
		helper.getWritableDatabase().delete(DatabaseContract.TABLE_CONTACTS, 
				DatabaseContract.COL_CONTACT_ID + " LIKE ?", 
				new String[] {Integer.toString(id)});
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
		
		if(!hasValue(DatabaseContract.TABLE_GAMES, DatabaseContract.COL_GAME_NAME, name)) {
			//If the name is not taken...
			
			ContentValues values = new ContentValues();	//Add it to the
			values.put(DatabaseContract.COL_GAME_NAME, name);	//games table
			
			helper.getWritableDatabase().insert(DatabaseContract.TABLE_GAMES, null, values);
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
	 * season worth of data from the database.
	 * 
	 * @return True if the game was removed, false if it was not. This happens
	 * if the string passed as a parameter was not found in the database.
	 *****/
	
	public boolean removeGame(Game g) {
		
		return removeGame(g.getName());
	}
	
	public synchronized boolean removeGame(String name) {
		
		if(!hasValue(DatabaseContract.TABLE_GAMES, DatabaseContract.COL_GAME_NAME, name))	
			//If this is not the name of a real game...
			return false;	//tell the caller that the operation failed.
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DatabaseContract.TABLE_ROBOTS, 
				new String[] {DatabaseContract.COL_ROBOT_ID}, 
				DatabaseContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {name}, 
				null,null, null);
		
		for(int robotCount = 0; robotCount < c.getCount(); robotCount++) {
			
			c.moveToNext();
			removeRobot(c.getInt(c.getColumnIndex(DatabaseContract.COL_ROBOT_ID)));
		}
		
		db = helper.getWritableDatabase();	//Reopen because the removeRobot method closed it.
		
		db.delete(DatabaseContract.TABLE_MATCH_PERF_METRICS, 
				DatabaseContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {name});
		
		db.delete(DatabaseContract.TABLE_ROBOT_METRICS, 
				DatabaseContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {name});
		
		db.delete(DatabaseContract.TABLE_COMPETITIONS, 
				DatabaseContract.COL_GAME_NAME + 
				" LIKE ?", new String[] {name});
		
		db.delete(DatabaseContract.TABLE_GAMES, 
				DatabaseContract.COL_GAME_NAME + 
				" LIKE ?", new String[] {name});
		
		helper.close();
		
		return true;
	}
	
	
	/*****
	 * Method: getAllGames
	 * 
	 * Summary: Gets all games from the database and returns them 
	 * in a Game array.
	 *****/
	
	public synchronized Game[] getAllGames() {
		
		Cursor c = helper.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseContract.TABLE_GAMES, null);
		Game[] g = new Game[c.getCount()];
		
		for(int i = 0; i < g.length; i++) {
			
			c.moveToNext();
			g[i] = new Game(c.getString(c.getColumnIndex(DatabaseContract.COL_GAME_NAME)));
		}
		
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
	
	public synchronized boolean addRobot(int teamNumber, String gameName, int numberOfWheels, 
			String wheelType, String driveTrain) {
		
		if(!hasValue(DatabaseContract.TABLE_GAMES, DatabaseContract.COL_GAME_NAME, gameName))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.COL_TEAM_NUMBER, teamNumber);
		values.put(DatabaseContract.COL_GAME_NAME, gameName);
		values.put(DatabaseContract.COL_ROBOT_ID, createID(DatabaseContract.TABLE_ROBOTS, 
				DatabaseContract.COL_ROBOT_ID));
		values.put(DatabaseContract.COL_NUMBER_WHEELS, numberOfWheels);
		values.put(DatabaseContract.COL_WHEEL_TYPE, wheelType);
		values.put(DatabaseContract.COL_DRIVETRAIN, driveTrain);
		
		db.insert(DatabaseContract.TABLE_ROBOTS, null, values);
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
		
		if(!hasValue(DatabaseContract.TABLE_ROBOTS, DatabaseContract.COL_ROBOT_ID, 
				Integer.toString(robotID)))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		//Remove all pictures
		
		db.delete(DatabaseContract.TABLE_MATCH_PERF, DatabaseContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(robotID)});
		db.delete(DatabaseContract.TABLE_ROBOTS, DatabaseContract.COL_ROBOT_ID + " LIKE ?", 
				new String[] {Integer.toString(robotID)});
		helper.close();
		
		return true;
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
	
	public synchronized boolean addRobotMetric(String name, String game, int type, 
			String[] range, String description) {
		
		if(hasValue(DatabaseContract.TABLE_ROBOT_METRICS, DatabaseContract.COL_GAME_NAME, game) && 
				hasValue(DatabaseContract.TABLE_ROBOT_METRICS, DatabaseContract.COL_METRIC_NAME, name))
			return false;	//There is already a type with this name for this game.
		
		if(!hasValue(DatabaseContract.TABLE_GAMES, DatabaseContract.COL_GAME_NAME, game))
			return false;	//This game is not in the database.
		
		if(type < 0 || type > DatabaseContract.HIGHEST_TYPE)
			return false;	//This is not a real type.
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		String rangeInput = new String();
		
		if(range != null) {
			
			for(String r :  range)	//Create the string for the range.
				rangeInput += r + ":";	//The range is stored in one cell.
		}
		
		ContentValues values = new ContentValues();	//New values for the ROBOT_METRICS table
		values.put(DatabaseContract.COL_METRIC_NAME, name);
		values.put(DatabaseContract.COL_GAME_NAME, game);
		values.put(DatabaseContract.COL_TYPE, type);
		values.put(DatabaseContract.COL_RANGE, rangeInput);
		values.put(DatabaseContract.COL_DESCRIPTION, description);
		
		Cursor c = db.query(DatabaseContract.TABLE_ROBOT_METRICS,	//Check to see who has
							new String[] {DatabaseContract.COL_METRIC_KEY}, //what keys already
							DatabaseContract.COL_GAME_NAME + " LIKE ?", 
							new String[] {game}, 
							null, null, 
							DatabaseContract.COL_METRIC_KEY + " ASC");
		
		for(int key = 0; key < DatabaseContract.COL_KEYS.length; key++) {	//Cycle through
																			//all possible keys.
			if(!c.moveToNext() ||
					!DatabaseContract.COL_KEYS[key].equals(c.getString(c.getColumnIndex(DatabaseContract.COL_METRIC_KEY)))) {
				
				values.put(DatabaseContract.COL_METRIC_KEY, //Assign this key to the new
						DatabaseContract.COL_KEYS[key]);	//metric
				
				ContentValues nullValue = new ContentValues();	//Make a null CV
				nullValue.putNull(DatabaseContract.COL_KEYS[key]);
				
				db.update(DatabaseContract.TABLE_ROBOTS,	//Put the null
						nullValue, 							//value into the
						DatabaseContract.COL_GAME_NAME + " LIKE ?",//robots table
						new String[] {game});
				
				break;	//exit the loop, no need to continue
			}
			
			if(key == 15)	//Return false because no more metrics can
				return false;	//be added. Limit reached.
		}
		
		db.insert(DatabaseContract.TABLE_ROBOT_METRICS, null, values);
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
	
	public synchronized boolean removeRobotMetric(String name, String game) {
		
		if(!hasValue(DatabaseContract.TABLE_ROBOT_METRICS, DatabaseContract.COL_METRIC_NAME, name) && 
				!hasValue(DatabaseContract.TABLE_ROBOT_METRICS, DatabaseContract.COL_GAME_NAME, game))
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DatabaseContract.TABLE_ROBOT_METRICS, 
							new String[] {DatabaseContract.COL_METRIC_KEY}, 
							DatabaseContract.COL_GAME_NAME + " LIKE ? AND " + 
							DatabaseContract.COL_METRIC_NAME + " LIKE ?", 
							new String[] {game, name}, 
							null, null, null);
		
		c.moveToFirst();
		String key = c.getString(c.getColumnIndex(DatabaseContract.COL_METRIC_KEY));
		
		ContentValues nullVal = new ContentValues();
		nullVal.putNull(key);
		
		db.update(DatabaseContract.TABLE_ROBOTS, nullVal, 
					DatabaseContract.COL_GAME_NAME + " LIKE ?", new String[] {game});
		
		db.delete(DatabaseContract.TABLE_ROBOT_METRICS, 
				  DatabaseContract.COL_GAME_NAME + " LIKE ? AND " + DatabaseContract.COL_METRIC_NAME + " LIKE ?", 
				  new String[] {game, name});
		
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
	
	public synchronized boolean addMatchPerformanceMetric(String name, String game, int type, String[] range, String description) {
		
		if(hasValue(DatabaseContract.TABLE_MATCH_PERF_METRICS, DatabaseContract.COL_GAME_NAME, game) && 
				hasValue(DatabaseContract.TABLE_MATCH_PERF_METRICS, DatabaseContract.COL_METRIC_NAME, name))
			return false;	//There is already a type with this name for this game.
		
		if(!hasValue(DatabaseContract.TABLE_GAMES, DatabaseContract.COL_GAME_NAME, game))
			return false;	//This game is not in the database.
		
		if(type < 0 || type > DatabaseContract.HIGHEST_TYPE)
			return false;	//This is not a real type.
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		String rangeInput = new String();
		
		if(range != null) {
		
			for(String r :  range)	//Create the string for the range.
				rangeInput += r + ":";	//The range is stored in one cell.
		}
		
		ContentValues values = new ContentValues();	//New values for the MATCH_PERF_METRICS table
		values.put(DatabaseContract.COL_METRIC_NAME, name);
		values.put(DatabaseContract.COL_GAME_NAME, game);
		values.put(DatabaseContract.COL_TYPE, type);
		values.put(DatabaseContract.COL_RANGE, rangeInput);
		values.put(DatabaseContract.COL_DESCRIPTION, description);
		
		Cursor c = db.query(DatabaseContract.TABLE_MATCH_PERF_METRICS,	//Check to see who has
							new String[] {DatabaseContract.COL_METRIC_KEY}, //what keys already
							DatabaseContract.COL_GAME_NAME + " LIKE ?", 
							new String[] {game}, 
							null, null, 
							DatabaseContract.COL_METRIC_KEY + " ASC");
		
		for(int key = 0; key < DatabaseContract.COL_KEYS.length; key++) {	//Cycle through
																			//all possible keys.
			if(!c.moveToNext() ||
					!DatabaseContract.COL_KEYS[key].equals(c.getString(c.getColumnIndex(DatabaseContract.COL_METRIC_KEY)))) {
				
				values.put(DatabaseContract.COL_METRIC_KEY, //Assign this key to the new
						DatabaseContract.COL_KEYS[key]);	//metric
				
				Cursor eventCursor = db.query(DatabaseContract.TABLE_COMPETITIONS, //Select the competitions for this game
											  new String[] {DatabaseContract.COL_EVENT_ID}, 
											  DatabaseContract.COL_GAME_NAME + " LIKE ?", 
											  new String[] {game}, 
											  null, null, null);
				
				ContentValues nullValue = new ContentValues();	//Make a null CV
				nullValue.putNull(DatabaseContract.COL_KEYS[key]);
				
				for(int eventCount = 0; eventCount < eventCursor.getCount(); eventCount++) {	//Cycle through all the comps
					
					eventCursor.moveToNext();
					
					db.update(DatabaseContract.TABLE_MATCH_PERF, //Set the key value to null in the match table
							nullValue, 
							DatabaseContract.COL_EVENT_ID + " LIKE ?", 
							new String[] {eventCursor.getString(
									eventCursor.getColumnIndex(DatabaseContract.COL_EVENT_ID))});
				}
				
				break;	//No need to continue
			}
			
			if(key == 15)	//Return false because no more metrics can
				return false;	//be added. Limit reached.
		}
		
		db.insert(DatabaseContract.TABLE_MATCH_PERF_METRICS, null, values);
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
	
	public synchronized boolean removeMatchPerformaceMetric(String name, String game) {
		
		if(!hasValue(DatabaseContract.TABLE_MATCH_PERF_METRICS, 
				DatabaseContract.COL_METRIC_NAME, name) && 
				!hasValue(DatabaseContract.TABLE_MATCH_PERF_METRICS, 
						DatabaseContract.COL_GAME_NAME, game))
			return false;	//If this is not a real game or metric...
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(DatabaseContract.TABLE_MATCH_PERF_METRICS,
				new String[] {DatabaseContract.COL_METRIC_KEY}, 
				DatabaseContract.COL_GAME_NAME + " LIKE ? AND " + 
						DatabaseContract.COL_METRIC_NAME + " LIKE ?", 
				new String[] {game, name}, 
				null, null, null);
		c.moveToFirst();//Gets the key for this metric
		
		ContentValues nullVal = new ContentValues();//Make a null CV for it
		nullVal.putNull(c.getString(c.getColumnIndex(DatabaseContract.COL_METRIC_KEY)));
		
		c = db.query(DatabaseContract.TABLE_COMPETITIONS, 
				new String[] {DatabaseContract.COL_EVENT_ID}, 
				DatabaseContract.COL_GAME_NAME + " LIKE ?", 
				new String[] {game}, 
				null, null, null);//Get the competitions for this game
		
		while(c.moveToNext()) {//While there are still more competitions...
			
			db.update(DatabaseContract.TABLE_MATCH_PERF, nullVal, 
					DatabaseContract.COL_EVENT_ID + " LIKE ?", 
					new String[] {//Set the metric at the key to null
						c.getString(c.getColumnIndex(DatabaseContract.COL_EVENT_ID))
					});
			
		}
		
		db.delete(DatabaseContract.TABLE_MATCH_PERF_METRICS, 
				  DatabaseContract.COL_GAME_NAME + " LIKE ? AND " + DatabaseContract.COL_METRIC_NAME + " LIKE ?", 
				  new String[] {game, name});//Delete the metric
		
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
	 * CHARLIE! THIS MAY HAVE TO BE CHANGED FOR 
	 * THE FLEXIBLE COLUMNS! FIX IT!
	 */
	
	public synchronized boolean updateRobot(String queryCols[], String queryVals[], 
			String updateCols[], String updateVals[]) {
		
		if(updateCols.length != updateVals.length) //They must be the same so that every value
			return false; //has something to map to and that there are no blank ones.
		
		if(queryCols.length != queryVals.length)
			return false;
		
		for(String s : updateCols) { //Does not allow the caller to update the team number.
			if(s.equals(DatabaseContract.COL_ROBOT_ID))
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
		
		helper.getWritableDatabase().update(DatabaseContract.TABLE_ROBOTS, vals, 
				queryString, queryVals);
		
		helper.close();
		
		return true;
	}
	
	public synchronized void insertMatchData() {
		
		
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
			
			System.out.println("No results returned from query.");
			
		} catch(Exception e) {
			
			System.out.println("Error in qeury.");
			
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
			
				newID = (int)(Math.random() * 1000);
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
	 */
	
	private boolean hasValue(String table, String column, String value) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor c = db.query(table, 
				new String[] {column}, 
				column + " LIKE ?", 
				new String[] {value},
				null, null, null);
		
		return c.moveToFirst();
	}
}
