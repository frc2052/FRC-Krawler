package com.team2052.frckrawler.bluetooth.client;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.User;

import java.util.List;

/**
 * @author Adam
 * @since 12/18/2014.
 */
public class LoginHandler {
    public static volatile LoginHandler instance;
    private final Context context;
    private DBManager daoSession;
    private User loggedOnUser = null;

    private LoginHandler(Context context, DBManager daoSession) {
        this.context = context;
        this.daoSession = daoSession;
    }

    public static LoginHandler getInstance(Context context, DBManager daoSession) {
        if (instance == null) synchronized (LoginHandler.class) {
            if (instance == null) instance = new LoginHandler(context, daoSession);
        }
        return instance;
    }


    public boolean isLoggedOn() {
        return loggedOnUser != null;
    }

    public void login(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final List<User> users = daoSession.mUsers.loadAll();
        String[] userNames = new String[users.size()];

        for (int i = 0; i < userNames.length; i++) {
            userNames[i] = users.get(i).getName();
        }
        builder.setCancelable(false);
        builder.setTitle("Login");
        builder.setIcon(R.drawable.ic_person_black_24dp);
        builder.setItems(userNames, (dialogInterface, i) -> {
            loggedOnUser = users.get(i);
        });
        builder.create().show();
    }

    public boolean loggedOnUserStillExists() {
        return loggedOnUser != null && daoSession.mUsers.load(loggedOnUser.getId()) != null;
    }

    public User getLoggedOnUser() {
        return loggedOnUser;
    }

    public void loggOff() {
        loggedOnUser = null;
    }
}
