package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.firebasemodels.MetricImportModel;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImportMetricsActivity extends DatabaseActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "ImportMetricsActivity";
    private static final String METRIC_CATEGORY_EXTRA = "METRIC_CATEGORY_EXTRA";
    private FirebaseListAdapter<MetricImportModel> adapter;
    private ListView mListView;
    private DatabaseReference databaseReference;
    private long game_id;
    private int metric_category;

    public static Intent newInstance(Context context, long game_id, int metric_category) {
        Intent intent = new Intent(context, ImportMetricsActivity.class);
        intent.putExtra(PARENT_ID, game_id);
        intent.putExtra(METRIC_CATEGORY_EXTRA, metric_category);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        game_id = getIntent().getLongExtra(PARENT_ID, 0);
        metric_category = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, MetricHelper.MATCH_PERF_METRICS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("match_perf");

        adapter = new FirebaseListAdapter<MetricImportModel>(this, MetricImportModel.class, R.layout.list_item_metrics_import, databaseReference) {
            @Override
            protected void populateView(View v, MetricImportModel model, int position) {
                ((TextView) v.findViewById(android.R.id.text1)).setText(model.name);
                ((TextView) v.findViewById(android.R.id.text2)).setText(model.description);
            }
        };
        mListView = (ListView) findViewById(R.id.list_layout);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListView.setOnItemClickListener(null);

        adapter.getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final MetricImportModel value = dataSnapshot.getValue(MetricImportModel.class);
                Observable.from(value.metrics)
                        .map(firebaseMetric -> {
                            final MetricHelper.MetricFactory metricFactory = new MetricHelper.MetricFactory(game_id, firebaseMetric.name);
                            metricFactory.setDataRaw(firebaseMetric.data);
                            @MetricHelper.MetricType
                            int type = firebaseMetric.type.intValue();
                            metricFactory.setMetricType(type);
                            metricFactory.setMetricCategory(metric_category);
                            return metricFactory.buildMetric();
                        })
                        .map(metric -> {
                            dbManager.getMetricsTable().insert(metric);
                            return metric;
                        })
                        .toList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> {
                            finish();
                        }, FirebaseCrash::report);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
