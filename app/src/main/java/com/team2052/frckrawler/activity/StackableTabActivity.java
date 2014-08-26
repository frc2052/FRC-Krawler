package com.team2052.frckrawler.activity;

import android.os.Bundle;
@Deprecated
public class StackableTabActivity extends TabActivity {

    public static final String PARENTS_EXTRA = "com.team2052.frckrawler.parentsArrayExtra";
    public static final String DB_VALUES_EXTRA = "com.team2052.frckrawler.dbValsExtra";
    public static final String DB_KEYS_EXTRA = "com.team2052.frckrawler.dbKeysExtra";

    protected String[] parents;
    protected String[] databaseValues;
    protected String[] databaseKeys;

    @Override
    public void onCreate(Bundle savedInstanceState) {

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

        setNoRootActivitySelected();
    }

    @Override
    public void onStart() {

        super.onStart();

        /*****
         * CHARLIE! YOU MAY WANT TO FIX THIS, BUT IT IS ONLY FOR COSMETICS.
         */
        /*LinearLayout l = (LinearLayout)findViewById(R.id.treeView);
		
		if(l != null) {
			
			l.removeAllViews();
			
			for(String s : parents)
				l.addView(new SidewaysTextView(this, s));
		
		} else {
			System.out.println("WARNING! A StackableTabActivity's treeView has not been defined.");
		}*/
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
