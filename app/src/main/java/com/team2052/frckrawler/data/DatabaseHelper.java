package com.team2052.frckrawler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.team2052.frckrawler.models.DaoMaster;
import com.team2052.frckrawler.models.ServerLogEntryDao;

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
        if (oldSchemaVer < 2) {
            //All current metrics should be enabled by default
            db.execSQL("ALTER TABLE 'METRIC' ADD COLUMN ENABLED INTEGER DEFAULT 1");
        }

        if (oldSchemaVer < 3) {
            db.execSQL("ALTER TABLE 'EVENT' ADD COLUMN UNIQUE_HASH TEXT_FIELD");
        }

        if (oldSchemaVer < 4) {
            db.execSQL("DROP TABLE IF EXISTS 'USER'");
        }

        if (oldSchemaVer < 5) {
            db.execSQL("ALTER TABLE 'MATCH_DATA' RENAME TO 'MATCH_DATUM'");
            db.execSQL("ALTER TABLE 'PIT_DATA' RENAME TO 'PIT_DATUM'");
        }

        if (oldSchemaVer < 6) {
            ServerLogEntryDao.createTable(wrap(db), false);
        }

        if (oldSchemaVer < 7) {
            db.execSQL("ALTER TABLE 'METRIC' ADD COLUMN PRIORITY INTEGER DEFAULT 0");
        }
    }
}
