package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.tab.MetricInfoPagerAdapter
import com.team2052.frckrawler.bindToViewPagerAndTabLayout
import com.team2052.frckrawler.models.Metric
import com.team2052.frckrawler.wrapAsObservable
import kotlinx.android.synthetic.main.layout_tab_fab.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MetricInfoActivity : DatabaseActivity() {
    lateinit var mMetric: Metric

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tab_fab)
        val metricId = intent.getLongExtra(METRIC_ID, 0)

        val metric = rxDbManager.metricsTable.load(metricId)

        if (metric == null) {
            finish()
            return
        }

        mMetric = metric

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        MetricInfoPagerAdapter(supportFragmentManager, metric.id).bindToViewPagerAndTabLayout(view_pager, tab_layout)
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_delete_menu, menu)
        //Remove edit menu item so we don't confuse our users for the time being
        menu.removeItem(R.id.menu_edit)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        /*if (item.getItemId() == R.typeId.menu_edit) {
            EditMetricDialogFragment.newInstance(metric).show(getSupportFragmentManager(), "editMetric");
        } else*/
        if (item.itemId == R.id.menu_delete) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Metric?")
            builder.setMessage("Are you sure you want to delete this metric? You will lose all data associated with this metric.")
            builder.setPositiveButton("Delete") { _, _ ->
                { rxDbManager.metricsTable.delete(mMetric) }.wrapAsObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { finish() }
            }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val METRIC_ID = "METRIC_ID"

        fun newInstance(context: Context, metric_id: Long): Intent {
            val intent = Intent(context, MetricInfoActivity::class.java)
            intent.putExtra(METRIC_ID, metric_id)
            return intent
        }
    }
}
