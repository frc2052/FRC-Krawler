package com.team2052.frckrawler;

import android.graphics.Color;

public class GlobalSettings {
	
	/**
	 * The weightingRatio is how much more the next match
	 * counts more than the last one when compiling the
	 * COUNTER, SLIDER, and numeric CHOOSER metrics.
	 * 
	 * LESS than one means the last match is LESS important
	 * than the first.
	 * 
	 * MORE than one means the last match is MORE important
	 * than the first
	 * 
	 * A VALUE OF ONE means that there is no weight at all,
	 * all matches are treated equally.
	 **/
	public static double weightingRatio = 1.2;
	
	/**
	 * The user has the option to add a robot every time a team
	 * is added. This streamlines the initial data entry process.
	 * The game is what game that robot will be playing.
	 **/
	public static boolean addRobotWithTeam = false;
	public static String robotsGame = "";
	
	/**
	 * The default color for the rows of tables and certain buttons.
	 */
	public static int ROW_COLOR = Color.rgb(36, 37, 85);
	public static int BUTTON_COLOR = Color.rgb(52, 53, 101);
	
	/**
	 * The user id of a logged in user. Initialized to 0 until
	 * somebody logs in. Only used for the scout's interface.
	 */
	public static int userID = 0;
	
	/**
	 * SharedPreferences file name
	 */
	public static final String PREFS_FILE_NAME = "FRCKrawlerPrefs";
	
	/**
	 * The SharedPreferences key for the MAC address of the 
	 * most recently connected server
	 */
	public static final String MAC_ADRESS_PREF = "MACAdress";
}
