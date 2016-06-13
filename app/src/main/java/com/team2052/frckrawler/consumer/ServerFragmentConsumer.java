package com.team2052.frckrawler.consumer;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.team2052.frckrawler.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerFragmentConsumer extends DataConsumer<List<String>> {
    @BindView(R.id.view_event) Button viewEventButton;
    @BindView(R.id.scout_match_button) Button scoutMatchButton;
    @BindView(R.id.scout_pit_button) Button scoutPitButton;
    @BindView(R.id.scout_practice_button) Button scoutPracticeButton;
    @BindView(R.id.event_spinner) public Spinner eventSpinner;

    @BindView(R.id.server_event_container) View mServerEventContainer;
    @BindView(R.id.server_events_error) View mServerEventsError;
    @BindView(R.id.scout_server_card) View mScoutServerCard;

    @Override
    public void updateData(List<String> data) {
        if (data == null || eventSpinner == null)
            return;
        if (data.isEmpty()) {
            showData(false);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, data);
        eventSpinner.setAdapter(adapter);

        showData(true);
    }

    private void showData(boolean show){
        if (show) {
            mServerEventContainer.setVisibility(View.VISIBLE);
            mScoutServerCard.setVisibility(View.VISIBLE);
            mServerEventsError.setVisibility(View.GONE);
        } else {
            mServerEventContainer.setVisibility(View.GONE);
            mScoutServerCard.setVisibility(View.GONE);
            mServerEventsError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, rootView);
    }
}
