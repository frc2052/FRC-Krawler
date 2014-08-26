package com.team2052.frckrawler.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * **
 * Class: Database Contract
 * <p/>
 * Summary: This is a representation of the structure of the database. Any statements
 * that edit the database content should use these constants rather than referencing
 * the tables and columns explicitly. Any additions to the schema of the database
 * should be made here. Any changes in the columns of a table should also be out into
 * the CREATE_TABLE_* constant for the table. Any tables added to the schema should
 * be also added to the createAllTables() and dropAllTables() method at the bottom.
 * If they are not, the changes will not be reflected in the structure of the
 * actual database.
 * <p/>
 * This class also contains several helper methods for large-scale database
 * management that can't be done with one query.
 *
 * @author Charles Hofer
 *         ***
 */

public class DBContract {


    /**
     * **
     * SQL Data Types
     * ***
     */

    public static final String INT = " INTEGER";
    public static final String DECIMAL = " REAL";
    public static final String STRING = " TEXT";
    public static final String BLOB = " BLOB";
    public static final String ANY = " NUMERIC";

    /**
     * **
     * Scouting Metric Map
     * <p/>
     * The scouting data types are the input method for the scouts.
     * ***
     */

    public static final int BOOLEAN = 0;
    public static final int COUNTER = 1;
    public static final int SLIDER = 2;
    public static final int CHOOSER = 3;
    public static final int TEXT = 4;
    public static final int MATH = 5;

    public static final int HIGHEST_TYPE = 5;

