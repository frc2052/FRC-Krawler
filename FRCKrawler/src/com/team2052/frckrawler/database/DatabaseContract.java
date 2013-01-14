package com.team2052.frckrawler.database;

import android.database.sqlite.SQLiteDatabase;

/*****
 * Class: Database Contract
 * 
 * Summary: This is a representation of the structure of the database. Any statements
 * that edit the database content should use these constants rather than referencing
 * the tables and columns explicitly. Any additions to the schema of the database
 * should be made here. Any changes in the columns of a table should also be out into
 * the CREATE_TABLE_* constant for the table. Any tables added to the schema should
 * be also added to the createAllTables() and dropAllTables() method at the bottom.
 * If they are not, the changes will not be reflected in the structure of the 
 * actual database.
 * 
 * This class also contains several helper methods for large-scale database
 * management that can't be done with one query.
 * 
 * @author Charles Hofer
 *****/

public class DatabaseContract {
	
	
	/*****
	 * SQL Data Types
	 *****/
	
	public static final String INT = " INTEGER";
	public static final String DECIMAL = " REAL";
	public static final String STRING = " TEXT";
	public static final String BLOB = " BLOB";
	public static final String ANY = " NUMERIC";
	
	/*****
	 * Scouting Metric Map
	 * 
	 * The scouting data types are the input method for the scouts.
	 * 
	 * 0 - BOOLEAN
	 * 1 - COUNTER
	 * 2 - SLIDER
	 * 3 - CHOOSER
	 * 4 - TEXT
	 *****/
	
	public static final int BOOLEAN = 0;
	public static final int COUNTER = 1;
	public static final int SLIDER = 2;
	public static final int CHOOSER = 3;
	public static final int TEXT = 4;
	
	public static final int HIGHEST_TYPE = 4;
	
	/*****
	 * Schema
	 *****/
	
	public static final String DATABASE_NAME = "scoutingdb";
	
	
	
	public static final String TABLE_USERS = "users";
	
	public static final String COL_USER_ID = "userid";
	public static final String COL_USER_NAME = "username";
	public static final String COL_SUPERUSER = "superuser";
	
	public static final String CREATE_TABLE_USERS = 
			"CREATE TABLE " + TABLE_USERS + " (" + COL_USER_ID + INT + ", " + COL_USER_NAME + 
					STRING + ", " + COL_SUPERUSER + STRING + ")";
	
	
	
	public static final String TABLE_TEAMS = "teams";
	
	public static final String COL_TEAM_NUMBER = "number";
	public static final String COL_TEAM_NAME = "name";
	public static final String COL_SCHOOL = "school";
	public static final String COL_CITY = "city";
	public static final String COL_ROOKIE_YEAR = "rookieyear";
	public static final String COL_WEBSITE = "website";
	public static final String COL_STATE_POSTAL_CODE = "state";
	public static final String COL_COLORS = "colors";
	
	public static final String CREATE_TABLE_TEAMS = 
			"CREATE TABLE " + TABLE_TEAMS + " (" + COL_TEAM_NUMBER + INT + ", " + COL_TEAM_NAME + STRING + 
					", " + COL_SCHOOL + STRING + ", " + COL_CITY + STRING + ", " + COL_ROOKIE_YEAR + 
					INT + ", " + COL_WEBSITE + STRING + ", " + COL_STATE_POSTAL_CODE + STRING + 
					", " + COL_COLORS + STRING + ")";
	
	
	
	public static final String TABLE_COMMENTS = "comments";
	
	//Team number, use COL_NUMBER;
	//User ID, use COL_USER_ID;
	public static final String COL_EVENT_ID = "eventid";
	public static final String COL_COMMENT = "comment";
	public static final String COL_DATE_STAMP = "datestamp";
	
	public static final String CREATE_TABLE_COMMENTS = 
			"CREATE TABLE " + TABLE_COMMENTS + " (" + COL_TEAM_NUMBER + INT + ", " + COL_USER_ID + INT + 
			", " + COL_EVENT_ID + INT + ", " + COL_COMMENT + STRING + ", " + COL_DATE_STAMP + INT 
			+ ")";
	
	
	
	public static final String TABLE_PICTURES = "pictures";
	
	public static final String COL_ROBOT_ID = "robotid";
	public static final String COL_FILE_PATH = "filepath";
	//Date stamp, user COL_DATE_STAMP
	
	public static final String CREATE_TABLE_PICTURES = 
			"CREATE TABLE " + TABLE_PICTURES + " (" + COL_ROBOT_ID + INT + ", " + 
					COL_FILE_PATH + STRING + ", " + COL_DATE_STAMP + INT +")";
	
	
	
	public static final String TABLE_CONTACTS = "contacts";
	
