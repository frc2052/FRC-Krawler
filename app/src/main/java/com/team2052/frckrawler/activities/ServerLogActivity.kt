package com.team2052.frckrawler.activities

import android.os.Bundle
import android.text.format.DateFormat
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.ListViewAdapter
import com.team2052.frckrawler.adapters.items.ListItem
import com.team2052.frckrawler.adapters.items.elements.KeyValueListElement
import com.team2052.frckrawler.core.data.models.ServerLogEntry
import rx.Observable

class ServerLogActivity : DatabaseActivity() {

    private var mListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        mListView = findViewById<View>(R.id.list) as ListView

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        rxDbManager.serverLog
                .flatMap<ServerLogEntry> { Observable.from(it) }
                .map { entry ->
                    val time = DateFormat.getMediumDateFormat(this).format(entry.time)
                    KeyValueListElement(time, entry.message)
                }
                .toList()
                .subscribe { listItems ->
                    val listViewAdapter = ListViewAdapter(this, listItems as List<ListItem>?)
                    mListView!!.adapter = listViewAdapter
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun inject() {
        component.inject(this)
    }
}
