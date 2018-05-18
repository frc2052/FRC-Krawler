package com.team2052.frckrawler.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.team2052.frckrawler.R
import com.team2052.frckrawler.core.common.MetricHelper
import com.team2052.frckrawler.core.data.models.Metric
import com.team2052.frckrawler.data.firebase.FirebaseUtil
import com.team2052.frckrawler.data.firebase.models.FirebaseMetric
import com.team2052.frckrawler.data.firebase.models.MetricImportModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ImportMetricsActivity : DatabaseActivity(), AdapterView.OnItemClickListener {
    private var adapter: FirebaseListAdapter<MetricImportModel>? = null
    private var mListView: ListView? = null
    private var databaseReference: DatabaseReference? = null
    private var game_id: Long = 0
    private var metric_category: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_view)

        game_id = intent.getLongExtra(DatabaseActivity.PARENT_ID, 0)
        metric_category = intent.getIntExtra(METRIC_CATEGORY_EXTRA, MetricHelper.MATCH_PERF_METRICS)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        databaseReference = FirebaseUtil.getFirebaseDatabase().reference.child("match_perf")

        /*adapter = object : FirebaseListAdapter<MetricImportModel>(this, MetricImportModel::class.java, R.layout.list_item_metrics_import, databaseReference) {
            override fun populateView(v: View, model: MetricImportModel, position: Int) {
                (v.findViewById<View>(android.R.id.text1) as TextView).text = model.name
                (v.findViewById<View>(android.R.id.text2) as TextView).text = model.description
            }
        }*/
        mListView = findViewById<View>(R.id.list) as ListView
        mListView!!.adapter = adapter
        mListView!!.onItemClickListener = this
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        //adapter!!.cleanup()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        mListView!!.onItemClickListener = null

        adapter!!.getRef(position).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("WrongConstant")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(MetricImportModel::class.java)
                Observable.from<FirebaseMetric>(value!!.metrics)
                        .map<Metric> { firebaseMetric ->
                            val metricFactory = com.team2052.frckrawler.core.metrics.MetricFactory(firebaseMetric.name)
                            metricFactory.setDataRaw(firebaseMetric.data)
                            metricFactory.setGameId(game_id)
                            @MetricHelper.MetricType
                            val type = firebaseMetric.type.toInt()
                            metricFactory.setMetricType(type)
                            metricFactory.setMetricCategory(metric_category)
                            metricFactory.buildMetric()
                        }
                        .map<Metric> { metric ->
                            rxDbManager.metricsTable.insert(metric)
                            metric
                        }
                        .toList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ onNext -> finish() }) { throwable ->
                            throwable.printStackTrace()
                            FirebaseCrash.report(throwable)
                        }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        private val TAG = "ImportMetricsActivity"
        private val METRIC_CATEGORY_EXTRA = "METRIC_CATEGORY_EXTRA"

        fun newInstance(context: Context, game_id: Long, metric_category: Int): Intent {
            val intent = Intent(context, ImportMetricsActivity::class.java)
            intent.putExtra(DatabaseActivity.PARENT_ID, game_id)
            intent.putExtra(METRIC_CATEGORY_EXTRA, metric_category)
            return intent
        }
    }

}