    /**
     * **
     * Schema
     * ***
     */
    public static final String DATABASE_NAME = "scoutingdb";
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "userid";
    public static final String COL_USER_NAME = "username";
    public static final String COL_SUPERUSER = "superuser";
    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" + COL_USER_ID + INT + ", " + COL_USER_NAME + STRING + ", " + COL_SUPERUSER + STRING + ")";
    public static final String TABLE_TEAMS = "teams";
    public static final String COL_TEAM_NUMBER = "number";

    /**
     * **
     * Common SQL statements
     * ***
     */
    public static final String SELECT_ALL_TEAM_DATA = "SELECT * FROM " + TABLE_TEAMS + " ORDER BY " + COL_TEAM_NUMBER + " ASC";
    public static final String COL_TEAM_NAME = "name";
    public static final String COL_SCHOOL = "school";
    public static final String COL_CITY = "city";
    public static final String COL_ROOKIE_YEAR = "rookieyear";
    public static final String COL_WEBSITE = "website";
    public static final String COL_STATE_POSTAL_CODE = "state";
    public static final String COL_COLORS = "colors";
    public static final String CREATE_TABLE_TEAMS = "CREATE TABLE " + TABLE_TEAMS + " (" + COL_TEAM_NUMBER + INT + ", " + COL_TEAM_NAME + STRING + ", " + COL_SCHOOL + STRING + ", " + COL_CITY + STRING + ", " + COL_ROOKIE_YEAR + INT + ", " + COL_WEBSITE + STRING + ", " + COL_STATE_POSTAL_CODE + STRING + ", " + COL_COLORS + STRING + ")";
    public static final String TABLE_COMMENTS = "comments";
    //Team number, use COL_TEAM_NUMBER;
    //User ID, use COL_USER_ID;
    public static final String COL_EVENT_ID = "eventid";
    public static final String COL_COMMENT = "comment";
    public static final String COL_DATE_STAMP = "datestamp";
    public static final String CREATE_TABLE_COMMENTS = "CREATE TABLE " + TABLE_COMMENTS + " (" + COL_TEAM_NUMBER + INT + ", " + COL_USER_ID + INT + ", " + COL_EVENT_ID + INT + ", " + COL_COMMENT + STRING + ", " + COL_DATE_STAMP + INT + ")";
    public static final String TABLE_PICTURES = "pictures";
    public static final String COL_ROBOT_ID = "robotid";
    public static final String COL_FILE_PATH = "filepath";
    //Date stamp, user COL_DATE_STAMP
    public static final String CREATE_TABLE_PICTURES =
            "CREATE TABLE " + TABLE_PICTURES + " (" + COL_ROBOT_ID + INT + ", " +
                    COL_FILE_PATH + STRING + ", " + COL_DATE_STAMP + INT + ")";
    public static final String TABLE_CONTACTS = "contacts";
    //Team number, use COL_NUMBER
    public static final String COL_CONTACT_ID = "contactid";
    public static final String COL_CONTACT_NAME = "contactname";
    public static final String COL_EMAIL = "email";
    public static final String COL_ADDRESS = "address";
    public static final String COL_PHONE_NUMBER = "phonenumber";
    public static final String CREATE_TABLE_CONTACTS =
            "CREATE TABLE " + TABLE_CONTACTS + " (" + COL_TEAM_NUMBER + INT + ", " +
                    COL_CONTACT_ID + STRING + ", " + COL_CONTACT_NAME + STRING + ", " +
                    COL_EMAIL + STRING + ", " + COL_ADDRESS + STRING + ", " +
                    COL_PHONE_NUMBER + STRING + ")";
    public static final String TABLE_ROBOTS = "robots";
    //Team number, use COL_NUMBER
    //Robot ID, use COL_ROBOT_ID
    public static final String COL_GAME_NAME = "gamename";
    public static final String COL_COMMENTS = "comments";
    public static final String COL_IMAGE_PATH = "imagepath";
    public static final String COL_OPR = "opr";
    public static final String TABLE_EVENTS = "events";
    //Game ID, use COL_GAME_NAME
    //Event ID, use COL_EVENT_ID
    public static final String COL_EVENT_NAME = "eventname";
    //Date, use COL_DATE_STAMP
    public static final String COL_LOCATION = "location";
    public static final String COL_FMS_EVENT_ID = "fmseventid";
    public static final String CREATE_TABLE_COMPETITIONS =
            "CREATE TABLE " + TABLE_EVENTS + " (" + COL_GAME_NAME + STRING + ", " +
                    COL_EVENT_ID + INT + ", " + COL_EVENT_NAME + STRING + ", " +
                    COL_DATE_STAMP + INT + ", " + COL_LOCATION + STRING + ", " +
                    COL_FMS_EVENT_ID + STRING + ")";
    public static final String TABLE_SCHEDULES = "schedules";
    //Event ID, use COL_EVENT_ID
    public static final String COL_MATCH_NUMBER = "matchnumber";
    public static final String COL_RED1 = "red1";
    public static final String COL_RED2 = "red2";
    public static final String COL_RED3 = "red3";
    public static final String COL_BLUE1 = "blue1";
    public static final String COL_BLUE2 = "blue2";
    public static final String COL_BLUE3 = "blue3";
    public static final String COL_RED_SCORE = "redscore";
    public static final String COL_BLUE_SCORE = "bluescore";
    public static final String CREATE_TABLE_SCHEDULES =
            "CREATE TABLE " + TABLE_SCHEDULES + " (" +
                    COL_EVENT_ID + INT + ", " + COL_MATCH_NUMBER + INT + ", " +
                    COL_RED1 + INT + ", " + COL_RED2 + INT + ", " +
                    COL_RED3 + INT + ", " + COL_BLUE1 + INT + ", " +
                    COL_BLUE2 + INT + ", " + COL_BLUE3 + INT + ", " +
                    COL_RED_SCORE + INT + ", " + COL_BLUE_SCORE + INT + ");";
    public static final String TABLE_GAMES = "games";
    public static final String CREATE_TABLE_GAMES =
            "CREATE TABLE " + TABLE_GAMES + " (" + COL_GAME_NAME + STRING + ")";
    public static final String TABLE_MATCH_PERF = "matchperformance";
    public static final String COL_DATA_ID = "matchid";

    //Game name, use COL_GAME_NAME
    //Robot ID, use COL_ROBOT_ID
    //Event ID, use EVENT_ID
    //User ID, use COL_USER_ID
    //Match Number, user COL_MATCH_NUMBER
    public static final String COL_MATCH_TYPE = "matchtype";
    /*THIS TABLE IS CURRENTLY UNUSED*/
    public static final String TABLE_DRIVER_DATA = "driverdata";
    public static final String TABLE_ROBOT_METRICS = "robotmetrics";
    //Game ID, use COL_GAME_NAME
    public static final String COL_METRIC_ID = "metricid";
    public static final String COL_METRIC_NAME = "metricname";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_METRIC_KEY = "metrickey";


    /*
     * Metrics are defined here. These are what kind of data we are collecting
     * with a certain addbutton, either about the robot itself, or its performance
     * during matches. The COL_KEY of a addbutton maps to a number, 0 - 15, on
     * the column of the Match Performance table or the Robots table.
     *
     * METRIC TYPE MAP
     *
     * 0 - BOOLEAN
     * 1 - COUNTER
     * 2 - SLIDER
     * 3 - CHOOSER
     * 4 - TEXT
     * 5 - MATH
     */
    public static final String COL_TYPE = "type";
    public static final String COL_RANGE = "range";
    public static final String COL_DISPLAY = "display";
    public static final String COL_POSITION = "metricPosition";
    public static final String CREATE_TABLE_ROBOT_METRICS =
            "CREATE TABLE " + TABLE_ROBOT_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " +
                    COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + INT + ", " +
                    COL_TYPE + INT + ", " + COL_RANGE + STRING + ", " + COL_DISPLAY + INT +
                    ", " + COL_POSITION + INT + ")";
    public static final String TABLE_MATCH_PERF_METRICS = "compmetrics";
    //Game ID, use COL_GAME_NAME
    //Metric Name, use COL_METRIC_NAME
    //Description, use COL_DESCRIPTION
    //Key, use COL_KEY
    //Type, use COL_TYPE
    //Range, use COL_RANGE
    //Display, use COL_DISPLAY
    //Position, use COL_POSITION
    public static final String CREATE_TABLE_MATCH_PERF_METRICS =
            "CREATE TABLE " + TABLE_MATCH_PERF_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " +
                    COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + STRING + ", " +
                    COL_TYPE + INT + ", " + COL_RANGE + STRING + ", " + COL_DISPLAY + INT +
                    ", " + COL_POSITION + INT + ")";
    public static final String TABLE_DRIVER_METRICS = "drivermetrics";
    //Game ID, use COL_GAME_NAME
    //Metric Name, use COL_METRIC_NAME
    //Description, use COL_DESCRIPTION
    //Key, use COL_KEY
    //Type, use COL_TYPE
    //Range, use COL_RANGE
    //
    public static final String CREATE_TABLE_DRIVER_METRICS =
            "CREATE TABLE " + TABLE_DRIVER_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " +
                    COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + STRING + ", " +
                    COL_TYPE + INT + ", " + COL_RANGE + STRING + ", " + COL_DISPLAY + INT +
                    ")";
    public static final String TABLE_EVENT_ROBOTS = "eventrobots";
    //Event ID, use COL_EVENT_ID
    //Robot ID, use COL_ROBOT_ID
    public static final String COL_IS_CHECKED = "isChecked";
    public static final String CREATE_TABLE_EVENT_ROBOTS =
            "CREATE TABLE " + TABLE_EVENT_ROBOTS + " (" + COL_EVENT_ID + INT + ", " +
                    COL_ROBOT_ID + INT + ", " + COL_IS_CHECKED + INT + ")";
    public static final String TABLE_LISTS = "lists";
    //Event ID, use COL_EVENT_ID
    public static final String COL_LIST_ID = "listid";
    public static final String COL_LIST_NAME = "listname";
    //Description, us COL_DESCRIPTION
    public static final String CREATE_TABLE_LISTS =
            "CREATE TABLE " + TABLE_LISTS + " (" + COL_EVENT_ID + INT + ", " +
                    COL_LIST_ID + INT + ", " + COL_LIST_NAME + STRING + ", " +
                    COL_DESCRIPTION + STRING + ")";
    public static final String TABLE_LIST_ENTRIES = "listentries";
    public static final String CREATE_TABLE_LIST_ENTRIES =
            "CREATE TABLE " + TABLE_LIST_ENTRIES + " (" + COL_LIST_ID + INT + ", " +
                    COL_ROBOT_ID + INT + ", " + COL_POSITION + INT + ")";
    /**
     * ************************************
     * SCOUT TABLES
     * <p/>
     * These tables are used if the device is the client
     * to another device's database.
     * *************************************
     */

    public static final String SCOUT_TABLE_USERS = "scoutusers";
    //COL_USER_ID = "userid";
    //COL_USER_NAME = "username";
    public static final String CREATE_SCOUT_TABLE_USERS =
            "CREATE TABLE " + SCOUT_TABLE_USERS + " (" + COL_USER_ID + INT + ", " + COL_USER_NAME +
                    STRING + ")";
    public static final String SCOUT_TABLE_EVENT = "scoutevent";
    //Game ID, use COL_GAME_NAME
    //Event ID, use COL_EVENT_ID
    //COL_EVENT_NAME = "eventname";
    //Date, use COL_DATE_STAMP
    //COL_LOCATION = "location";
    //COL_FMS_EVENT_ID = "fmseventid";
    public static final String CREATE_SCOUT_TABLE_EVENT =
            "CREATE TABLE " + SCOUT_TABLE_EVENT + " (" + COL_GAME_NAME + STRING + ", " +
                    COL_EVENT_ID + INT + ", " + COL_EVENT_NAME + STRING + ", " +
                    COL_DATE_STAMP + INT + ", " + COL_LOCATION + STRING + ", " +
                    COL_FMS_EVENT_ID + STRING + ")";

    //List ID, use COL_LIST_ID
    //Robot ID, use COL_ROBOT_ID
    //Position, use COL_POSITION
    public static final String SCOUT_TABLE_ROBOTS = "scoutrobots";
    //Team number, use COL_NUMBER
    //Robot ID, use COL_ROBOT_ID
    //COL_GAME_NAME = "gamename";
    //COL_COMMENTS = "comments";
    //COL_IMAGE_PATH
    //COL_KEYS = new String[50];
    //COL_KEYS_LIST;
    public static final String COL_WAS_UPDATED = "wasupdated";
    public static final String SCOUT_TABLE_MATCH_PERF = "scoutmatchperf";
    public static final String SCOUT_TABLE_DRIVER_DATA = "scoutdriverdata";
    public static final String SCOUT_TABLE_ROBOT_METRICS = "scoutrobotmetrics";
    public static final String CREATE_SCOUT_TABLE_ROBOT_METRICS =
            "CREATE TABLE " + SCOUT_TABLE_ROBOT_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " +
                    COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + INT + ", " +
                    COL_TYPE + INT + ", " + COL_RANGE + STRING + ", " + COL_DISPLAY + INT + ", " +
                    COL_POSITION + INT + ")";
    public static final String SCOUT_TABLE_MATCH_PERF_METRICS = "scoutmatchmetrics";
    //COL_TEAM_NAME
    //COL_OPR
    //Game ID, use COL_GAME_NAME
    //Metric Name, use COL_METRIC_NAME
    //Description, use COL_DESCRIPTION
    //Key, use COL_KEY
    //Type, use COL_TYPE
    //Range, use COL_RANGE
    //Display, use COL_DISPLAY
    //Position, use COL_POSITION
    public static final String CREATE_SCOUT_TABLE_MATCH_PERF_METRICS =
            "CREATE TABLE " + SCOUT_TABLE_MATCH_PERF_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " +
                    COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + STRING + ", " +
                    COL_TYPE + INT + ", " + COL_RANGE + STRING + ", " + COL_DISPLAY + INT +
                    ", " + COL_POSITION + INT + ")";
    public static final String SCOUT_TABLE_DRIVER_METRICS = "scoutdrivermetrics";

    //Data ID, use COL_DATA_ID
    //Robot ID, use COL_ROBOT_ID
    //Event ID, use EVENT_ID
    //User ID, use COL_USER_ID
    //"matchnumber";
    //"matchtype";
    //Comments, use COL_COMMENTS

    //Keys, use COL_KEYS[]
    //Game ID, use COL_GAME_NAME
    //Metric Name, use COL_METRIC_NAME
    //Description, use COL_DESCRIPTION
    //Key, use COL_KEY
    //Type, use COL_TYPE
    //Range, use COL_RANGE
    //
    public static final String CREATE_SCOUT_TABLE_DRIVER_METRICS =
            "CREATE TABLE " + SCOUT_TABLE_DRIVER_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_GAME_NAME + STRING + ", " + COL_METRIC_NAME + STRING + ", " +
                    COL_DESCRIPTION + STRING + ", " + COL_METRIC_KEY + STRING + ", " +
                    COL_TYPE + INT + ", " + COL_RANGE + STRING + ", " + COL_DISPLAY + INT +
                    ", " + COL_POSITION + INT + ")";
    public static final String SCOUT_TABLE_SCHEDULE = "scoutschedule";

    //Robot ID, use COL_ROBOT_ID
    //Event ID, use COL_EVENT_ID
    //Match Number, use COL_MATCH_NUMBER
    //Comments, use COL_COMMENTS
    //Keys, use COL_KEYS
    //public static final String COL_MATCH_NUMBER = "matchnumber";
    //public static final String COL_RED1 = "red1";
    //public static final String COL_RED2 = "red2";
    //public static final String COL_RED3 = "red3";
    //public static final String COL_BLUE1 = "blue1";
    //public static final String COL_BLUE2 = "blue2";
    //public static final String COL_BLUE3 = "blue3";
    //public static final String COL_RED_SCORE = "redscore";
    //public static final String COL_BLUE_SCORE = "bluescore";
    public static final String CREATE_SCOUT_TABLE_SCHEDULE =
            "CREATE TABLE " + SCOUT_TABLE_SCHEDULE + " (" +
                    COL_MATCH_NUMBER + INT + ", " +
                    COL_RED1 + INT + ", " + COL_RED2 + INT + ", " +
                    COL_RED3 + INT + ", " + COL_BLUE1 + INT + ", " +
                    COL_BLUE2 + INT + ", " + COL_BLUE3 + INT + ", " +
                    COL_RED_SCORE + INT + ", " + COL_BLUE_SCORE + INT + ");";
    /**
     * *******
     * Summary Tables
     * <p/>
     * These tables are used when a device syncs with the
     * summary buttons.
     * ********
     */
    public static final String[] SUMMARY_MATCH_KEYS = new String[50];
    //Game ID, use COL_GAME_NAME
    //COL_METRIC_ID = "metricid";
    //COL_METRIC_NAME = "metricname";
    //COL_DESCRIPTION = "description";
    //COL_METRIC_KEY = "metrickey";
    //COL_TYPE = "type";
    //COL_RANGE = "range";
    //COL_DISPLAY = "display";
    //Position, use COL_POSITION
    public static final String[] SUMMARY_ROBOT_KEYS = new String[50];
    public static final String SUMMARY_TABLE_ROBOT_METRICS = "summaryrobotmetrics";
    //COL_METRIC_ID = "metricid";
    //COL_METRIC_NAME = "metricname";
    //COL_METRIC_KEY = "metrickey";
    //COL_TYPE = "type";
    //Position, use COL_POSITION
    public static final String CREATE_SUMMARY_TABLE_ROBOT_METRICS =
            "CREATE TABLE " + SUMMARY_TABLE_ROBOT_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_METRIC_NAME + STRING + ", " + COL_METRIC_KEY + INT + ", " +
                    COL_TYPE + INT + ", " + COL_POSITION + INT + ");";
    public static final String SUMMARY_TABLE_MATCH_PERF_METRICS = "summarymatchmetrics";
    //Metric Name, use COL_METRIC_NAME
    //Key, use COL_KEY
    //Type, use COL_TYPE
    //Position, use COL_POSITION
    public static final String CREATE_SUMMARY_TABLE_MATCH_PERF_METRICS =
            "CREATE TABLE " + SUMMARY_TABLE_MATCH_PERF_METRICS + " (" + COL_METRIC_ID + INT + ", " +
                    COL_METRIC_NAME + STRING + ", " + COL_METRIC_KEY + STRING + ", " +
                    COL_TYPE + INT + ", " + COL_POSITION + INT + ");";
    public static final String SUMMARY_TABLE_SUMMARY_DATA = "summarysummarydata";
    public static final String SUMMARY_TABLE_MATCH_PERF = "summarymatchperformance";
    public static String[] COL_KEYS = new String[50];
    public static String COL_KEYS_LIST;

    static {
        COL_KEYS_LIST = new String("");
        for (int i = 0; i < COL_KEYS.length; i++) {
            COL_KEYS[i] = "d" + i;
            COL_KEYS_LIST += COL_KEYS[i] + ANY;
            if (i < COL_KEYS.length - 1) {
                COL_KEYS_LIST += ", ";
            }
        }
    }

    public static final String CREATE_TABLE_ROBOTS =
            "CREATE TABLE " + TABLE_ROBOTS + " (" + COL_TEAM_NUMBER + INT + ", " + COL_ROBOT_ID +
                    INT + ", " + COL_GAME_NAME + STRING + ", " + COL_IMAGE_PATH + STRING + ", "
                    + COL_COMMENTS + STRING + ", " + COL_OPR + DECIMAL +
                    ", " + COL_KEYS_LIST + ")";
    //Comments, use COL_COMMENTS
    //Keys, use COL_KEYS[]
    public static final String CREATE_TABLE_MATCH_PERF =
            "CREATE TABLE " + TABLE_MATCH_PERF + " (" + COL_DATA_ID + INT + ", " +
                    COL_ROBOT_ID + INT + ", " + COL_EVENT_ID + INT + ", " + COL_USER_ID + INT + ", " +
                    COL_MATCH_NUMBER + INT + ", " + COL_MATCH_TYPE + STRING + ", " +
                    COL_COMMENTS + STRING + ", " + COL_KEYS_LIST + ")";
    //Robot ID, use COL_ROBOT_ID
    //Event ID, use COL_EVENT_ID
    //Match Number, use COL_MATCH_NUMBER
    //Comments, use COL_COMMENTS
    //Keys, use COL_KEYS
    public static final String CREATE_TABLE_DRIVER_DATA =
            "CREATE TABLE " + TABLE_DRIVER_DATA + " (" + COL_ROBOT_ID + INT + ", " +
                    COL_EVENT_ID + INT + ", " + COL_MATCH_NUMBER + INT + ", " +
                    COL_COMMENTS + STRING + ", " + COL_KEYS_LIST + ")";
    public static final String CREATE_SCOUT_TABLE_ROBOTS =
            "CREATE TABLE " + SCOUT_TABLE_ROBOTS + " (" + COL_TEAM_NUMBER + INT + ", " + COL_ROBOT_ID +
                    INT + ", " + COL_GAME_NAME + STRING + ", " + COL_COMMENTS + STRING + ", " +
                    COL_IMAGE_PATH + STRING + ", " + COL_KEYS_LIST + ", " +
                    COL_WAS_UPDATED + ", " + COL_TEAM_NAME + STRING + ", " + COL_OPR +
                    DECIMAL + ")";
    public static final String CREATE_SCOUT_TABLE_MATCH_PERF =
            "CREATE TABLE " + SCOUT_TABLE_MATCH_PERF + " (" + COL_DATA_ID + INT + ", " +
                    COL_ROBOT_ID + INT + ", " + COL_EVENT_ID + INT + ", " + COL_USER_ID + INT + ", " +
                    COL_MATCH_NUMBER + INT + ", " + COL_MATCH_TYPE + STRING + ", " +
                    COL_COMMENTS + STRING + ", " + COL_KEYS_LIST + ")";
    public static final String CREATE_SCOUT_TABLE_DRIVER_DATA =
            "CREATE TABLE " + SCOUT_TABLE_DRIVER_DATA + " (" + COL_ROBOT_ID + INT + ", " +
                    COL_EVENT_ID + INT + ", " + COL_MATCH_NUMBER + INT + ", " +
                    COL_COMMENTS + STRING + ", " + COL_KEYS_LIST + ")";
    public static String SUMMARY_MATCH_KEYS_LIST = "";
    //Match Number, user COL_MATCH_NUMBER
    //Team Number
    //Comments, use COL_COMMENTS
    //Keys, use COL_KEYS[]
    public static final String CREATE_SUMMARY_TABLE_MATCH_PERF = "CREATE TABLE " + SUMMARY_TABLE_MATCH_PERF + " (" + COL_TEAM_NUMBER + INT + ", " + COL_MATCH_NUMBER + INT + ", " + COL_COMMENTS + STRING + ")";
    public static String SUMMARY_ROBOT_KEYS_LIST = "";

    static {
        for (int i = 0; i < COL_KEYS.length; i++) {
            SUMMARY_MATCH_KEYS[i] = "md" + i;
            SUMMARY_ROBOT_KEYS[i] = "rd" + i;
            SUMMARY_MATCH_KEYS_LIST += SUMMARY_MATCH_KEYS[i] + ANY;
            SUMMARY_ROBOT_KEYS_LIST += SUMMARY_ROBOT_KEYS[i] + ANY;
            if (i < COL_KEYS.length - 1) {
                SUMMARY_MATCH_KEYS_LIST += ", ";
                SUMMARY_ROBOT_KEYS_LIST += ", ";
            }
        }
    }


    //Additions may be made to this contract to include tables for Awards and OPR or CCWM
    //Checked
    //Team Number
    //OPR
    //Match Data
    //Robot Data
    public static final String CREATE_SUMMARY_TABLE_SUMMARY_DATA =
            "CREATE TABLE " + SUMMARY_TABLE_SUMMARY_DATA + " (" + COL_IS_CHECKED + INT + ", " +
                    COL_TEAM_NUMBER + INT + ", " + COL_OPR + DECIMAL + ", " +
                    SUMMARY_MATCH_KEYS_LIST + ", " + SUMMARY_ROBOT_KEYS_LIST + ");";

    private DBContract() {
    }

    /**
     * **
     * Method: creatAllTables
     * <p/>
     * Summary: This method puts all tables in this contract into the database object
     * passed as a parameter.  If this contract is added to, this method must also
     * be added to to make the changes appear in the database.
     * ***
     */

    public static void createSchema(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USERS);
        database.execSQL(CREATE_TABLE_TEAMS);
        database.execSQL(CREATE_TABLE_COMMENTS);
        database.execSQL(CREATE_TABLE_PICTURES);
        database.execSQL(CREATE_TABLE_CONTACTS);
        database.execSQL(CREATE_TABLE_ROBOTS);
        database.execSQL(CREATE_TABLE_COMPETITIONS);
        database.execSQL(CREATE_TABLE_GAMES);
        database.execSQL(CREATE_TABLE_SCHEDULES);
        database.execSQL(CREATE_TABLE_MATCH_PERF);
        database.execSQL(CREATE_TABLE_ROBOT_METRICS);
        database.execSQL(CREATE_TABLE_MATCH_PERF_METRICS);
        database.execSQL(CREATE_TABLE_EVENT_ROBOTS);
        database.execSQL(CREATE_TABLE_DRIVER_DATA);
        database.execSQL(CREATE_TABLE_DRIVER_METRICS);
        database.execSQL(CREATE_TABLE_LISTS);
        database.execSQL(CREATE_TABLE_LIST_ENTRIES);

        //SCOUT TABLES//
        database.execSQL(CREATE_SCOUT_TABLE_USERS);
        database.execSQL(CREATE_SCOUT_TABLE_EVENT);
        database.execSQL(CREATE_SCOUT_TABLE_ROBOTS);
        database.execSQL(CREATE_SCOUT_TABLE_MATCH_PERF);
        database.execSQL(CREATE_SCOUT_TABLE_DRIVER_DATA);
        database.execSQL(CREATE_SCOUT_TABLE_ROBOT_METRICS);
        database.execSQL(CREATE_SCOUT_TABLE_MATCH_PERF_METRICS);
        database.execSQL(CREATE_SCOUT_TABLE_DRIVER_METRICS);
        database.execSQL(CREATE_SCOUT_TABLE_SCHEDULE);

        //SUMMARY TABLES//
        database.execSQL(CREATE_SUMMARY_TABLE_ROBOT_METRICS);
        database.execSQL(CREATE_SUMMARY_TABLE_MATCH_PERF_METRICS);
        database.execSQL(CREATE_SUMMARY_TABLE_MATCH_PERF);
        database.execSQL(CREATE_SUMMARY_TABLE_SUMMARY_DATA);
    }

    /**
     * **
     * Method: dropAllTables()
     * <p/>
     * Summary: Removes all tables and the data in them from the database.
     * This cannot be undone.
     * ***
     */
    public static void dropSchema(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ROBOTS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH_PERF);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH_PERF_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ROBOT_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_ROBOTS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER_DATA);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_ENTRIES);

        //SCOUT TABLES//
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_USERS);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_EVENT);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_ROBOTS);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_MATCH_PERF);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_DRIVER_DATA);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_ROBOT_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_MATCH_PERF_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_DRIVER_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + SCOUT_TABLE_SCHEDULE);

        //SUMMARY TABLES//
        database.execSQL("DROP TABLE IF EXISTS " + SUMMARY_TABLE_SUMMARY_DATA);
        database.execSQL("DROP TABLE IF EXISTS " + SUMMARY_TABLE_MATCH_PERF_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + SUMMARY_TABLE_ROBOT_METRICS);
        database.execSQL("DROP TABLE IF EXISTS " + SUMMARY_TABLE_MATCH_PERF);
    }

}
