package com.team2052.frckrawler.di.binding;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.adapters.items.ListItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewBinder extends BaseDataBinder<List<ListItem>> {
    @BindView(R.id.list)
    public ListView listView;
    @BindView(R.id.no_data_root_view)
    public View noDataRootView;
    @BindView(R.id.no_data_image)
    public ImageView noDataImage;
    @BindView(R.id.no_data_title)
    public TextView noDataTitle;

    NoDataParams noDataParams;

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
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
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

    public void setNoDataParams(NoDataParams noDataParams) {
        this.noDataParams = noDataParams;
    }

}
