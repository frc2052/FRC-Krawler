package com.team2052.frckrawler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.team2052.frckrawler.util.LogHelper;

import de.greenrobot.dao.internal.SqlUtils;
import frckrawler.DaoMaster;

/**
 * @author Adam
 * @since 10/7/2014
 */
public class DatabaseHelper extends DaoMaster.OpenHelper
{
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory)
    {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldSchemaVer, int newSchemaVer)
    {
        LogHelper.info("Upgrading Schema Version from " + oldSchemaVer + " to " + newSchemaVer);
        //Where you update the schema
        //EX db.execSQL("ALTER TABLE ROBOT ADD COLUMN '_ID' INTEGER");
        onCreate(db);
    }
}
