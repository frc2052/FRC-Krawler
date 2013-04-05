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
	 * The default color for the rows of tables.
	 */
	public static int ROW_COLOR = Color.rgb(36, 37, 85);
	
	/**
	 * The user id of a a logged in user. Initialized to 0 until
	 * somebody logs in.
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
