package com.team2052.frckrawler.core.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.melnykov.fab.FloatingActionButton;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.PickListListElement;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.PickList;

import java.util.List;

/**
 * Created by adam on 3/29/15.
 */
public class PicklistActivity extends DatabaseActivity implements View.OnClickListener {

    private Event mEvent;
    private FloatingActionButton mFab;
    private ListView mList;

    public static Intent newInstance(Context context, Event event) {
        Intent intent = new Intent(context, PicklistActivity.class);
        intent.putExtra(PARENT_ID, (long) event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = mDbManager.getDaoSession().getEventDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        initViews();
        setActionBarTitle("Picklist");
        setActionBarSubtitle(mEvent.getName());
        Log.d("FRCKrawler", String.valueOf(mEvent == null));
    }

    private void initViews() {
        setContentView(R.layout.activity_picklist);
        mList = (ListView) findViewById(R.id.pick_list_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.attachToListView(mList);
        mFab.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        load_list();
    }

    private void load_list() {
        new LoadPickLists().execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            EditText e = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(e);
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Add", (dialog, which) -> {
                String s = e.getText().toString();
                PickList pickList = new PickList(null, s, mEvent.getId(), null);
                mDbManager.getDaoSession().insert(pickList);
                load_list();
            });
            builder.create().show();
        }
    }

    public class LoadPickLists extends AsyncTask<Void, Void, List<PickList>> {

        @Override
        protected List<PickList> doInBackground(Void... params) {
            mEvent.resetPickListList();
            return mEvent.getPickListList();

        }

        @Override
        protected void onPostExecute(List<PickList> pickLists) {
            List<ListItem> listItems = Lists.newArrayList();
            for (PickList pickList : pickLists) {
                listItems.add(new PickListListElement(pickList));
            }

            mList.setAdapter(new ListViewAdapter(PicklistActivity.this, listItems));
        }
    }
}
