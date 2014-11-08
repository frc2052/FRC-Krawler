package com.team2052.frckrawler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.team2052.frckrawler.db.DaoMaster;
import com.team2052.frckrawler.util.LogHelper;

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

        if (oldSchemaVer == 3 && newSchemaVer == 4) {
            db.execSQL("ALTER TABLE 'MATCH_COMMENT' ADD COLUMN ROBOT_ID INTEGER");
        }
        if (oldSchemaVer == 4 && newSchemaVer == 5) {
            //Schema change add columns DATE INTEGER and TITLE TEXT
            db.execSQL("ALTER TABLE 'ROBOT_PHOTO' ADD COLUMN DATE INTEGER");
            db.execSQL("ALTER TABLE 'ROBOT_PHOTO' ADD COLUMN TITLE TEXT");
        }
        //EX db.execSQL("ALTER TABLE ROBOT ADD COLUMN '_ID' INTEGER");
    }
}
