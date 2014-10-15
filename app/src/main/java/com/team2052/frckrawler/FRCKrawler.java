package com.team2052.frckrawler;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.team2052.frckrawler.database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import frckrawler.DaoMaster;
import frckrawler.DaoSession;

/**
 * @author Adam
 */
public class FRCKrawler extends Application
{

    private DaoSession daoSession;
    private DaoMaster daoMaster;

    public static DaoSession getDaoSession(Context context)
    {
        return ((FRCKrawler) context.getApplicationContext()).getDaoSession();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        setupDB();
    }

    private void setupDB()
    {
        DatabaseHelper helper = new DatabaseHelper(this, "FRCKrawler", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }

    public File copyDB(File newPath) throws Exception
    {
        File currentDB = new File(daoMaster.getDatabase().getPath());
        if (currentDB.exists()) {
            FileOutputStream fileOutputStream = new FileOutputStream(newPath);
            FileInputStream fileInputStream = new FileInputStream(currentDB);
            FileChannel src = fileInputStream.getChannel();
            FileChannel dst = fileOutputStream.getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            fileOutputStream.close();
            fileInputStream.close();
        }
        return newPath;
    }

}