	//Team number, use COL_NUMBER
	public static final String COL_CONTACT_ID = "contactid";
	public static final String COL_CONTACT_NAME = "contactname";
	public static final String COL_EMAIL = "email";
	public static final String COL_ADDRESS = "address";
	public static final String COL_PHONE_NUMBER = "phonenumber";
	
	public static final String CREATE_TABLE_CONTACTS = 
			"CREATE TABLE " + TABLE_CONTACTS + " (" + COL_TEAM_NUMBER + INT + ", " + 
					COL_CONTACT_ID + STRING +", " + COL_CONTACT_NAME + STRING + ", " + 
					COL_EMAIL + STRING + ", " + COL_ADDRESS + STRING + ", " + 
					COL_PHONE_NUMBER + STRING + ")";
					
	
	
	public static final String TABLE_ROBOTS = "robots";
	
	//Team number, use COL_NUMBER
	//Robot ID, use COL_ROBOT_ID
	public static final String COL_GAME_NAME = "gamename";
	public static final String COL_NUMBER_WHEELS = "numberwheels";
	public static final String COL_WHEEL_TYPE = "wheeltype";
	public static final String COL_DRIVETRAIN = "drivetrain";
	public static final String[] COL_KEYS = 
		{"d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10", "d11", "d12", "d13", "d14", "d15"};
	
	public static final String CREATE_TABLE_ROBOTS = 
			"CREATE TABLE " + TABLE_ROBOTS + " (" + COL_TEAM_NUMBER + INT + ", " + COL_ROBOT_ID + 
					INT + ", " + COL_GAME_NAME + STRING + ", " + COL_NUMBER_WHEELS + INT + ", " + 
					COL_WHEEL_TYPE + STRING + ", " + COL_DRIVETRAIN + STRING + ", " + COL_KEYS[0] + ANY + 
					", " + COL_KEYS[1] + ANY + ", " + COL_KEYS[2] + ANY + ", " + COL_KEYS[3] + ANY + 
					", " + COL_KEYS[4] + ANY + ", " + COL_KEYS[5] + ANY + ", " + COL_KEYS[6] + ANY + 
					", " + COL_KEYS[7] + ANY + ", " + COL_KEYS[8] + ANY + ", " + COL_KEYS[9] + ANY + 
					", " + COL_KEYS[10] + ANY + ", " + COL_KEYS[11] + ANY + ", " + COL_KEYS[12] + ANY + 
					", " + COL_KEYS[13] + ANY + ", " + COL_KEYS[14] + ANY + ", " + 
					COL_KEYS[15] + ANY + ")";
	
	
	
	public static final String TABLE_COMPETITIONS = "competitions";
	
	//Game ID, use COL_GAME_NAME
	//Event ID, use COL_EVENT_ID
	public static final String COL_EVENT_NAME = "eventname";
	//Date, use COL_DATE_STAMP
	public static final String COL_LOCATION = "location";
	public static final String COL_FMS_EVENT_ID = "fmseventid";
	
	public static final String CREATE_TABLE_COMPETITIONS =
			"CREATE TABLE " + TABLE_COMPETITIONS + " (" + COL_GAME_NAME + STRING + ", " + 
					COL_EVENT_ID + INT + ", " + COL_EVENT_NAME + STRING + ", " + 
					COL_DATE_STAMP + INT + ", " + COL_LOCATION +STRING + ", " + 
					COL_FMS_EVENT_ID + STRING + ")";
	
	
	
	public static final String TABLE_GAMES = "games";
	
	//Game name, use COL_GAME_NAME
	
	public static final String CREATE_TABLE_GAMES = 
			"CREATE TABLE " + TABLE_GAMES + " (" + COL_GAME_NAME + STRING + ")";
	
	
	
	public static final String TABLE_MATCH_PERF = "matchperformance";
	
	//Robot ID, use COL_ROBOT_ID
	//Event ID, use EVENT_ID
	//User ID, use COL_USER_ID
	public static final String COL_MATCH_NUMBER = "matchnumber";
	public static final String COL_MATCH_TYPE = "matchtype";
	public static final String COL_AUTO_SCORE = "autoscore";
	public static final String COL_TELEOP_SCORE = "teleopscore";
	public static final String COL_PENALTIES = "penalties";
	public static final String COL_COMMENTS = "comments";
	public static final String COL_DRIVER_RATING = "driverrating";
	//Keys, use COL_KEYS[]
	
