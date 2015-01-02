package com.team2052.frckrawler.core.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.DaoMaster;

/**
 * @author Adam
 * @since 10/7/2014
 */
public class DatabaseHelper extends DaoMaster.OpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldSchemaVer, int newSchemaVer) {
        LogHelper.info("Upgrading Schema Version from " + oldSchemaVer + " to " + newSchemaVer);
    }
}
