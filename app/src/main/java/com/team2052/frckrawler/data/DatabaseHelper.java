package com.team2052.frckrawler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.team2052.frckrawler.models.DaoMaster;

/**
 * @author Adam
 * @since 10/7/2014
 */
public class DatabaseHelper extends DaoMaster.OpenHelper {
    public static final String LOG_TAG = "DatabaseHelper";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldSchemaVer, int newSchemaVer) {
        Log.i(LOG_TAG, "Upgrading Schema Version from " + oldSchemaVer + " to " + newSchemaVer);
    }
}
