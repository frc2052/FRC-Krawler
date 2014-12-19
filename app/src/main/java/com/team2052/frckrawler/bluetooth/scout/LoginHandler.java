package com.team2052.frckrawler.bluetooth.scout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.User;

import java.util.List;

/**
 * @author Adam
 * @since 12/18/2014.
 */
public class LoginHandler
{
    public static volatile LoginHandler instance;
    private final Context context;
    private DaoSession daoSession;
    private User loggedOnUser = null;

    private LoginHandler(Context context, DaoSession daoSession)
    {
        this.context = context;
        this.daoSession = daoSession;
    }

    public static LoginHandler getInstance(Context context, DaoSession daoSession)
    {
        if (instance == null) synchronized (LoginHandler.class) {
            if (instance == null) instance = new LoginHandler(context, daoSession);
        }
        return instance;
    }


    public boolean isLoggedOn()
    {
        return loggedOnUser != null;
    }

    public void login()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final List<User> users = daoSession.getUserDao().loadAll();
        String[] userNames = new String[users.size()];

        for (int i = 0; i < userNames.length; i++) {
            userNames[i] = users.get(i).getName();
        }
        builder.setCancelable(false);
        builder.setTitle("Login");
        builder.setIcon(R.drawable.ic_person_black_24dp);
        builder.setItems(userNames, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                loggedOnUser = users.get(which);
            }
        });
        builder.create().show();
    }

    public boolean loggedOnUserStillExists()
    {
        if (loggedOnUser == null) {
            return false;
        }
        return daoSession.getUserDao().load(loggedOnUser.getId()) != null;
    }

    public User getLoggedOnUser()
    {
        return loggedOnUser;
    }

    public void loggOff()
    {
        loggedOnUser = null;
    }
}
