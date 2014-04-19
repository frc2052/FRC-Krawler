package com.team2052.frckrawler;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class ClientSummaryActivity extends Activity implements OnClickListener {
	
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
