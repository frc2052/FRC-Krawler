package com.team2052.frckrawler.binding;

import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.List;

public class ListViewBinder extends BaseDataBinder<List<ListItem>> {
    public ListView listView;
    public View noDataRootView;
    public ImageView noDataImage;
    public TextView noDataTitle;

    ListViewNoDataParams noDataParams;

    @Override
    public void updateData(List<ListItem> data) {
        if (data == null || listView == null || data.isEmpty()) {
            showNoData(true);
            return;
        }
        ListViewAdapter adapter = new ListViewAdapter(mActivity, data);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        showNoData(false);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void bindViews() {

    }

    public void showNoData(boolean shown) {
        if (shown) {
            noDataImage.setImageResource(noDataParams.getDrawable());
            noDataTitle.setText(noDataParams.getTitle());

            listView.setVisibility(View.GONE);
            noDataRootView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            noDataRootView.setVisibility(View.GONE);
        }
    }

    public void setNoDataParams(ListViewNoDataParams noDataParams) {
        this.noDataParams = noDataParams;
    }

    public static class ListViewNoDataParams {
        private String mTitle;
        private int mDrawable;

        public ListViewNoDataParams(String mTitle, @DrawableRes int drawableId) {
            this.mTitle = mTitle;
            this.mDrawable = drawableId;
        }

        public String getTitle() {
            return mTitle;
        }

        public int getDrawable() {
            return mDrawable;
        }
    }
}
