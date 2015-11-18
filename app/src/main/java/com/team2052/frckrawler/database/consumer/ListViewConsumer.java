package com.team2052.frckrawler.database.consumer;

import android.widget.ListView;

import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.List;

public class ListViewConsumer extends DataComsumer<List<ListItem>> {
    public ListView listView;

    @Override
    public void updateData(List<ListItem> data) {
        if (data == null || listView == null)
            return;
        ListViewAdapter adapter = new ListViewAdapter(mActivity, data);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable e) {

    }
}
