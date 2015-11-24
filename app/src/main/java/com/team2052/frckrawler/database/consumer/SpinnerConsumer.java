package com.team2052.frckrawler.database.consumer;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by Adam on 11/24/2015.
 */
public class SpinnerConsumer extends DataConsumer<List<String>> {
    public Spinner mSpinner;

    @Override
    public void updateData(List<String> data) {
        if (data == null || mSpinner == null)
            return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, data);
        mSpinner.setAdapter(adapter);
    }
}
