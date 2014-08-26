package com.team2052.frckrawler.activity.dialog;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.SummaryActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.database.structures.SortKey;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.QueryWidget;

import java.util.ArrayList;

public class QuerySortingDialogActivity extends Activity implements OnClickListener {

    public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventID";

    private DBManager dbManager;
    private QueryWidget matchQueryWidget;
    private QueryWidget pitQueryWidget;
    private volatile Event event;
    private volatile Metric[] matchMetrics;
    private volatile Metric[] pitMetrics;
    private ArrayList<Metric> sortMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_query_and_sorting);

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

        dbManager = DBManager.getInstance(this);
        sortMetrics = new ArrayList<Metric>();

        new GetDataTask().execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel) {
            setResult(SummaryActivity.REQUEST_NO_REFRESH);
            finish();

        } else {
            SummaryActivity.setQuery(Integer.parseInt(getIntent().getStringExtra
                            (EVENT_ID_EXTRA)), matchQueryWidget.getQuerys(),
                    pitQueryWidget.getQuerys(), new Query[0]
            );

            int itemPos = ((Spinner) findViewById(R.id.sortKey)).getSelectedItemPosition();
            if (itemPos != 0) {
                int metricType;
                if (itemPos == 1) {
                    metricType = SortKey.OPR_TYPE;
                } else if (itemPos < matchMetrics.length + 1)
                    metricType = SortKey.MATCH_METRIC_TYPE;
                else
                    metricType = SortKey.PIT_METRIC_TYPE;

                if (metricType != SortKey.OPR_TYPE)
                    SummaryActivity.setSortKey(new SortKey(metricType,
                            sortMetrics.get(itemPos - 2).getID()));
                else
                    SummaryActivity.setSortKey(new SortKey(metricType, -1));

            } else
                SummaryActivity.setSortKey(null);

            setResult(SummaryActivity.REQUEST_REFRESH);
            finish();
        }
    }


    /**
     * **
     * Class: GetMetricsTask
     */
    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            event = dbManager.getEventsByColumns
                    (new String[]{DBContract.COL_EVENT_ID},
                            new String[]{getIntent().getStringExtra
                                    (EVENT_ID_EXTRA)}
                    )[0];
            matchMetrics = dbManager.getMatchPerformanceMetricsByColumns
                    (new String[]{DBContract.COL_GAME_NAME},
                            new String[]{event.getGameName()});
            pitMetrics = dbManager.getRobotMetricsByColumns
                    (new String[]{DBContract.COL_GAME_NAME},
                            new String[]{event.getGameName()});
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            LinearLayout list = (LinearLayout) findViewById(R.id.queryListList);
            list.removeAllViews();

            list.addView(new MyTextView(getApplicationContext(),
                    "Query by Match Metrics", 18));
            matchQueryWidget = new QueryWidget(getApplicationContext(),
                    matchMetrics, SummaryActivity.getMatchQuerys
                    (Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA))),
                    Query.TYPE_MATCH_DATA
            );
            list.addView(matchQueryWidget);

            list.addView(new MyTextView(getApplicationContext(),
                    "Query by Robot Metrics", 18));
            pitQueryWidget = new QueryWidget(getApplicationContext(),
                    pitMetrics, SummaryActivity.getPitQuerys
                    (Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA))),
                    Query.TYPE_ROBOT
            );
            list.addView(pitQueryWidget);

            for (int i = 0; i < matchMetrics.length; i++) {
                if (matchMetrics[i].getType() == DBContract.COUNTER ||
                        matchMetrics[i].getType() == DBContract.MATH ||
                        matchMetrics[i].getType() == DBContract.BOOLEAN ||
                        matchMetrics[i].getType() == DBContract.SLIDER ||
                        (matchMetrics[i].getType() == DBContract.CHOOSER &&
                                matchMetrics[i].isNumericChooser()))
                    sortMetrics.add(matchMetrics[i]);
            }

            for (int i = 0; i < pitMetrics.length; i++) {
                if (pitMetrics[i].getType() == DBContract.COUNTER ||
                        pitMetrics[i].getType() == DBContract.MATH ||
                        pitMetrics[i].getType() == DBContract.SLIDER ||
                        (pitMetrics[i].getType() == DBContract.CHOOSER &&
                                pitMetrics[i].isNumericChooser()))
                    sortMetrics.add(pitMetrics[i]);
            }

            ArrayAdapter<String> sortChoices = new ArrayAdapter<String>
                    (QuerySortingDialogActivity.this, R.layout.scout_spinner_item);
            sortChoices.add("Team Number");
            sortChoices.add("OPR");

            int selectedPos = -1;
            for (int i = 0; i < sortMetrics.size(); i++) {
                sortChoices.add(sortMetrics.get(i).getMetricName());
                if (SummaryActivity.getSortKey() != null &&
                        sortMetrics.get(i).getID() == SummaryActivity.getSortKey().getMetricID())
                    selectedPos = i + 2;
            }
            ((Spinner) findViewById(R.id.sortKey)).setAdapter(sortChoices);
            if (SummaryActivity.getSortKey() != null &&
                    SummaryActivity.getSortKey().getMetricType() == SortKey.OPR_TYPE)
                ((Spinner) findViewById(R.id.sortKey)).setSelection(1);
            else if (selectedPos != -1)
                ((Spinner) findViewById(R.id.sortKey)).setSelection(selectedPos);
        }
    }
}
