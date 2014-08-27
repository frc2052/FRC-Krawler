package com.team2052.frckrawler.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Adam on 8/24/2014.
 */
public class DatabaseActivity extends Activity {

    public static final String PARENTS_EXTRA = "com.team2052.frckrawler.parentsArrayExtra";
    public static final String DB_VALUES_EXTRA = "com.team2052.frckrawler.dbValsExtra";
    public static final String DB_KEYS_EXTRA = "com.team2052.frckrawler.dbKeysExtra";

    protected String[] parents;
    protected String[] databaseKeys;
    protected String[] databaseValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parents = getIntent().getStringArrayExtra(PARENTS_EXTRA);
        databaseValues = getIntent().getStringArrayExtra(DB_VALUES_EXTRA);
        databaseKeys = getIntent().getStringArrayExtra(DB_KEYS_EXTRA);

        if (parents == null)
            parents = new String[0];

        if (databaseValues == null)
            databaseValues = new String[0];

        if (databaseKeys == null)
            databaseKeys = new String[0];
    }

    /**
     * *
     * Method: getAddressoOfDatabaseKey
     *
     * @param key
     * @return Summary: returns the first address of the string passed as
     * a parameter or -1 if it wasn't in the database.
     * ***
     */
    protected int getAddressOfDatabaseKey(String key) {
        for (int i = 0; i < databaseKeys.length; i++) {
            if (databaseKeys[i].equals(key))
                return i;
        }
        return -1;
    }
}
