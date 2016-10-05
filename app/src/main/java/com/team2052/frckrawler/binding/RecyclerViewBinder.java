package com.team2052.frckrawler.binding;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.team2052.frckrawler.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;

public class RecyclerViewBinder extends BaseDataBinder<List<Object>> {
    @BindView(R.id.no_data_root_view)
    public View noDataRootView;
    @BindView(R.id.no_data_image)
    public ImageView noDataImage;
    @BindView(R.id.no_data_title)
    public TextView noDataTitle;
    RecyclerViewAdapterCreatorProvider mProvider;
    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    RecyclerMultiAdapter mAdapter;
    private ListViewNoDataParams noDataParams;

    @Override
    public void updateData(List<Object> data) {
        if (data == null || data.isEmpty()) {
            showNoData(true);
            return;
        }
        SmartAdapter.MultiAdaptersCreator creator = SmartAdapter.items(new ArrayList<>(data));
        mProvider.provideAdapterCreator(creator);
        mAdapter = creator.into(mRecyclerView);
        showNoData(false);
    }

    public void setRecyclerViewAdapterCreatorProvider(RecyclerViewAdapterCreatorProvider mProvider) {
        this.mProvider = mProvider;
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
    }

    public void showNoData(boolean shown) {
        if (shown) {
            noDataImage.setImageResource(noDataParams.getDrawable());
            noDataTitle.setText(noDataParams.getTitle());

            mRecyclerView.setVisibility(View.GONE);
            noDataRootView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noDataRootView.setVisibility(View.GONE);
        }
    }

    public void setNoDataParams(ListViewNoDataParams noDataParams) {
        this.noDataParams = noDataParams;
    }

    public interface RecyclerViewAdapterCreatorProvider {
        void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator);
    }
}
