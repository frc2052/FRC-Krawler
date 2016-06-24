package com.team2052.frckrawler.binding;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by Adam on 11/24/2015.
 */
public class SpinnerBinder extends BaseDataBinder<List<String>> {
    public Spinner mSpinner;
    public NoDataHandler noDataHandler;

    @Override
    public void updateData(List<String> data) {
        if (data == null || mSpinner == null)
            return;
        if (data.isEmpty() && noDataHandler != null) {
            noDataHandler.noData(this);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, data);
        mSpinner.setAdapter(adapter);

        if (noDataHandler != null)
            noDataHandler.showData();
    }

    @Override
    public void bindViews() {

    }
}
