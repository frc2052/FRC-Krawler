package com.team2052.frckrawler;

import android.graphics.Color;

public class GlobalValues {
	
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
