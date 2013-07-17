package com.team2052.frckrawler.database;

import android.content.*;
import android.database.sqlite.*;

/*****
 * Class: DatabaseHelper
 * 
 * Summary: This extends the SQLiteHelper class. It handles the database on a lower level
 * than the DatabaseManager and should only be used by the DatabaseManager. Methods that
 * make changes to the database should be executed on a different thread than the
 * DatabaseManager's thread.
 *****/

/*
 * WARNING: When onUpgrade or onDowngrade is called, the database is wiped clean and rebuilt, this
 * should change in future implementation and is only done for development purposes.
 */

public class DBHelper extends SQLiteOpenHelper {
	
	
	public static final int DATABASE_VERSION = 24;	//You must add one when changing the 
		//structure of the database.
	
	public DBHelper(Context context, String path) {
		super(context, path, null, DATABASE_VERSION);
	}
	
	/*****
	 * Method: onCreate
	 * 
	 * Summary: Called only if there is no database already associated with this app.
	 * This is where you should create tables and set up the database.
	 *****/
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		DBContract.createSchema(database);
	}
	
	/*****
	 * Method: onUpgrade
	 * 
	 * Summary: Called when the app itself is updated to a newer version.
	 *****/
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		DBContract.dropSchema(database);
		onCreate(database);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		onUpgrade(database, oldVersion, newVersion);
	}
}
