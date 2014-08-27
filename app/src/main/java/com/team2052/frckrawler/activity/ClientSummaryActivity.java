package com.team2052.frckrawler.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Query;

public class ClientSummaryActivity extends BaseActivity implements OnClickListener {

    private static final int COMMENT_CHAR_LIMIT = 20;
    private static final int MATCH_DATA_BUTTON_ID = 2;
    private static Query[] queries = new Query[0];

    private int dataCount = 0;
    private DBManager dbManager;
    private TableLayout table;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_summary);
    }

    @Override
    public void onClick(View v) {

    }
}
