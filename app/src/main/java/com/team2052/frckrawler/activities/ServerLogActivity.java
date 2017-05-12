package com.team2052.frckrawler.activities;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.KeyValueListElement;
import com.team2052.frckrawler.theme.Themes;

import rx.Observable;

public class ServerLogActivity extends DatabaseActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Themes.getCurrentTheme(this).getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mListView = (ListView) findViewById(R.id.list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rxDbManager.getServerLog()
                .flatMap(Observable::from)
                .map(entry -> {
                    String time = DateFormat.getTimeFormat(this).format(entry.getTime());
                    return (ListItem) new KeyValueListElement(time, entry.getMessage());
                })
                .toList()
                .subscribe(listItems -> {
                    ListViewAdapter listViewAdapter = new ListViewAdapter(this, listItems);
                    mListView.setAdapter(listViewAdapter);
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }
}