	public static final String CREATE_TABLE_MATCH_PERF = 
			"CREATE TABLE " + TABLE_MATCH_PERF + " (" + COL_ROBOT_ID + INT + ", " + 
					COL_EVENT_ID + INT + ", " + COL_USER_ID + INT + ", " + 
					COL_MATCH_NUMBER + INT + ", " + COL_MATCH_TYPE + STRING + ", " +
					COL_AUTO_SCORE + INT + ", " + COL_TELEOP_SCORE + INT + ", " +
					COL_PENALTIES + INT + ", " + COL_COMMENTS + STRING + ", " +
					COL_DRIVER_RATING + INT + ", " + COL_KEYS[0] + ANY + ", " + COL_KEYS[1] + ANY + 
					", " + COL_KEYS[2] + ANY + ", " + COL_KEYS[3] + ANY + ", " + COL_KEYS[4] + ANY + 
					", " + COL_KEYS[5] + ANY + ", " + COL_KEYS[6] + ANY + ", " + COL_KEYS[7] + ANY + 
					", " + COL_KEYS[8] + ANY + ", " + COL_KEYS[9] + ANY + ", " + COL_KEYS[10] + ANY + 
					", " + COL_KEYS[11] + ANY + ", " + COL_KEYS[12] + ANY + ", " + COL_KEYS[13] + ANY + 
					", " + COL_KEYS[14] + ANY + ", " + COL_KEYS[15] + ANY + ")";
	
	
	
	/*
	 * Metrics are defined here. These are what kind of data we are collecting
	 * with a certain metric, either about the robot itself, or its performance
	 * during matches. The COL_KEY of a metric maps to a number, 0 - 15, on
	 * the column of the Match Performance table or the Robots table.
	 *
	 * METRIC TYPE MAP
	 * 
	 * 0 - BOOLEAN
	 * 1 - COUNTER
	 * 2 - SLIDER
	 * 3 - CHOOSER
	 * 4 - TEXT
	 */
	
	public static final String TABLE_ROBOT_METRICS = "robotmetrics";
	
	//Game ID, use COL_GAME_NAME
	public static final String COL_METRIC_NAME = "metricname";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_METRIC_KEY = "metrickey";
	public static final String COL_TYPE = "type";
	public static final String COL_RANGE = "range";
	
	public static final String CREATE_TABLE_ROBOT_METRICS =
			"CREATE TABLE " + TABLE_ROBOT_METRICS + " (" + 
					COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " + 
					COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + INT + ", " + 
					COL_TYPE + INT + ", " + COL_RANGE + STRING + ")";
	
	
	
	public static final String TABLE_MATCH_PERF_METRICS = "compmetrics";
	
	//Game ID, use COL_GAME_NAME
	//Metric Name, use COL_METRIC_NAME
	//Description, use COL_DESCRIPTION
	//Key, use COL_KEY
	//Type, use COL_TYPE
	//Range, use COL_RANGE
	
	public static final String CREATE_TABLE_MATCH_PERF_METRICS = 
			"CREATE TABLE " + TABLE_MATCH_PERF_METRICS + " (" + 
					COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " + 
					COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + STRING + ", " + 
					COL_TYPE + INT + ", " + COL_RANGE + STRING + ")";
	
	
	
	//Additions may be made to this contract to include tables for Awards and OPR or CCWM
	
	/*****
	 * Common SQL statements
	 *****/
	
	public static final String SELECT_ALL_TEAM_DATA = "SELECT * FROM " + TABLE_TEAMS;
	
	/*****
	 * Method: creatAllTables
	 * 
	 * Summary: This method puts all tables in this contract into the database object
	 * passed as a parameter.  If this contract is added to, this method must also
	 * be added to to make the changes appear in the database.
	 *****/
	
	public static void createAllTables(SQLiteDatabase database) {
		
		database.execSQL(CREATE_TABLE_USERS);
		database.execSQL(CREATE_TABLE_TEAMS);
		database.execSQL(CREATE_TABLE_COMMENTS);
		database.execSQL(CREATE_TABLE_PICTURES);
		database.execSQL(CREATE_TABLE_CONTACTS);
		database.execSQL(CREATE_TABLE_ROBOTS);
		database.execSQL(CREATE_TABLE_COMPETITIONS);
		database.execSQL(CREATE_TABLE_GAMES);
		database.execSQL(CREATE_TABLE_MATCH_PERF);
		database.execSQL(CREATE_TABLE_ROBOT_METRICS);
		database.execSQL(CREATE_TABLE_MATCH_PERF_METRICS);
	}
	
	/*****
	 * Method: dropAllTables()
	 * 
	 * Summary: Removes all tables and the data in them from the database.
	 * This cannot be undone.
	 *****/
	
	public static void dropAllTables(SQLiteDatabase database) {
		
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ROBOTS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPETITIONS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH_PERF);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH_PERF_METRICS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ROBOT_METRICS);
	}
	
	private DatabaseContract() {}

}
